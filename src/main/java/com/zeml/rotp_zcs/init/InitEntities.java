package com.zeml.rotp_zcs.init;

import com.github.standobyte.jojo.JojoMod;
import com.zeml.rotp_zcs.CreamStarterAddon;

import com.zeml.rotp_zcs.entity.damaging.projectile.HealSprayEntity;
import com.zeml.rotp_zcs.entity.damaging.projectile.SprayEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, CreamStarterAddon.MOD_ID);

    public static final RegistryObject<EntityType<SprayEntity>> CREAM_SPRAY = ENTITIES.register("spray_bullet",
            () -> EntityType.Builder.<SprayEntity>of(SprayEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).setUpdateInterval(20).fireImmune()
                    .build(new ResourceLocation(JojoMod.MOD_ID, "spray_bullet").toString()));

    public static final RegistryObject<EntityType<HealSprayEntity>> CREAM_HEAL = ENTITIES.register("heal_spray",
            () -> EntityType.Builder.<HealSprayEntity>of(HealSprayEntity::new, EntityClassification.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).setUpdateInterval(20).fireImmune()
                    .build(new ResourceLocation(JojoMod.MOD_ID, "heal_spray").toString()));
}
