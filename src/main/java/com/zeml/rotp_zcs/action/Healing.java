package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Healing extends StandEntityAction {
    public Healing(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target){
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get() || user.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()){
            return ActionConditionResult.POSITIVE;
        }
        return conditionMessage("no_cream");
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){
            if(userPower.getStamina() >9){
                userPower.getUser().heal(.25F);
                world.playSound(null,standEntity.blockPosition(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1,1);
            }
        }
    }



}
