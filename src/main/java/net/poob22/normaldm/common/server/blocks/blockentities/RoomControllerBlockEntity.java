package net.poob22.normaldm.common.server.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.DungeonGateBlock;
import net.poob22.normaldm.common.server.blocks.DungeonMobSpawnerBlock;
import net.poob22.normaldm.common.server.blocks.properties.GateState;
import net.poob22.normaldm.common.server.blocks.properties.RoomDefinition;
import net.poob22.normaldm.common.server.blocks.properties.RoomDefinitions;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

public class RoomControllerBlockEntity extends BlockEntity {
    Logger LOG = NormalDungeonMod.LOGGER;

    public int tickCount;
    private static final int CHECK_INTERVAL = 10;
    private static final int GATE_CHECK_INTERVAL = 60;
    List<AABB> roomBounds;
    List<AABB> playerRoomBounds;

    // default room type
    public RoomDefinition RoomLayout = RoomDefinitions.SMALL;

    public enum RoomState {
        DORMANT,
        ACTIVE,
        CLEARED,
        FAILED
    }

    private RoomState state = RoomState.DORMANT;

    public RoomControllerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(NDMBlockEntities.ROOM_CONTROLLER.get(), pPos, pBlockState);
    }

    Set<UUID> EnemiesInRoom = new HashSet<>();
    List<Player> PlayersInRoom = new ArrayList<>();
    Set<BlockPos> GatesInRoom = new HashSet<>();

    public static void tick(Level level, BlockPos pos, BlockState state, RoomControllerBlockEntity entity) {
        entity.tickCount++;
        entity.initBounds();

        if(!level.isClientSide) {
            if(entity.tickCount % CHECK_INTERVAL == 0){
                switch(entity.state) {
                    case DORMANT:
                        entity.dormantState(level);
                        break;

                    case ACTIVE:
                        entity.checkClearRoom(level);
                        entity.checkFailRoom();
                        break;

                    case CLEARED: break;

                    case FAILED:
                }
            }

            if(entity.tickCount % GATE_CHECK_INTERVAL == 0){
                entity.getGatesInRoom(level);
            }
        }
    }

    public void initBounds() {
        roomBounds = RoomLayout.createRoomBounds(this.getBlockPos(), false);
        playerRoomBounds = RoomLayout.createRoomBounds(this.getBlockPos(), true);
    }

    /// STATE METHODS ///

    public void dormantState(Level level) {
        getPlayersInRoom(level);
        if(!PlayersInRoom.isEmpty()) {
            setState(RoomState.ACTIVE);
        }
    }

    public void activateRoom(Level level) {
        if(!level.isClientSide) {
            for(Player player : PlayersInRoom){
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            }
            lockGates(level);
            spawnEnemies();
            ServerLevel l = (ServerLevel)level;
            checkEnemiesInRoom(l);

            LOG.info("ROOM HAS BEEN ACTIVATED");
        }
    }

    public void checkClearRoom(Level level) {
        checkEnemiesInRoom((ServerLevel) level);
        if(EnemiesInRoom.isEmpty()){
            setState(RoomState.CLEARED);
        }
    }

    private void clearRoom(Level level) {
        unlockGates(level);
        level.playSound(null, this.getBlockPos(), SoundEvents.WITHER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f);
        // other feedback responses

    }

    public void checkFailRoom() {
        checkPlayersInRoom();
        if (PlayersInRoom.isEmpty()) {
            setState(RoomState.FAILED);
        }
    }

    private void failRoom(Level level) {
        for(UUID id : EnemiesInRoom) {
            ServerLevel l = (ServerLevel) level;
            Entity e = l.getEntity(id);
            if(e != null) e.remove(Entity.RemovalReason.DISCARDED);
        }
        unlockGates(level);
    }

    private void setState(RoomState newState) {
        if(state == newState) return;

        this.state = newState;

        setChanged();

        if(level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }

        onStateChanged(newState);
    }

    private void onStateChanged(RoomState newState) {
        if(level != null) {
            switch(newState) {
                case ACTIVE -> activateRoom(level);
                case CLEARED -> clearRoom(level);
                case FAILED -> failRoom(level);
                default -> {}
            }
        }
    }

    /// UTIL METHODS ///

    public void getSpawnedEnemiesInRoom(Level level) {
        List<DungeonMob> Enemies = new ArrayList<>();
        for(AABB box : roomBounds) {
            Enemies.add((DungeonMob) level.getEntitiesOfClass(DungeonMob.class, box));
        }

        for(DungeonMob e : Enemies) {
            UUID id = e.getUUID();
            e.setInDungeon(true);
            EnemiesInRoom.add(id);
        }

        LOG.info(EnemiesInRoom.size() + " Enemies Detected in Room");
    }

    private void checkEnemiesInRoom(ServerLevel level) {
        if (!EnemiesInRoom.isEmpty()) {
            EnemiesInRoom.removeIf(id -> {
                Entity e = level.getEntity(id);
                if(e instanceof DungeonMob) {
                    boolean flag = true;
                    for(AABB box : roomBounds) {
                        flag = box.intersects(e.getBoundingBox());
                    }
                    if(!e.isAlive() || !flag) {
                        ((DungeonMob) e).setInDungeon(false);
                        NormalDungeonMod.LOGGER.info("Enemy either died or left room bounds, will not be added back to the room");
                        return true;
                    }
                    return false;
                } else {
                    return true;
                }
            });
        }
        getSpawnedEnemiesInRoom(level);
    }

    public void getPlayersInRoom(Level level) {
        List<Player> p = new ArrayList<>();
        for(AABB box : playerRoomBounds) {
            p.addAll(level.getEntitiesOfClass(Player.class, box, player -> player.isAlive() && !player.isSpectator() && !player.isCreative() && player.isAlive()));
        }
        PlayersInRoom.clear();
        PlayersInRoom.addAll(p);
    }

    private void checkPlayersInRoom() {
        PlayersInRoom.removeIf(e -> e == null || !isPlayerInVolumes(e) || !e.isAlive() || e.isSpectator() || e.isCreative());
    }

    protected boolean isPlayerInVolumes(Player player) {
        for(AABB box : roomBounds) {
            if(box.intersects(player.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    public List<AABB> getRoomBounds() {
        return roomBounds;
    }
    public List<AABB> getPlayerRoomBounds() {
        return playerRoomBounds;
    }
    public RoomState getRoomState() {
        return state;
    }

    public void setRoomLayout(RoomDefinition type) {
        this.RoomLayout = type;
        setChanged();
        if(level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void lockGates(Level level) {
        for(BlockPos pos : GatesInRoom) {
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof DungeonGateBlock gate) {
                gate.setGateState(level, pos, state, GateState.LOCKED);
            }
        }
    }

    private void unlockGates(Level level) {
        for(BlockPos pos : GatesInRoom) {
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof DungeonGateBlock gate) {
                if(!gate.getHasBeenOpened(state))
                    gate.setGateState(level, pos, state, GateState.CLOSED);
                else
                    gate.setGateState(level, pos, state, GateState.OPENING);
            }
        }
    }

    private void getGatesInRoom(Level level) {
        GatesInRoom.clear();

        BlockPos origin = this.getBlockPos();
        for()
        BlockPos min = origin.offset(this.RoomLayout.getMinOfVolumes());
        BlockPos max = origin.offset(this.RoomLayout.getMaxOfVolumes());
        for(BlockPos pos : BlockPos.betweenClosed(min, max)){
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof DungeonGateBlock gate) {
                if(DungeonGateBlock.isLowerHalf(state)) {
                    GatesInRoom.add(pos.immutable());
                }
            }
        }
        NormalDungeonMod.LOGGER.info("Gates in Room: {}", GatesInRoom.size());
    }

    private void spawnEnemies() {
        BlockPos origin = this.getBlockPos();
        BlockPos min = origin.offset(this.RoomLayout.getMinOfVolumes());
        BlockPos max = origin.offset(this.RoomLayout.getMaxOfVolumes());

        if(this.level != null && !this.level.isClientSide){
            for(BlockPos pos : BlockPos.betweenClosed(min, max)) {
                if(this.level.getBlockState(pos).getBlock() instanceof DungeonMobSpawnerBlock) {
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if(blockEntity instanceof DungeonMobSpawner spawner) {
                        NormalDungeonMod.LOGGER.info("Spawning DungeonMobSpawner at " + pos + " | Spawned entity: " + spawner.getMobToSpawn());
                        spawner.spawnMob();
                    }
                }
            }
        }
    }

    /// NBT HELPERS ///

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // save room state
        RoomState s = state;
        tag.putInt("roomState", s.ordinal());
        if(this.RoomLayout != null) {
            tag.putString("roomType", RoomLayout.toString());
        }

        saveUUIDSet(tag, "enemies", EnemiesInRoom);
        saveBlockPosSet(tag, "gatePos", GatesInRoom);
        // add save data for chests later
    }

    private void saveUUIDSet(CompoundTag tag, String key, Set<UUID> set) {
        ListTag list = new ListTag();
        for(UUID id : set) {
            CompoundTag e = new CompoundTag();
            e.putUUID("enemyId", id);
            list.add(e);
        }
        tag.put(key, list);
    }

    private void saveBlockPosSet(CompoundTag tag, String key, Set<BlockPos> set) {
        ListTag list = new ListTag();
        for(BlockPos pos : set) {
            CompoundTag e = new CompoundTag();
            e.put("pos", NbtUtils.writeBlockPos(pos));
            list.add(e);
        }
        tag.put(key, list);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // load room state
        int s = tag.getInt("roomState");
        if (s >= 0 && s < RoomState.values().length) {
            state = RoomState.values()[s];
        } else {
            LOG.error("Invalid Room State Loaded...Reverting To DORMANT");
            state = RoomState.DORMANT;
        }
        this.setRoomLayout(RoomDefinitions.get(tag.getString("roomType")));

        loadUUIDSet(tag, "enemies", EnemiesInRoom);
        loadBlockPosSet(tag, "gatePos", GatesInRoom);
        NormalDungeonMod.LOGGER.info("Loaded {} gates in room", GatesInRoom.size());
    }

    private void loadUUIDSet(CompoundTag tag, String key, Set<UUID> set) {
        set.clear();
        if(tag.contains(key, ListTag.TAG_LIST)) {
            ListTag list = tag.getList(key, ListTag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); ++i) {
                CompoundTag e = (CompoundTag) list.get(i);
                UUID id = e.getUUID("enemyId");
                set.add(id);
            }
        }
    }

    private void loadBlockPosSet(CompoundTag tag, String key, Set<BlockPos> set) {
        set.clear();
        if(tag.contains(key, ListTag.TAG_LIST)) {
            ListTag list = tag.getList(key, ListTag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); ++i) {
                CompoundTag e = (CompoundTag) list.get(i);
                BlockPos pos = NbtUtils.readBlockPos(e.getCompound("pos"));
                set.add(pos);
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
