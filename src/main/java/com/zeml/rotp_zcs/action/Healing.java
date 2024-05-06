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
import net.minecraft.item.ItemStack;
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
        if(result(user)){
            return ActionConditionResult.POSITIVE;
        }
        return conditionMessage("no_cream");
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task){
        if(!world.isClientSide){
            boolean use = true;
            if(userPower.getUser().getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
                consumeAmmo(userPower.getUser().getItemInHand(Hand.MAIN_HAND));


            } else if (userPower.getUser().getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()){
                consumeAmmo(userPower.getUser().getItemInHand(Hand.OFF_HAND));
            }

            if(userPower.getStamina() >9){
                userPower.getUser().heal(.25F);
                world.playSound(null,standEntity.blockPosition(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1,1);
            }
        }
    }

    private boolean result(LivingEntity user){
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
            if(user.getItemInHand(Hand.MAIN_HAND).hasTag() && user.getItemInHand(Hand.MAIN_HAND).getTag().getInt("Ammo")>0){
                return true;
            }
            return false;
        } else if (user.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
            if(user.getItemInHand(Hand.OFF_HAND).hasTag() && user.getItemInHand(Hand.OFF_HAND).getTag().getInt("Ammo")>0){
                return true;
            }
        }
        return false;
    }

    private boolean consumeAmmo(ItemStack cream) {
        int ammo = getAmmo(cream);
        if (ammo < 0) {
            cream.getTag().putInt("Ammo", 0);
            return false;
        }
        if (ammo > 0) {
            cream.getTag().putInt("Ammo", --ammo);
            return true;
        }
        return false;
    }

    private static int getAmmo(ItemStack cream) {
        return cream.getOrCreateTag().getInt("Ammo");
    }

}
