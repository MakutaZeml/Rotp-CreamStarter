package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandAction;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitStands;
import com.zeml.rotp_zcs.init.IntTags;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

public class FillMeatItems extends StandEntityAction {

    public FillMeatItems(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    protected Action<IStandPower> replaceAction(IStandPower power, ActionTarget target){
        if(!checkTrans(power)){
            return InitStands.CS_ENTITY_FILL.get();
        }
        return super.replaceAction(power,target);
    }

    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (result(user)) {
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide) {
            if (userPower.getUser() instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity) userPower.getUser();
                if (player.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
                  fillHand(player,Hand.MAIN_HAND);
                } else if (player.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
                    fillHand(player,Hand.OFF_HAND);
                }
            }
        }
    }


    public void fillHand(PlayerEntity player, Hand hando) {
        if (!fillItems(player, player.getItemInHand(hando), IntTags.EGGCELENT_MEAT, 240)) {
            if (!fillItems(player, player.getItemInHand(hando), IntTags.GOOD_MEAT, 80)) {
                if (!fillItems(player, player.getItemInHand(hando), IntTags.MID_MEAT, 60)){
                        fillItems(player,player.getItemInHand(hando),IntTags.BAD_MEAT,30);
                }
            }

        }
    }

    private boolean result(LivingEntity user){
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
            if(user.getItemInHand(Hand.MAIN_HAND).hasTag() && user.getItemInHand(Hand.MAIN_HAND).getTag().getInt("Ammo")<CreamStarterItem.MAX_AMMO){
                return true;
            }
            return false;
        } else if (user.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
            if(user.getItemInHand(Hand.OFF_HAND).hasTag() && user.getItemInHand(Hand.OFF_HAND).getTag().getInt("Ammo")<CreamStarterItem.MAX_AMMO){
                return true;
            }
        }
        return false;
    }


    private boolean fillItems (PlayerEntity player, ItemStack stack, Tags.IOptionalNamedTag<Item> tag, int amount){
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if(stack.getTag().getInt("Ammo")<CreamStarterItem.MAX_AMMO){
                if (inventoryStack.getItem().is(tag)) {
                    int fill =stack.getTag().getInt("Ammo")+amount ;
                    stack.getTag().putInt("Ammo",fill);
                    inventoryStack.shrink(1);
                    if(stack.getTag().getInt("Ammo")>CreamStarterItem.MAX_AMMO){
                        stack.getTag().putInt("Ammo",CreamStarterItem.MAX_AMMO);
                        i= player.inventory.getContainerSize();
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private boolean checkTrans(IStandPower power){
        if(power.getUser() instanceof PlayerEntity){
            PlayerEntity player = (PlayerEntity) power.getUser();
            return checkTags(player, IntTags.BAD_MEAT) || checkTags(player, IntTags.MID_MEAT) ||
                    checkTags(player, IntTags.GOOD_MEAT) || checkTags(player, IntTags.EGGCELENT_MEAT);
        }
        return false;
    }

    private boolean checkTags(PlayerEntity player, Tags.IOptionalNamedTag<Item> tag){
        for (int i = 0; i < player.inventory.getContainerSize(); ++i) {
            ItemStack inventoryStack = player.inventory.getItem(i);
            if (inventoryStack.getItem().is(tag)) {
                i= player.inventory.getContainerSize();
                return true;
            }
        }
        return false;
    }


    @Override
    public StandAction[] getExtraUnlockable() {
        return new StandAction[] { InitStands.CS_ENTITY_FILL.get()};
    }
}
