package com.zeml.rotp_zcs.item;

import com.github.standobyte.jojo.util.mc.MCUtil;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.util.CreamStarterUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MeatMaskItem extends Item {

    public MeatMaskItem(Properties properties) {
        super(properties);
    }





    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity living, Hand hand) {
        if(living instanceof PlayerEntity){
            this.setTargetType(itemStack,living.getName().getString(),1,Optional.of(living.getUUID()));
            NBTUtil.writeGameProfile(itemStack.getOrCreateTag(),player.getGameProfile());
        }else {
            this.setTargetType(itemStack,living.getType().getRegistryName().toString(),2, Optional.empty());

        }
        return super.interactLivingEntity(itemStack, player, living, hand);
    }

    public String getTargetType(ItemStack stack){
        if(stack.hasTag()){
            return  !stack.getTag().getString("Target").isEmpty()? stack.getTag().getString("Target"): "";
        }
        return "";
    }

    public int targetMode(ItemStack stack){
        if(stack.hasTag() && stack.getTag().contains("mode")){
            return stack.getTag().getInt("mode");
        }
        return 0;
    }

    public Optional<UUID> getTargetUUID(ItemStack stack){
        if(stack.hasTag()){
            if(stack.getTag().hasUUID("UUID")){
                return Optional.of(stack.getTag().getUUID("UUID"));
            }
        }
        return Optional.empty();
    }


    public void setTargetType(ItemStack stack, String entity, int mode, Optional<UUID> uuid){
        CompoundNBT nbt =stack.getOrCreateTag();
        nbt.putString("Target",entity);
        nbt.putInt("mode",mode);
        uuid.ifPresent(value -> nbt.putUUID("UUID", value));
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

    public void setHostile(ItemStack stack, boolean hostile){
        stack.getOrCreateTag().putBoolean("hostile",hostile);
    }

    public boolean getHostile(ItemStack stack){
        if(stack.hasTag() && stack.getTag().contains("hostile")){
            return stack.getTag().getBoolean("hostile");
        }
        return false;
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }
}
