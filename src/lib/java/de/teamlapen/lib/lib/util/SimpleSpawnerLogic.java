package de.teamlapen.lib.lib.util;

import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraft.world.spawner.WorldEntitySpawner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Simple mob spawning logic. More configurable than {@link AbstractSpawner} but less functional.
 */
public class SimpleSpawnerLogic<T extends Entity> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static final int MOB_COUNT_DIV = (int) Math.pow(17.0D, 2.0D);

    private @Nonnull
    final EntityType<T> entityType;
    private @Nullable
    BlockPos pos;
    private @Nullable
    World world;
    private @Nullable
    Consumer<T> onSpawned;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int activateRange = 16;
    private int dailyLimit = Integer.MAX_VALUE;
    private int spawnCount = 1;
    private int maxNearbyEntities = 4;
    private int spawnRange = 4;
    private int spawnDelay = 20;
    private int spawnedToday = 0;
    private long spawnedLast = 0L;
    private boolean flag = true;
    private EntityClassification limitType;

    public SimpleSpawnerLogic(@Nonnull EntityType<T> entityTypeIn) {
        this.entityType = entityTypeIn;
    }

    @Nonnull
    public EntityType<T> getEntityType() {
        return entityType;
    }

    public int getSpawnedToday() {
        return spawnedToday;
    }

    public boolean isActivated() {
        if (this.world == null) return false;
        if (this.pos == null) return false;
        return this.world.hasNearbyAlivePlayer(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D, this.activateRange);
    }

    public void readFromNbt(CompoundNBT nbt) {
        this.spawnDelay = nbt.getInt("delay");
        this.spawnedToday = nbt.getInt("spawned_today");
        this.spawnedLast = nbt.getLong("spawned_last");
        this.flag = nbt.getBoolean("spawner_flag");
    }

    public SimpleSpawnerLogic<T> setActivateRange(int activateRange) {
        this.activateRange = activateRange;
        return this;
    }

    public void setBlockPos(BlockPos blockPosIn) {
        this.pos = blockPosIn;
    }

    public SimpleSpawnerLogic<T> setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
        return this;
    }

    public boolean setDelayToMin(int id) {
        if (id == 1 && (this.world == null || this.world.isClientSide)) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if any more creatures of the given type are allowed in the world before spawning
     */
    public SimpleSpawnerLogic<T> setLimitTotalEntities(EntityClassification creatureType) {
        limitType = creatureType;
        return this;
    }

    public SimpleSpawnerLogic<T> setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
        return this;
    }

    public SimpleSpawnerLogic<T> setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
        return this;
    }

    public SimpleSpawnerLogic<T> setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
        return this;
    }

    public SimpleSpawnerLogic<T> setOnSpawned(Consumer<T> onSpawned) {
        this.onSpawned = onSpawned;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawn(boolean spawn) {
        this.flag = spawn;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
        return this;
    }

    public void setWorld(@Nullable World worldIn) {
        this.world = worldIn;
    }

    public void updateSpawner() {
        if (isActivated()) {
            if (this.world instanceof ServerWorld) {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }


                if ((this.world.getGameTime()) > this.spawnedLast + 24000) {
                    this.spawnedToday = 0;
                    this.flag = true;
                } else if (this.spawnedToday >= dailyLimit) {
                    this.flag = false;
                }
                if (!this.flag)
                    return;

                boolean flag1 = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    T entity = this.getEntityType().create(this.world);

                    if (entity == null) {
                        break;
                    }

                    int j = this.world.getEntitiesOfClass(entity.getClass(), getSpawningBox().inflate(5)).size();

                    if (j >= this.maxNearbyEntities) {
                        this.resetTimer();
                        break;
                    }

                    if (limitType != null) {
                        @Nullable
                        WorldEntitySpawner.EntityDensityManager densityManager = ((ServerWorld) this.world).getChunkSource().getLastSpawnState();
                        try {
                            if (densityManager != null && !densityManager.canSpawnForCategory(limitType)) {
                                this.resetTimer();
                                break;
                            }
                        } catch (NoSuchMethodError e) {
                            //Workaround for https://github.com/TeamLapen/Vampirism/issues/983
                            //Maybe remove when https://github.com/magmafoundation/Magma-1.16.x/issues/154 is solved
                        }

                    }

                    if (UtilLib.spawnEntityInWorld((ServerWorld) this.world, getSpawningBox(), entity, 1, Collections.emptyList(), SpawnReason.SPAWNER)) {
                        onSpawned(entity);
                        flag1 = true;
                    }
                }
                if (flag1) {
                    this.resetTimer();
                    this.spawnedToday++;
                    this.spawnedLast = this.world.getGameTime();
                }
            }
        }
    }

    public void writeToNbt(CompoundNBT nbt) {
        nbt.putInt("delay", spawnDelay);
        nbt.putInt("spawned_today", spawnedToday);
        nbt.putLong("spawned_last", spawnedLast);
        nbt.putBoolean("spawner_flag", flag);
    }

    protected AxisAlignedBB getSpawningBox() {
        if (this.pos == null) return AxisAlignedBB.ofSize(0, 0, 0);
        return (new AxisAlignedBB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX() + 1, this.pos.getY() + 1, this.pos.getZ() + 1)).inflate(this.spawnRange, this.spawnRange, this.spawnRange);

    }

    protected void onSpawned(T e) {
        if (e instanceof MobEntity) {
            ((MobEntity) e).spawnAnim();
        }
        if (this.onSpawned != null) {
            this.onSpawned.accept(e);
        }
    }

    private void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + (this.world == null ? 0 : this.world.random.nextInt(i));
        }
    }
}