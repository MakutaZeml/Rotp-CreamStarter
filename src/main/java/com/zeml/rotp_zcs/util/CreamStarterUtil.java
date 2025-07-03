package com.zeml.rotp_zcs.util;

import com.github.standobyte.jojo.JojoModConfig;
import com.github.standobyte.jojo.util.mc.reflection.CommonReflection;
import com.zeml.rotp_zcs.item.MeatMaskItem;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.Set;
import java.util.function.Predicate;

public class CreamStarterUtil {

    public static void editMobAiGoals(MobEntity mob) {
        if (mob.getClassification(false) == EntityClassification.MONSTER) {
            makeMobsNeutral(mob);
        }
        else if (mob instanceof IronGolemEntity) {
            mob.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, PlayerEntity.class, 5, false, false,
                    target -> (target.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof  MeatMaskItem) &&
                            ((MeatMaskItem)target.getItemBySlot(EquipmentSlotType.HEAD).getItem()).getHostile(target.getItemBySlot(EquipmentSlotType.HEAD))));
        }
    }

    private static void makeMobsNeutral(MobEntity mob) {
        if (JojoModConfig.getCommonConfigInstance(false).vampiresAggroMobs.get()) return;

        Set<PrioritizedGoal> goals = CommonReflection.getGoalsSet(mob.targetSelector);
        for (PrioritizedGoal prGoal : goals) {
            Goal goal = prGoal.getGoal();
            if (goal instanceof NearestAttackableTargetGoal) {
                NearestAttackableTargetGoal<?> targetGoal = (NearestAttackableTargetGoal<?>) goal;
                Class<? extends LivingEntity> targetClass = CommonReflection.getTargetClass(targetGoal);

                if (targetClass == PlayerEntity.class) {
                    EntityPredicate selector = CommonReflection.getTargetConditions(targetGoal);
                    if (selector != null) {
                        Predicate<LivingEntity> oldPredicate = CommonReflection.getTargetSelector(selector);
                        Predicate<LivingEntity> undeadPredicate = target ->
                                (target.getItemBySlot(EquipmentSlotType.HEAD).getItem() instanceof  MeatMaskItem) &&
                                        ((MeatMaskItem)target.getItemBySlot(EquipmentSlotType.HEAD).getItem()).getHostile(target.getItemBySlot(EquipmentSlotType.HEAD));
                        CommonReflection.setTargetConditions(targetGoal, new EntityPredicate().range(CommonReflection.getTargetDistance(targetGoal)).selector(
                                oldPredicate != null ? oldPredicate.and(undeadPredicate) : undeadPredicate));
                    }
                }
            }
        }
    }



}
