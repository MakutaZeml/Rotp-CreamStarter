package com.zeml.rotp_zcs.init;

import com.zeml.rotp_zcs.CreamStarterAddon;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, CreamStarterAddon.MOD_ID);


    public static final RegistryObject<BasicParticleType> MEAT_CREAM = PARTICLES.register("meat_cream", () -> new BasicParticleType(false));



}
