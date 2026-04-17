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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.NormalDungeonMod;
import org.slf4j.Logger;

import java.util.*;

public class RoomControllerBlockEntity extends BlockEntity {
    Logger LOG = NormalDungeonMod.LOGGER;

    public int tickCount;
    AABB roomBounds = new AABB(this.getBlockPos()).inflate(10);
    AABB pRoomBounds = roomBounds.deflate(1);

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
        //NormalDungeonMod.LOGGER.info(entity.tickCount + " ticks");
        if(!level.isClientSide) {
            if(entity.tickCount % 10 == 0){
                //NormalDungeonMod.LOGGER.info("check");

                switch(entity.state) {
                    case DORMANT:
                        NormalDungeonMod.LOGGER.info("Room is Dormant, checking for players...");
                        entity.getPlayersInRoom(level);

                        if(!entity.PlayersInRoom.isEmpty()) {
                            entity.activateRoom(level);
                        }
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

    public void activateRoom(Level level) {
        if(!level.isClientSide) {
            getEnemiesInRoom(level); // remove when spawnEnemies implemented

            for(Player player : PlayersInRoom){
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));
            }

            lockDoors(level);

            state = RoomState.ACTIVE;
            LOG.info("ROOM HAS BEEN ACTIVATED");

            // spawnEnemies()
        }
    }

    public void checkClearRoom(Level level) {
        checkEnemiesInRoom((ServerLevel) level);

        if(EnemiesInRoom.isEmpty()){
            unlockDoors(level);
            state = RoomState.CLEARED;
            LOG.info("ROOM HAS BEEN CLEARED");
        }
    }

    public void checkFailRoom() {
        checkPlayersInRoom();
        if (PlayersInRoom.isEmpty()) {
            state = RoomState.FAILED;
            LOG.info("ROOM HAS BEEN FAILED");

            for(UUID id : EnemiesInRoom) {
                ServerLevel l = (ServerLevel) level;
                Entity e = l.getEntity(id);
                if(e != null) e.kill();
            }
            unlockDoors(level);
        }
    }

    public void getEnemiesInRoom(Level level) {
        List<Monster> Enemies = level.getEntitiesOfClass(Monster.class, getRoomBounds());

        for(Monster e : Enemies) {
            UUID id = e.getUUID();
            EnemiesInRoom.add(id);
        }

        LOG.info(EnemiesInRoom.size() + " Enemies Detected in Room");
    }

    private void checkEnemiesInRoom(ServerLevel level) {
        if (!EnemiesInRoom.isEmpty()) {
            EnemiesInRoom.removeIf(id -> {
                Entity e = level.getEntity(id);
                return e == null || !e.isAlive();
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // save room state
        RoomState s = state;
        tag.putInt("roomState", s.ordinal());

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

    // don't know if these are needed but i have them
    private void setRoomBounds(AABB aabb) {
        roomBounds = aabb;
    }
    public AABB getRoomBounds() {
        return roomBounds;
    }
    private void setPlayerRoomBounds(AABB aabb) {
        pRoomBounds = aabb;
    }
    public AABB getPlayerRoomBounds() {
        return pRoomBounds;
    }

    private void lockDoors(Level level) {
        for(Player player : PlayersInRoom){
            player.sendSystemMessage(Component.literal("Doors have been 'locked'"));
        }

        DoorsInRoom.clear();
        BlockPos min = this.getBlockPos().offset(-10, -5, -10);
        BlockPos max = this.getBlockPos().offset(10, 5, 10);
        LOG.info("min: " + min + " max: " + max);
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

        level.playSound(null, this.getBlockPos(), SoundEvents.WITHER_DEATH, SoundSource.BLOCKS, 1.0f, 1.0f);

        // gonna have to add particles later using packets
    }

    private void spawnEnemies() {
        //check roomBounds for instances of MobSpawnBlock (or whatever i call them) and activate them
        // (they will have an activate function that spawns the mob and removes the block)
        // add all enemies spawned into hashset.
    }
}
