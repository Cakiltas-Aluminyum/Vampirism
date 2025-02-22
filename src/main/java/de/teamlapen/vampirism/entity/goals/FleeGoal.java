package de.teamlapen.vampirism.entity.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

/**
 * Basic Flee from anything ai
 */
public abstract class FleeGoal extends Goal {
    private final CreatureEntity theCreature;
    private final double movementSpeed;
    private final World world;
    private final boolean restrictToHome;
    private double shelterX;
    private double shelterY;
    private double shelterZ;

    public FleeGoal(CreatureEntity theCreature, double movementSpeed, boolean restrictToHome) {
        this.theCreature = theCreature;
        this.movementSpeed = movementSpeed;
        this.restrictToHome = restrictToHome;
        world = theCreature.getCommandSenderWorld();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!shouldFlee()) return false;
        Vector3d vec3 = this.findPossibleShelter();

        if (vec3 == null) {
            return false;
        } else {
            this.shelterX = vec3.x;
            this.shelterY = vec3.y;
            this.shelterZ = vec3.z;
            return true;
        }
    }

    public boolean continueExecuting() {
        return !this.theCreature.getNavigation().isDone();
    }

    public void start() {
        this.theCreature.getNavigation().moveTo(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    protected abstract boolean isPositionAcceptable(World world, BlockPos pos);

    protected abstract boolean shouldFlee();

    private Vector3d findPossibleShelter() {
        Random random = this.theCreature.getRandom();
        BlockPos blockpos = new BlockPos(this.theCreature.getX(), this.theCreature.getBoundingBox().minY, this.theCreature.getZ());

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.offset(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

            if (isPositionAcceptable(world, blockpos1)) {
                if (restrictToHome && !theCreature.getRestrictCenter().equals(BlockPos.ZERO)) {

                    if (!theCreature.isWithinRestriction(blockpos1)) continue;
                }
                return new Vector3d(blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
            }
        }

        return null;
    }

}