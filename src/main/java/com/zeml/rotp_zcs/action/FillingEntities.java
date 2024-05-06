package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitSounds;
import com.zeml.rotp_zcs.init.IntTags;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FillingEntities extends StandEntityAction {
    public FillingEntities(StandEntityAction.Builder builder) {
        super(builder);
    }

    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (result(user)) {
            return ActionConditionResult.POSITIVE;
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standTickPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        if (!world.isClientSide){
            RayTraceResult ray = JojoModUtil.rayTrace(standEntity.getUser().getEyePosition(1.0F),standEntity.getUser().getLookAngle(),
                    3,world,standEntity,e->!(e.is(standEntity) || e.is(standEntity.getUser())),0,0);
            if(ray.getType() == RayTraceResult.Type.ENTITY){
                Entity target =  ((EntityRayTraceResult) ray).getEntity();
                if(!(target instanceof ProjectileEntity) && !IntTags.NO_MEATABLE.contains(target.getType())){
                    LivingEntity living = (LivingEntity) target;
                    if(userPower.getUser().getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get() ){
                        fillEntities(living,userPower.getUser().getItemInHand(Hand.MAIN_HAND));
                    } else if (userPower.getUser().getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
                        fillEntities(living,userPower.getUser().getItemInHand(Hand.OFF_HAND));
                    }
                }
            } else if (ray.getType() == RayTraceResult.Type.MISS) {
                if(userPower.getUser().getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get() ){
                    fillEntities(userPower.getUser(),userPower.getUser().getItemInHand(Hand.MAIN_HAND));
                } else if (userPower.getUser().getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
                    fillEntities(userPower.getUser(),userPower.getUser().getItemInHand(Hand.OFF_HAND));
                }

            }
        }
    }


    private void fillEntities(LivingEntity entity, ItemStack stack){
        int fill = stack.getTag().getInt("Ammo")+Math.round(entity.getHealth());
        stack.getTag().putInt("Ammo",fill);
        if(stack.getTag().getInt("Ammo")>CreamStarterItem.MAX_AMMO){
            stack.getTag().putInt("Ammo",CreamStarterItem.MAX_AMMO);
        }else {
            if(Math.random() < .3){
                entity.hurt(DamageSource.GENERIC,1F);
            }
        }
    }


    private boolean result(LivingEntity user){
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
            if(user.getItemInHand(Hand.MAIN_HAND).hasTag() && user.getItemInHand(Hand.MAIN_HAND).getTag().getInt("Ammo")< CreamStarterItem.MAX_AMMO){
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
}
