package com.zeml.rotp_zcs.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;

public class CreamStarterItem extends Item {
    public CreamStarterItem(Properties properties) {
        super(properties);
    }





    @Override
    public UseAction getUseAnimation(ItemStack stack) {
        return UseAction.BOW;
    }
}
