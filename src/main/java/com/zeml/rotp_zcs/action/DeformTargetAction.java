package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityHeavyAttack;
import com.github.standobyte.jojo.action.stand.punch.StandEntityPunch;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.general.GeneralUtil;
import com.github.standobyte.jojo.util.mc.damage.StandEntityDamageSource;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.init.InitParticles;
import com.zeml.rotp_zcs.init.InitSounds;
import com.zeml.rotp_zcs.item.CreamStarterItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.UseAction;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DeformTargetAction extends StandEntityAction {

    public DeformTargetAction(StandEntityAction.Builder builder) {
        super(builder);
    }


    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (user.getItemInHand(Hand.MAIN_HAND).getItem() instanceof CreamStarterItem || user.getItemInHand(Hand.OFF_HAND).getItem() instanceof CreamStarterItem) {
            RayTraceResult rayTraceResult = JojoModUtil.rayTrace(user, 4.5, Entity::isAlive);
            if (rayTraceResult instanceof EntityRayTraceResult) {
                Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    CreamStarterAddon.LOGGER.debug("Eyes {} ,{}, looking {} ", rayTraceResult.getLocation().y, (entity.position().y + livingEntity.getEyeHeight()), areTheyLooking(user, livingEntity));
                    CreamStarterAddon.LOGGER.debug("Eyes bool {}, look bool {}", Math.abs(rayTraceResult.getLocation().y - (entity.position().y + livingEntity.getEyeHeight())) < .35F, areTheyLooking(user, livingEntity));

                    if (Math.abs(rayTraceResult.getLocation().y - (entity.position().y + livingEntity.getEyeHeight())) < .35F
                            && areTheyLooking(user, livingEntity) ||
                            Math.abs(rayTraceResult.getLocation().y - (entity.position().y + .25F)) < .5
                    ) {
                        return ActionConditionResult.POSITIVE;
                    }
                }
            }
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        LivingEntity user = userPower.getUser();
        RayTraceResult rayTraceResult = JojoModUtil.rayTrace(user, 4.5, Entity::isAlive);
        if (rayTraceResult instanceof EntityRayTraceResult) {
            Entity entity = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                CreamStarterAddon.LOGGER.debug("Ray tracer {}, entity {}, eyes {}", rayTraceResult, entity.position(), entity.position().add(0, livingEntity.getEyeHeight(), 0));
                if (Math.abs(rayTraceResult.getLocation().y - (entity.position().y + livingEntity.getEyeHeight())) < .35F && areTheyLooking(user, livingEntity)) {
                    if (!world.isClientSide) {
                        livingEntity.addEffect(new EffectInstance(Effects.BLINDNESS.getEffect(), 60, 0, false, false, false));
                        coolDownStarter(user);
                        world.playSound(null,user.getX(),user.getY(),user.getZ(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1F,1F);

                    } else {
                        double dx = rayTraceResult.getLocation().x- user.getX();
                        double dy = rayTraceResult.getLocation().y - user.getY();
                        double dz =  rayTraceResult.getLocation().z - user.getZ();
                        world.addParticle(InitParticles.MEAT.get(),user.getX(),user.getY()+user.getBbHeight()*.625F,user.getZ(),dx,dy,dz);

                    }
                } else if (Math.abs(rayTraceResult.getLocation().y - (entity.position().y + .25F)) < .5) {
                    if (!world.isClientSide) {
                        livingEntity.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN.getEffect(), 60, 2, false, false, false));
                        coolDownStarter(user);
                        world.playSound(null,user.getX(),user.getY(),user.getZ(), InitSounds.CS_SPRAY.get(), SoundCategory.PLAYERS,1F,1F);
                    } else {
                        double dx = rayTraceResult.getLocation().x- user.getX();
                        double dy = rayTraceResult.getLocation().y - user.getY();
                        double dz =  rayTraceResult.getLocation().z - user.getZ();
                        world.addParticle(InitParticles.MEAT.get(),user.getX(),user.getY()+user.getBbHeight()*.625F,user.getZ(),dx,dy,dz);

                    }
                }


            }

        }
    }


    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }

    private void coolDownStarter(LivingEntity user) {
        if (user.getItemInHand(Hand.OFF_HAND).getItem() instanceof CreamStarterItem) {
            user.getItemInHand(Hand.OFF_HAND).releaseUsing(user.level, user, 0);
        } else if (user.getItemInHand(Hand.MAIN_HAND).getItem() instanceof CreamStarterItem) {
            user.getItemInHand(Hand.MAIN_HAND).releaseUsing(user.level, user, 0);
        }
    }

    private boolean areTheyLooking(LivingEntity user, LivingEntity target) {
        Vector3d sourcePos = user.position();
        Vector3d targetView = target.getViewVector(1);
        Vector3d vectorTo = sourcePos.vectorTo(target.position()).normalize();
        vectorTo = new Vector3d(vectorTo.x,0,vectorTo.z);
        return vectorTo.dot(targetView) < -0.5;
    }



}
