package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitSounds;
import com.zeml.rotp_zcs.init.IntTags;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class DeformAction extends StandEntityAction {
    private final float angle = 75;

    public DeformAction(StandEntityAction.Builder builder){
        super(builder);
    }

    @Override
    protected ActionConditionResult checkTarget(ActionTarget target, LivingEntity user, IStandPower power) {
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get() || user.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
            return rightAngleAndTarget(target.getBlockPos(), user) && hasFreeSpace(user.level,getTargetBlock(user,target.getBlockPos()))
                    ? ActionConditionResult.POSITIVE : ActionConditionResult.NEGATIVE;
        }
        return conditionMessage("no_cream");
    }


    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(!world.isClientSide){
            LivingEntity user = userPower.getUser();
            BlockPos target = getTargetBlock(user,task.getTarget().getBlockPos());
            Vector3d vector3d = Vector3d.atCenterOf(target);

            float delta = 0;
            if(!world.getBlockState(target.below()).getMaterial().isSolid() && !Direction.UP.isFacingAngle(1F)){
                delta -= 1;
            }

            user.teleportTo(vector3d.x,target.getY()+delta, vector3d.z);
            if(world.getBlockState(target.above()).getMaterial().isSolid()){
                user.setPose(Pose.CROUCHING);
            }

        }
    }

    @Override
    public void onHoldTick(World world, LivingEntity user, IStandPower power, int ticksHeld, ActionTarget target, boolean requirementsFulfilled) {
        if(!world.isClientSide){
            world.playSound(null,user.blockPosition(),InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1F,1F);
        }
    }

    @Override
    public TargetRequirement getTargetRequirement() {
            return TargetRequirement.BLOCK;
    }


    private boolean rightAngleAndTarget(BlockPos blockPos, LivingEntity user){
        return isVertical(user,blockPos) || isHorizontal(user,blockPos);
    }

    private boolean isHorizontal(LivingEntity user, BlockPos blockPos){
        boolean horizontal = false;
        if(Math.abs(user.getViewXRot(1))<angle){
            switch (Direction.fromYRot(user.getViewYRot(1))){
                case EAST: case WEST: case NORTH: case SOUTH: horizontal = true;
                    break;
            }
        }
        return IntTags.HORIZONTAL.contains(user.level.getBlockState(blockPos).getBlock()) && horizontal;
    }

    private boolean isVertical(LivingEntity user, BlockPos blockPos){
        boolean vertical = Math.abs(user.getViewXRot(1))>angle;
        return IntTags.VERTICAL.contains(user.level.getBlockState(blockPos).getBlock()) &&  vertical;
    }

    private BlockPos getTargetBlock(LivingEntity user, BlockPos blockPos){
        if(Math.abs(user.getViewXRot(1))<angle){
            switch (Direction.fromYRot(user.getViewYRot(1))){
                case SOUTH:return blockPos.south();
                case NORTH:return blockPos.north();
                case WEST: return blockPos.west();
                case EAST: return blockPos.east();
            }
        }else {
            if(user.getViewXRot(1)>0){
                return blockPos.below();
            }else {
                return blockPos.above();
            }
        }
        return blockPos;
    }

    private boolean hasFreeSpace(World world, BlockPos blockPos){
        return !world.getBlockState(blockPos).getMaterial().isSolidBlocking() || world.getBlockState(blockPos).getMaterial().isLiquid();
    }


}
