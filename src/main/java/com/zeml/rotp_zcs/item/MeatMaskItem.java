package com.zeml.rotp_zcs.item;

import com.github.standobyte.jojo.item.StandDiscItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class MeatMaskItem extends Item {

    public MeatMaskItem(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }





    public void setTarget(ItemStack stack, LivingEntity target){
        stack.getOrCreateTag().putUUID("target",target.getUUID());
    }

    public Optional<UUID> getTarget(ItemStack stack){
        if(stack.hasTag()){
            if(stack.getTag().hasUUID("target")){
                return Optional.of(stack.getTag().getUUID("target"));
            }
        }
        return Optional.empty();
    }


    public void setOwner(ItemStack stack, LivingEntity owner){
        stack.getOrCreateTag().putUUID("owner",owner.getUUID());
    }

    public Optional<UUID> getOwner(ItemStack stack){
        if(stack.hasTag()){
            if(stack.getTag().hasUUID("owner")){
                return Optional.of(stack.getTag().getUUID("owner"));
            }
        }
        return Optional.empty();
    }



    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
}
