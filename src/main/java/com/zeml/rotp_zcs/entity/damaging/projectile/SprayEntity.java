package com.zeml.rotp_zcs.entity.damaging.projectile;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.action.stand.CrazyDiamondMisshapeBodyPart;
import com.github.standobyte.jojo.client.particle.custom.CustomParticlesHelper;
import com.github.standobyte.jojo.entity.damaging.projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.util.mod.JojoModUtil;
import com.zeml.rotp_zcs.init.InitEntities;
import com.zeml.rotp_zcs.init.InitParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class SprayEntity extends ModdedProjectileEntity {
    LivingEntity shooter;
    boolean damage;
    public SprayEntity(LivingEntity shooter, World world){
        super(InitEntities.CREAM_SPRAY.get(),shooter, world);
        this.shooter = shooter;
    }
    public SprayEntity(EntityType<SprayEntity> spray, World world) {
        super(spray, world);
    }

    @Override
    public void tick(){
        super.tick();
        Vector3d mov = getDeltaMovement();

        if(level.isClientSide){
            for(int i=0;i<2;++i){
                level.addParticle(InitParticles.MEAT.get(),
                        this.getX()-mov.x+random.nextFloat()/3,
                        this.getY()-mov.y+random.nextFloat()/3,
                        this.getZ()-mov.z+random.nextFloat()/3
                        ,mov.x,mov.y,mov.z);
            }
        }

        if(this.shooter != null){
            if(this.distanceTo(shooter)>9){
                this.remove();
            }
        }

    }

    @Override
    protected void afterEntityHit(EntityRayTraceResult entityRayTraceResult, boolean entityHurt){
        Entity entity = entityRayTraceResult.getEntity();
        if(entity instanceof LivingEntity){
            LivingEntity living =  (LivingEntity) entity;
            if(this.position().y <= living.position().y+5*living.getBbHeight()/9){
                living.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN,60,1));
                living.addEffect(new EffectInstance(ModStatusEffects.MISSHAPEN_LEGS.get(),30));
            }
            if(this.position().y > living.position().y+15*living.getBbHeight()/18){
                living.addEffect(new EffectInstance(Effects.BLINDNESS,30,1));
            }
        }
    }



    @Override
    public int ticksLifespan() {
        return 20;
    }

    @Override
    protected float getBaseDamage() {
        return 1;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0;
    }

    @Override
    public boolean standDamage() {
        return true;
    }

    @Override
    protected float knockbackMultiplier() {
        return 0.1F;
    }
}
