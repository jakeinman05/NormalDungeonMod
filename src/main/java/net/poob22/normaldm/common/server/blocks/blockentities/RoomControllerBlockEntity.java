package net.poob22.normaldm.common.server.blocks.blockentities;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.DungeonGateBlock;
import net.poob22.normaldm.common.server.blocks.DungeonMobSpawnerBlock;
import net.poob22.normaldm.common.server.blocks.NDMBlocks;
import net.poob22.normaldm.common.server.blocks.properties.GateState;
import net.poob22.normaldm.common.server.blocks.properties.RoomDefinition;
import net.poob22.normaldm.common.server.blocks.properties.RoomDefinitions;
import net.poob22.normaldm.common.server.blocks.properties.RoomVolume;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class RoomControllerBlockEntity extends BlockEntity {
    Logger LOG = NormalDungeonMod.LOGGER;

    public int tickCount;
    private static final int CHECK_INTERVAL = 10;
    List<RoomVolume> roomBounds;

    public boolean roomSpawned = false;

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

        if(!level.getGameRules().getBoolean(NormalDungeonMod.ALLOW_ROOM_CONTROLLER_FUNCTION)) {
            return;
        }

        if(entity.roomBounds == null) {
            entity.initBounds();
        }

        if(!level.isClientSide) {
            if(!entity.hasSpawned() && level instanceof ServerLevel sl) {
                entity.setHasSpawned();
                entity.setChanged();

                BlockPos spawnPos = entity.getBlockPos();
                BlockPos spawnOffset = entity.getRoomSpawnOffset();

                entity.spawnRoom(sl, spawnPos, spawnOffset);
            }

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
        }
    }

    public void initBounds() {
        roomBounds = RoomLayout.getVolumes();
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
            getGatesInRoom(level);
            lockGates(level);
            spawnEnemies();
            ServerLevel l = (ServerLevel)level;
            checkEnemiesInRoom(l);
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

        if(level instanceof ServerLevel l) {
            spawnRewards(l);
        }
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
        for(RoomVolume v : roomBounds) {
            Enemies.addAll(level.getEntitiesOfClass(DungeonMob.class, v.toAABB(this.getBlockPos(), false)));
        }

        for(DungeonMob e : Enemies) {
            UUID id = e.getUUID();
            e.setInDungeon(true);
            EnemiesInRoom.add(id);
        }
    }

    private void checkEnemiesInRoom(ServerLevel level) {
        if (!EnemiesInRoom.isEmpty()) {
            EnemiesInRoom.removeIf(id -> {
                Entity e = level.getEntity(id);
                if(e instanceof DungeonMob) {
                    boolean flag = false;
                    for(RoomVolume v : roomBounds) {
                        if (v.toAABB(this.getBlockPos(), false).intersects(e.getBoundingBox())) {
                            flag = true;
                            break;
                        }
                    }
                    if(!e.isAlive() || !flag) {
                        ((DungeonMob) e).setInDungeon(false);
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
        Set<Player> p = new HashSet<>();
        for(RoomVolume v : roomBounds) {
            p.addAll(level.getEntitiesOfClass(Player.class, v.toAABB(this.getBlockPos(), true), player -> player.isAlive() && !player.isSpectator() && !player.isCreative() && player.isAlive()));
        }
        PlayersInRoom.clear();
        PlayersInRoom.addAll(p);
    }

    private void checkPlayersInRoom() {
        PlayersInRoom.removeIf(e -> e == null || !isPlayerInVolumes(e) || !e.isAlive() || e.isSpectator() || e.isCreative());
    }

    protected boolean isPlayerInVolumes(Player player) {
        for(RoomVolume v : roomBounds) {
            if(v.toAABB(this.getBlockPos(), true).intersects(player.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    public List<RoomVolume> getRoomBounds() {
        return roomBounds;
    }
    public RoomState getRoomState() {
        return state;
    }

    public void setRoomLayout(RoomDefinition type) {
        this.RoomLayout = type;
        setChanged();
        if(level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        initBounds();
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

        for(RoomVolume v : roomBounds) {
            BlockPos min = origin.offset(v.min);
            BlockPos max = origin.offset(v.max);

            for(BlockPos pos : BlockPos.betweenClosed(min, max)){
                BlockState state = level.getBlockState(pos);
                if(state.getBlock() instanceof DungeonGateBlock gate) {
                    if(DungeonGateBlock.isLowerHalf(state)) {
                        GatesInRoom.add(pos.immutable());
                    }
                }
            }
        }
    }

    private void spawnEnemies() {
        BlockPos origin = this.getBlockPos();

        if(this.level != null && !this.level.isClientSide){
            for(RoomVolume v : roomBounds) {
                BlockPos min = origin.offset(v.min);
                BlockPos max = origin.offset(v.max);

                for(BlockPos pos : BlockPos.betweenClosed(min, max)) {
                    if(this.level.getBlockState(pos).getBlock() instanceof DungeonMobSpawnerBlock) {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if(blockEntity instanceof DungeonMobSpawner spawner) {
                            spawner.spawnMob();
                        }
                    }
                }
            }
        }
    }

    protected void spawnRewards(ServerLevel l) {
        BlockPos pos = this.getBlockPos();

        ResourceLocation r = ResourceLocation.fromNamespaceAndPath(MODID, "dungeon_rewards/basic_rewards");
        LootTable table = l.getServer().getLootData().getLootTable(r);
        LootParams params = new LootParams.Builder(l).create(LootContextParamSets.EMPTY);
        ObjectArrayList<ItemStack> rewards = table.getRandomItems(params);

        for(ItemStack stack : rewards) {
            ItemEntity item = new ItemEntity(l, pos.getX() + 0.5, pos.getY() + 2.0, pos.getZ() + 0.5, stack);

            item.setDeltaMovement(item.getDeltaMovement().add(0, 0.2, 0));
            l.addFreshEntity(item);
        }
    }

    protected void spawnRoom(ServerLevel level, BlockPos pos, BlockPos offset) {
        String roomType = this.RoomLayout.toString();
        String dimension = level.dimension().location().getPath();

        ResourceLocation roomPoolId = ResourceLocation.fromNamespaceAndPath(MODID, dimension + "/" + roomType + "_rooms");
        StructureTemplatePool roomPool = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL).get(roomPoolId);

        if(roomPool == null || roomPool.size() == 0) {
            NormalDungeonMod.LOGGER.error("Template pool: " + roomPoolId + " is returning a null template pool or is empty, cancelling room spawn...");
            return;
        }

        StructurePoolElement chosenRoom = roomPool.getRandomTemplate(level.getRandom());

        BlockPos offsetPos = pos.offset(offset);

        chosenRoom.place(
                level.getStructureManager(),
                level,
                level.structureManager(),
                level.getChunkSource().getGenerator(),
                offsetPos,
                pos,
                Rotation.NONE,
                BoundingBox.infinite(),
                level.getRandom(),
                false
        );
        getGatesInRoom(level);
        coverUnusedGates(level);
        decayRoom(level);

        ResourceLocation floorPoolId = ResourceLocation.fromNamespaceAndPath(MODID, dimension + "/floors/" + roomType + "_floors");
        StructureTemplatePool floorPool = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL).get(floorPoolId);

        if(floorPool == null || floorPool.size() == 0) {
            NormalDungeonMod.LOGGER.error("Template pool: " + floorPoolId + " is returning a null template pool or is empty, cancelling room spawn...");
            return;
        }

        StructurePoolElement chosenFloor = floorPool.getRandomTemplate(level.getRandom());

        chosenFloor.place(
                level.getStructureManager(),
                level,
                level.structureManager(),
                level.getChunkSource().getGenerator(),
                offsetPos,
                pos,
                Rotation.NONE,
                BoundingBox.infinite(),
                level.getRandom(),
                false
        );
    }

    public BlockPos getRoomSpawnOffset() {
        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for(RoomVolume v : roomBounds) {
            if(v.getMin().getX() < minX) minX = v.getMin().getX();
            if(v.getMin().getZ() < minZ) minZ = v.getMin().getZ();
        }

        return new BlockPos(minX, 1, minZ);
    }

    protected void coverUnusedGates(Level level) {
        for(BlockPos pos : this.GatesInRoom) {
            if(level.getBlockState(pos).getBlock() instanceof DungeonGateBlock gate) {
                if(!gate.shouldBeHere(level, pos)) {
                    level.setBlock(pos, NDMBlocks.CELLAR_WALL.get().defaultBlockState(), 3);
                    level.setBlock(pos.above(), NDMBlocks.CELLAR_WALL.get().defaultBlockState(), 3);
                }
            }
        }
    }

    protected void decayRoom(Level level) {
        for(RoomVolume v : roomBounds) {
            BlockPos min = this.getBlockPos().offset(v.getMin());
            BlockPos max = this.getBlockPos().offset(v.getMax());
            for(BlockPos pos : BlockPos.betweenClosed(min ,max)) {
                BlockState state = level.getBlockState(pos);

                if(state.isAir()) continue;

                if(state.is(NDMBlocks.CELLAR_WALL.get())) {
                    float roll = level.random.nextFloat();

                    if(pos.getY() - this.getBlockPos().getY() == 2) {
                        if(roll <= 0.05F) {
                            level.setBlock(pos, NDMBlocks.CELLAR_WALL_DRAIN.get().defaultBlockState(), 2);
                        }
                        else if(roll <= 0.5F) {
                            BlockState newState = level.random.nextInt(2) == 1 ? NDMBlocks.CELLAR_WALL_CRACKED.get().defaultBlockState() : NDMBlocks.CELLAR_WALL_MOSSY.get().defaultBlockState();
                            level.setBlock(pos, newState, 2);
                        }
                    }
                    else {
                        if(roll <= 0.6F) {
                            BlockState newState = roll <= 0.3 ? NDMBlocks.CELLAR_WALL_MOSSY.get().defaultBlockState() : NDMBlocks.CELLAR_WALL_CRACKED.get().defaultBlockState();
                            level.setBlock(pos, newState, 2);
                        }
                    }
                }
                if(state.is(NDMBlocks.CELLAR_CEILING.get())) {
                    float roll = level.random.nextFloat();

                    if(roll <= 0.2) {
                        level.setBlock(pos, NDMBlocks.CELLAR_CEILING_MOLDY.get().defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    public boolean hasSpawned() {
        return this.roomSpawned;
    }

    public void setHasSpawned() {
        this.roomSpawned = true;
    }

    /// NBT HELPERS ///

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        // save room state
        RoomState s = state;
        tag.putInt("roomState", s.ordinal());
        if(this.RoomLayout != null) {
            tag.putString("roomType", RoomLayout.toString());
        }

        saveUUIDSet(tag, "enemies", EnemiesInRoom);
        saveBlockPosSet(tag, "gatePos", GatesInRoom);

        tag.putBoolean("spawnedRoom", this.roomSpawned);
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
    public void load(@NotNull CompoundTag tag) {
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

        this.roomSpawned = tag.getBoolean("spawnedRoom");
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
