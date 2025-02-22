package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.DefendAreaTask;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;


public class DefendAreaGoal extends TargetGoal {

    private final MinionEntity<?> entity;
    private final EntityPredicate predicate;
    /**
     * Cache bb as long as {@link DefendAreaGoal#center} is unmodified
     */
    private AxisAlignedBB bb;
    private BlockPos center;

    public DefendAreaGoal(MinionEntity<?> entity) {
        super(entity, false);
        this.entity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.predicate = new EntityPredicate().selector(e -> entity.getAttackPredicate(true).test(e)).ignoreInvisibilityTesting().range(60);
    }

    @Override
    public boolean canContinueToUse() {
        return entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.DEFEND_AREA.get()).isPresent() && super.canContinueToUse();
    }

    @Override
    public boolean canUse() {
        return entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.DEFEND_AREA.get() && ((DefendAreaTask.Desc) task).center != null).map(task -> {
                    BlockPos newCenter = ((DefendAreaTask.Desc) task).center;
                    if (bb == null || center == null || !center.equals(newCenter)) {
                        this.bb = new AxisAlignedBB(newCenter).inflate(((DefendAreaTask.Desc) task).distance);
                        this.center = newCenter;
                    }

                    this.targetMob = entity.level.getNearestEntity(LivingEntity.class, predicate, entity, entity.getX(), entity.getY(), entity.getZ(), bb);
                    return this.targetMob != null;
                }
        ).orElse(false);
    }

    @Override
    public void start() {
        super.start();
        this.entity.setTarget(this.targetMob);
    }
}
