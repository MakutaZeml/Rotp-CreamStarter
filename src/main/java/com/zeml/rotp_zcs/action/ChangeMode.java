package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.init.InitItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ChangeMode extends StandEntityAction {

    public ChangeMode(StandEntityAction.Builder builder) {
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
            if(userPower.getUser().getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
                ItemStack itemStack = userPower.getUser().getItemInHand(Hand.MAIN_HAND);

                if(itemStack.hasTag()){
                    changeMode(itemStack);
                }


            }else if(userPower.getUser().getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()){
                ItemStack itemStack = userPower.getUser().getItemInHand(Hand.OFF_HAND);

                if(itemStack.hasTag()){
                    changeMode(itemStack);
                }
            }

    }

    public static void changeMode(ItemStack stack){
        CompoundNBT nbt = stack.getTag();
        String mode = nbt.getString("mode");

        if(mode.equals("attack")){
            nbt.putString("mode","heal");
        }

        if(mode.equals("heal")){
            nbt.putString("mode","attack");
        }

    }

}
