package net.poob22.normaldm.common.server.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.DungeonMobSpawnerBlock;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.slf4j.Logger;

import java.util.*;

public class RoomControllerBlockEntity extends BlockEntity {
    Logger LOG = NormalDungeonMod.LOGGER;
    public boolean showBounds = false;

    public int tickCount;
    private static final int CHECK_INTERVAL = 10;
    AABB roomBounds;
    AABB playerRoomBounds;

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
    Set<BlockPos> DoorsInRoom = new HashSet<>();
    public static Set<BlockPos> LOCKED_DOORS = new HashSet<>();

    public static void tick(Level level, BlockPos pos, BlockState state, RoomControllerBlockEntity entity) {
        entity.tickCount++;
        entity.initBounds();

        if(!level.isClientSide) {
            if(entity.tickCount % CHECK_INTERVAL == 0){
                switch(entity.state) {
                    case DORMANT:
                        //NormalDungeonMod.LOGGER.info("Room is Dormant, checking for players...");
                        entity.dormantState(level);
                        break;

                    case ACTIVE:
                        // check for enemies cleared
                        entity.checkClearRoom(level);
                        entity.checkFailRoom();
                        break;

                    case CLEARED: break;

                    case FAILED:
                }
            }
        }
    }

    private void initBounds() {
        if(roomBounds == null) {
            roomBounds = new AABB(this.getBlockPos()).inflate(10);
            playerRoomBounds = roomBounds.deflate(1);
        }
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
            lockDoors(level);
            spawnEnemies();
            getSpawnedEnemiesInRoom(level);

            LOG.info("ROOM HAS BEEN ACTIVATED");
        }
    }

    public void checkClearRoom(Level level) {
        checkEnemiesInRoom((ServerLevel) level);

        // room cleared
        if(EnemiesInRoom.isEmpty()){
            setState(RoomState.CLEARED);
        }
    }

    private void clearRoom(Level level) {
        unlockDoors(level);
        level.playSound(null, this.getBlockPos(), SoundEvents.WITHER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f);
        // other feedback responses

        LOG.info("ROOM HAS BEEN CLEARED");
    }

    public void checkFailRoom() {
        checkPlayersInRoom();
        if (PlayersInRoom.isEmpty()) {
            setState(RoomState.FAILED);
            LOG.info("ROOM HAS BEEN FAILED");
        }
    }

    private void failRoom(Level level) {
        for(UUID id : EnemiesInRoom) {
            ServerLevel l = (ServerLevel) level;
            Entity e = l.getEntity(id);
            if(e != null) e.remove(Entity.RemovalReason.DISCARDED);
        }
        unlockDoors(level);
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
        List<DungeonMob> Enemies = level.getEntitiesOfClass(DungeonMob.class, getRoomBounds());

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
                    if(!e.isAlive() || !roomBounds.intersects(e.getBoundingBox())) {
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
    }

    public void getPlayersInRoom(Level level) {
        List<Player> p = level.getEntitiesOfClass(Player.class, getPlayerRoomBounds(), player -> !player.isSpectator() && !player.isCreative() && player.isAlive());
        PlayersInRoom.clear();
        PlayersInRoom.addAll(p);
    }

    private void checkPlayersInRoom() {
        PlayersInRoom.removeIf(e -> e == null || !e.getBoundingBox().intersects(getRoomBounds()) || !e.isAlive() || e.isSpectator() || e.isCreative());
    }

    private void setRoomBounds(AABB aabb) {
        roomBounds = aabb;
    }
    public AABB getRoomBounds() {
        return roomBounds;
    }
    private void setPlayerRoomBounds(AABB aabb) {
        playerRoomBounds = aabb;
    }
    public AABB getPlayerRoomBounds() {
        return playerRoomBounds;
    }
    public RoomState getRoomState() {
        return state;
    }
    private void setRoomState(RoomState state) {
        this.state = state;
    }

    private void lockDoors(Level level) {
        for(Player player : PlayersInRoom){
            player.sendSystemMessage(Component.literal("Doors have been 'locked'"));
        }

        DoorsInRoom.clear();
        BlockPos min = this.getBlockPos().offset(-10, -5, -10);
        BlockPos max = this.getBlockPos().offset(10, 5, 10);
        for(BlockPos pos : BlockPos.betweenClosed(min, max)){
            BlockState state = level.getBlockState(pos);
            if(state.getBlock() instanceof DoorBlock door) {
                door.setOpen(null, level, state, pos, false);
                if(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                    LOG.info("Door found at " + pos);
                    BlockPos immutable = pos.immutable();
                    DoorsInRoom.add(immutable);
                    LOCKED_DOORS.add(immutable);
                }
            }
        }

        LOG.info(LOCKED_DOORS.size() + " doors have been locked");
    }

    private void unlockDoors(Level level) {
        for(Player player : PlayersInRoom){
            player.sendSystemMessage(Component.literal("Doors have been 'unlocked'"));
        }

        LOCKED_DOORS.removeAll(DoorsInRoom);
        DoorsInRoom.clear();

        // going to have to add particles later using packets (for chest spawn)
    }

    private void spawnEnemies() {
        BlockPos min = this.getBlockPos().offset(-10, -5, -10);
        BlockPos max = this.getBlockPos().offset(10, 5, 10);

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
        NormalDungeonMod.LOGGER.info("Saving Room State Ordinal: " + s.ordinal() + ", " + RoomState.values()[s.ordinal()]);

        saveUUIDSet(tag, "enemies", EnemiesInRoom);
        saveBlockPosSet(tag, "doorPos", DoorsInRoom);
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

        loadUUIDSet(tag, "enemies", EnemiesInRoom);
        loadBlockPosSet(tag, "doorPos", DoorsInRoom);
        LOCKED_DOORS.addAll(DoorsInRoom);

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
}
