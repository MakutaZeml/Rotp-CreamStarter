package com.zeml.rotp_zcs.mixin;

import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin  extends Entity {

    public LivingEntityMixin(EntityType<?> p_i48580_1_, World p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }


    /*
    @Inject(method = "tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        CreamStarterAddon.LOGGER.debug("Living Mixin");
        if(!this.level.isClientSide){
            if(this.getEntity() instanceof MobEntity){
                MobEntity mobEntity = (MobEntity) this.getEntity();
                LivingEntity target = mobEntity.getTarget();
                if(target != null){
                    ItemStack itemStack = target.getItemBySlot(EquipmentSlotType.HEAD);
                    if(itemStack.getItem() instanceof MeatMaskItem){
                        MeatMaskItem item = (MeatMaskItem) itemStack.getItem();
                        if(item.getHostile(itemStack) && mobEntity.getLastHurtByMob() != target){
                            mobEntity.setTarget(null);
                            CreamStarterAddon.LOGGER.debug("Living Mixin Worked");
                        }
                    }
                }

            }
        }

    }
     */

    @Inject(method = "getVisibilityPercent", at=@At("RETURN"),cancellable = true)
    private void onMaskUse(Entity entity, CallbackInfoReturnable<Double> cir){
        ItemStack itemStack =  this.getItemBySlot(EquipmentSlotType.HEAD);
        double prev = cir.getReturnValue();
        if(itemStack.getItem() instanceof MeatMaskItem){
            MeatMaskItem meatMaskItem = (MeatMaskItem) itemStack.getItem();
            if(meatMaskItem.getHostile(itemStack)){
                prev *= .05;
            }
        }
        CreamStarterAddon.LOGGER.debug("Final visibility {}", prev);
        cir.setReturnValue(prev);
    }

    @Shadow
    protected abstract void defineSynchedData();

    @Shadow
    public abstract void readAdditionalSaveData(CompoundNBT p_70037_1_);

    @Shadow
    public abstract void addAdditionalSaveData(CompoundNBT p_213281_1_);

    @Shadow
    public abstract IPacket<?> getAddEntityPacket();

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlotType p_184582_1_);

}
