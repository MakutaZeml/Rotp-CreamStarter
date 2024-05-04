package com.zeml.rotp_zcs.entity.damaging.projectile;

import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.CrazyDiamondHeal;
import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.entity.damaging.projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.init.ModParticles;
import com.zeml.rotp_zcs.init.InitEntities;
import com.zeml.rotp_zcs.init.InitItems;
import com.zeml.rotp_zcs.init.InitParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class HealSprayEntity extends ModdedProjectileEntity {
    LivingEntity shooter;
    public HealSprayEntity(LivingEntity shooter, World world){
        super(InitEntities.CREAM_SPRAY.get(),shooter, world);
        this.shooter = shooter;
    }
    public HealSprayEntity(EntityType<HealSprayEntity> spray, World world) {
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
        if(entity instanceof LivingEntity && !(entity instanceof IronGolemEntity) &&
                !(entity instanceof SnowGolemEntity) && !(entity instanceof SkeletonEntity) &&
                !(entity instanceof WitherSkeletonEntity) && !(entity instanceof SkeletonHorseEntity) ){
            LivingEntity living = (LivingEntity) entity;
            living.heal(0.5F);

            if (ClientUtil.canSeeStands()){
                CrazyDiamondHeal.addParticlesAround(entity);
                level.addParticle(ModParticles.CD_RESTORATION.get(),
                        entity.position().x + (random.nextDouble() - 0.5) * 0.25,
                        entity.position().y + (random.nextDouble() - 0.5) * 0.25,
                        entity.position().z + (random.nextDouble() - 0.5) * 0.25,
                        0, 0, 0);

            }

        }
        if(entity instanceof SkeletonEntity){
            if(ticksTrans(this.shooter,10)){
                ZombieEntity zombie = new ZombieEntity(entity.level);
                zombie.setPos(entity.getX(),entity.getY(),entity.getZ());
                zombie.setItemSlot(EquipmentSlotType.CHEST,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.CHEST));
                zombie.setItemSlot(EquipmentSlotType.FEET,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.FEET));
                zombie.setItemSlot(EquipmentSlotType.HEAD,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.HEAD));
                zombie.setItemSlot(EquipmentSlotType.LEGS,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.LEGS));
                zombie.setItemSlot(EquipmentSlotType.MAINHAND,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.MAINHAND));
                zombie.setItemSlot(EquipmentSlotType.OFFHAND,((SkeletonEntity) entity).getItemBySlot(EquipmentSlotType.OFFHAND));
                zombie.setHealth(((SkeletonEntity) entity).getHealth());
                entity.level.addFreshEntity(zombie);
                entity.remove();
            }
        }
        if(entity instanceof StrayEntity){
            if(ticksTrans(this.shooter,10)){
                ZombieEntity zombie = new ZombieEntity(entity.level);
                zombie.setPos(entity.getX(),entity.getY(),entity.getZ());
                zombie.setItemSlot(EquipmentSlotType.CHEST,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.CHEST));
                zombie.setItemSlot(EquipmentSlotType.FEET,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.FEET));
                zombie.setItemSlot(EquipmentSlotType.HEAD,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.HEAD));
                zombie.setItemSlot(EquipmentSlotType.LEGS,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.LEGS));
                zombie.setItemSlot(EquipmentSlotType.MAINHAND,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.MAINHAND));
                zombie.setItemSlot(EquipmentSlotType.OFFHAND,((StrayEntity) entity).getItemBySlot(EquipmentSlotType.OFFHAND));
                zombie.setHealth(((StrayEntity) entity).getHealth());
                entity.level.addFreshEntity(zombie);
                entity.remove();
            }

        }

        if(entity instanceof SkeletonHorseEntity){

            if(ticksTrans(this.shooter,30)){
                ZombieHorseEntity zombieHorse = new ZombieHorseEntity(EntityType.ZOMBIE_HORSE ,entity.level);
                zombieHorse.setTamed(true);
                if(((SkeletonHorseEntity) entity).isSaddled()){
                    zombieHorse.equipSaddle(SoundCategory.PLAYERS);
                }
                zombieHorse.setPos(entity.getX(),entity.getY(),entity.getZ());
                zombieHorse.setHealth(((SkeletonHorseEntity) entity).getHealth());
                level.addFreshEntity(zombieHorse);
                entity.remove();
            }

        }
    }

    @Override
    public int ticksLifespan() {
        return 20;
    }

    @Override
    protected float getBaseDamage() {
        return -0.25F;
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
        return 0.0F;
    }

    @Override
    protected boolean hurtTarget(Entity target, LivingEntity owner){
        return false;
    }

    @Override
    protected void breakProjectile(ActionTarget.TargetType targetType, RayTraceResult hitTarget) {
        if (targetType == ActionTarget.TargetType.BLOCK || targetType == ActionTarget.TargetType.ENTITY) {
            super.breakProjectile(targetType, hitTarget);
        }
    }

    protected static boolean ticksTrans(LivingEntity user ,int limit){
        if(user.getItemInHand(Hand.MAIN_HAND).getItem() == InitItems.CREAM_STARTER.get()){
            CompoundNBT nbt = user.getItemInHand(Hand.MAIN_HAND).getTag();
            if(nbt == null ){
                nbt = new CompoundNBT();
                nbt.putInt("ticks",0);
            }
            if(nbt.getInt("ticks") >= limit){
                return true;
            }else {
                nbt.putInt("ticks",nbt.getInt("ticks")+1);
            }
            return false;

        } else if (user.getItemInHand(Hand.OFF_HAND).getItem() == InitItems.CREAM_STARTER.get()) {
            CompoundNBT nbt = user.getItemInHand(Hand.OFF_HAND).getTag();
            if(nbt == null ){
                nbt = new CompoundNBT();
                nbt.putInt("ticks",0);
            }
            if(nbt.getInt("ticks") >= limit){
                return true;
            }else {
                nbt.putInt("ticks",nbt.getInt("ticks")+1);
            }
            return false;
        }
        return false;
    }

}
