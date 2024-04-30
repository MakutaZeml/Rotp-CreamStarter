package com.zeml.rotp_zcs.init;

import com.github.standobyte.jojo.JojoMod;
import com.zeml.rotp_zcs.CreamStarterAddon;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, CreamStarterAddon.MOD_ID);
    

}
