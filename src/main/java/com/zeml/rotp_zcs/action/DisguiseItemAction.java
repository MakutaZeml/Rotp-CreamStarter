package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.capability.LivingDataProvider;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class DisguiseItemAction extends StandEntityAction {

    public DisguiseItemAction(StandEntityAction.Builder builder){
        super(builder);
    }

    @Override
    protected ActionConditionResult checkHeldItems(LivingEntity user, IStandPower power) {
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() instanceof CreamStarterItem){
            if(user.getItemInHand(Hand.OFF_HAND).isEmpty()){
                if( user.getItemInHand(Hand.MAIN_HAND).getTag().getInt("Ammo")>=50){
                    return ActionConditionResult.POSITIVE;
                }
                return conditionMessage("");
            }
            return conditionMessage("hand");
        }
        return conditionMessage("no_cream.main");
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if(!world.isClientSide){
            LivingEntity livingEntity = userPower.getUser();
            ItemStack itemStack = new ItemStack(InitItems.MEAT_MASK.get());
            if(itemStack.getItem() instanceof MeatMaskItem){
                MeatMaskItem meatMaskItem = (MeatMaskItem) itemStack.getItem();
                meatMaskItem.setOwner(itemStack, livingEntity);
            }
            ItemStack cream = livingEntity.getItemInHand(Hand.MAIN_HAND);
            if(cream.getItem() instanceof MeatMaskItem){
                int ammo = Healing.getAmmo(cream)-50;
                cream.getOrCreateTag().putInt("Ammo", ammo);
                livingEntity.getCapability(LivingDataProvider.CAPABILITY).ifPresent(livingData -> livingData.setMeat(50));
            }
            livingEntity.setItemInHand(Hand.OFF_HAND,itemStack);
        }
    }
}
