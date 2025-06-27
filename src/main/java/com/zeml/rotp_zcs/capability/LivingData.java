package com.zeml.rotp_zcs.capability;

import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class LivingData implements INBTSerializable<CompoundNBT> {
    private final LivingEntity entity;
    private int meat = CreamStarterItem.MAX_AMMO/2;

    public LivingData(LivingEntity entity) {
        this.entity = entity;
    }

    public void setMeat(int meat) {
        this.meat = meat;
    }

    public int getMeat() {
        return meat;
    }

    public void syncWithAnyPlayer(ServerPlayerEntity player) {

        //AddonPackets.sendToClient(new TrPickaxesThrownPacket(entity.getId(), pickaxesThrown), player);
    }

    // If there is data that should only be known to the player, and not to other ones, sync it here instead.
    public void syncWithEntityOnly(ServerPlayerEntity player) {
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

}
