package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.ActionConditionResult;
import com.github.standobyte.jojo.action.ActionTarget;
import com.github.standobyte.jojo.action.stand.CrazyDiamondLeaveObject;
import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.github.standobyte.jojo.action.stand.StandEntityActionModifier;
import com.github.standobyte.jojo.entity.EyeOfEnderInsideEntity;
import com.github.standobyte.jojo.entity.stand.StandEntity;
import com.github.standobyte.jojo.entity.stand.StandEntityTask;
import com.github.standobyte.jojo.entity.stand.TargetHitPart;
import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.item.StandArrowItem;
import com.github.standobyte.jojo.modcompat.ModInteractionUtil;
import com.github.standobyte.jojo.power.impl.nonstand.type.vampirism.VampirismUtil;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.github.standobyte.jojo.util.mc.damage.DamageUtil;
import com.zeml.rotp_zcs.init.InitSounds;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.EyeOfEnderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EntityTeleportEvent;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class PutItemAction extends StandEntityAction {
    static final Map<Predicate<ItemStack>, TriConsumer<LivingEntity, ItemStack, LivingEntity>> ITEM_ACTION = Util.make(new HashMap<>(), map -> {
        map.put(item -> item.getItem() == Items.CHORUS_FRUIT, (target, item, user) ->           chorusFruitTeleport(target, user));
        map.put(item -> item.getItem() == Items.ENDER_EYE, (target, item, user) ->              enderEyeFlight(target, item, user));
        map.put(item -> item.getItem() == Items.SNOWBALL, (target, item, user) ->               target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 40, 0)));
        map.put(item -> item.getItem() == Items.SNOW, (target, item, user) ->                   target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 80, 0)));
        map.put(item -> item.getItem() == Items.SNOW_BLOCK, (target, item, user) ->             target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 120, 1)));
        map.put(item -> item.getItem() == Items.ICE, (target, item, user) ->                    target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 200, 1)));
        map.put(item -> item.getItem() == Items.PACKED_ICE, (target, item, user) ->             target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 200, 2)));
        map.put(item -> item.getItem() == Items.BLUE_ICE, (target, item, user) ->               target.addEffect(new EffectInstance(ModStatusEffects.FREEZE.get(), 200, 3)));
        map.put(item -> item.getItem() == Items.FIRE_CHARGE, (target, item, user) ->            DamageUtil.setOnFire(target, 5, false));
        map.put(item -> item.getItem() == Items.BLAZE_POWDER, (target, item, user) ->           DamageUtil.setOnFire(target, 10, false));
        map.put(item -> item.getItem() == Items.BLAZE_ROD, (target, item, user) ->              DamageUtil.setOnFire(target, 20, false));
        map.put(item -> item.getItem() == Items.LAVA_BUCKET, (target, item, user) ->            DamageUtil.setOnFire(target, 20, false));
        map.put(item -> item.getItem() == Items.GLOWSTONE_DUST, (target, item, user) ->         target.addEffect(new EffectInstance(Effects.GLOWING, 100)));
        map.put(item -> item.getItem() == Items.SPECTRAL_ARROW, (target, item, user) ->         target.addEffect(new EffectInstance(Effects.GLOWING, 200)));
        map.put(item -> item.getItem() == Items.GLOWSTONE, (target, item, user) ->              target.addEffect(new EffectInstance(Effects.GLOWING, 400)));
        map.put(item -> item.getItem() == Items.MILK_BUCKET, (target, item, user) ->            target.curePotionEffects(item));
        map.put(item -> !PotionUtils.getMobEffects(item).isEmpty(), (target, item, user) ->     PotionUtils.getMobEffects(item).forEach(effect -> target.addEffect(effect)));
        map.put(item -> item.isEdible(), (target, item, user) ->                                target.eat(target.level, item.copy()));
        map.put(item -> item.getItem() == Items.ENCHANTED_GOLDEN_APPLE, (target, item, user) -> VampirismUtil.onEnchantedGoldenAppleEaten(target));
    });
    static final Map<Predicate<ItemStack>, TriPredicate<LivingEntity, ItemStack, LivingEntity>> ITEM_ACTION_CONDITIONAL = Util.make(new HashMap<>(), map -> {
        map.put(item -> item.getItem() instanceof StandArrowItem, (target, item, user) ->       StandArrowItem.onPiercedByArrow(target, item, target.level, Optional.of(user)));
    });


    public PutItemAction(StandEntityAction.Builder builder){
        super(builder);
    }

    @Override
    protected ActionConditionResult checkSpecificConditions(LivingEntity user, IStandPower power, ActionTarget target) {
        if (power.isActive() && target.getEntity() instanceof LivingEntity){
            ItemStack item = user.getOffhandItem();
            return ActionConditionResult.noMessage(!item.isEmpty() && canUseItem(item));
        }
        return ActionConditionResult.NEGATIVE;
    }

    @Override
    public void standPerform(World world, StandEntity standEntity, IStandPower userPower, StandEntityTask task) {
        Entity entity = task.getTarget().getEntity();
        ItemStack item = userPower.getUser().getOffhandItem();
        if(entity instanceof  LivingEntity && !item.isEmpty()){
            LivingEntity targetEntity = (LivingEntity) entity;
            boolean itemUsed = false;
            boolean itemAction1 = ITEM_ACTION.entrySet().stream().anyMatch(entry -> {
                boolean triggered = false;
                Predicate<ItemStack> itemCheck = entry.getKey();
                TriConsumer<LivingEntity, ItemStack, LivingEntity> itemEffect = entry.getValue();
                if (!world.isClientSide() && itemCheck.test(item)) {
                    itemEffect.accept(targetEntity, item, userPower.getUser());
                    triggered = true;
                }
                return triggered;
            });
            boolean itemAction2 = ITEM_ACTION_CONDITIONAL.entrySet().stream().anyMatch(entry -> {
                boolean triggered = false;
                Predicate<ItemStack> itemCheck = entry.getKey();
                TriPredicate<LivingEntity, ItemStack, LivingEntity> itemEffect = entry.getValue();
                if (!world.isClientSide() && itemCheck.test(item) && itemEffect.test(targetEntity, item, userPower.getUser())) {
                    triggered = true;
                }
                return triggered;
            });
            itemUsed = itemAction1 || itemAction2;
            if (itemUsed) {
                world.playSound(null,userPower.getUser().blockPosition(), InitSounds.CS_SPRAY.get(),SoundCategory.PLAYERS, 1F,1F);
                item.shrink(1);
            }
        }
    }


    @Override
    protected void onTaskStopped(World world, StandEntity standEntity, IStandPower standPower, StandEntityTask task, @Nullable StandEntityAction newAction) {
        standPower.getUser().getMainHandItem().releaseUsing(world,standPower.getUser(),0);
        super.onTaskStopped(world, standEntity, standPower, task, newAction);
    }

    @Override
    public TargetRequirement getTargetRequirement() {
        return TargetRequirement.ENTITY;
    }

    private static void chorusFruitTeleport(LivingEntity target, LivingEntity user) {
        if (ModInteractionUtil.isEntityEnderman(target)) {
            target.heal(6);
            return;
        }
        double xPrev = target.getX();
        double yPrev = target.getY();
        double zPrev = target.getZ();

        for(int tpTry = 0; tpTry < 16; ++tpTry) {
            Random random = target.getRandom();
            double x;
            double y = MathHelper.clamp(target.getY() + (double)(random.nextInt(16) - 8), 0.0D, (double)(target.level.getHeight() - 1));
            double z;
            if (user != null) {
                Vector3d middlePos = target.position().add(user.getLookAngle().scale(12));
                x = middlePos.x + (random.nextDouble() - 0.5) * 8.0;
                z = middlePos.z + (random.nextDouble() - 0.5) * 8.0;
            }
            else {
                x = xPrev + (random.nextDouble() + 1.0) * (random.nextBoolean() ? 1 : -1) * 8.0;
                z = zPrev + (random.nextDouble() + 1.0) * (random.nextBoolean() ? 1 : -1) * 8.0;
            }
            if (target.isPassenger()) {
                target.stopRiding();
            }

            EntityTeleportEvent.ChorusFruit event = ForgeEventFactory.onChorusFruitTeleport(target, x, y, z);
            if (!event.isCanceled() && target.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
                SoundEvent soundevent = target instanceof FoxEntity ? SoundEvents.FOX_TELEPORT : SoundEvents.CHORUS_FRUIT_TELEPORT;
                target.yRot = random.nextFloat() * 360F;
                target.yRotO = target.yRot;
                target.level.playSound((PlayerEntity)null, xPrev, yPrev, zPrev, soundevent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                target.playSound(soundevent, 1.0F, 1.0F);
                break;
            }
        }
    }

    private static void enderEyeFlight(LivingEntity entity, ItemStack item, LivingEntity user) {
        if (!entity.level.isClientSide()) {
            ServerWorld world = (ServerWorld) entity.level;
            BlockPos strongholdPos = world.getChunkSource().getGenerator().findNearestMapFeature(world, Structure.STRONGHOLD, entity.blockPosition(), 100, false);
            if (strongholdPos != null) {
                EyeOfEnderEntity eyeOfEnder = new EyeOfEnderInsideEntity(entity.level, entity);

                eyeOfEnder.setItem(item);
                eyeOfEnder.signalTo(strongholdPos);

                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENDER_EYE_LAUNCH,
                        SoundCategory.NEUTRAL, 0.5F, 0.4F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
                world.levelEvent(null, 1003, entity.blockPosition(), 0);

                world.addFreshEntity(eyeOfEnder);

                if (entity instanceof ServerPlayerEntity) {
                    CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayerEntity) entity, strongholdPos);
                }
                if (user instanceof ServerPlayerEntity) {
                    CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayerEntity) user, strongholdPos);
                }
            }
        }
    }

    static boolean canUseItem(ItemStack itemStack) {
        return ITEM_ACTION.keySet().stream().anyMatch(predicate -> predicate.test(itemStack))
                || ITEM_ACTION_CONDITIONAL.keySet().stream().anyMatch(predicate -> predicate.test(itemStack));
    }
}
