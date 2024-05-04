package com.zeml.rotp_zcs.init;

import com.github.standobyte.jojo.init.ModSounds;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.github.standobyte.jojo.util.mc.OstSoundList;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CreamStarterAddon.MOD_ID);

    public static final RegistryObject<SoundEvent> CS_SUMMON = ModSounds.STAND_SUMMON_DEFAULT;

    public static final RegistryObject<SoundEvent> VOID =SOUNDS.register("void",
            ()->new SoundEvent(new ResourceLocation(CreamStarterAddon.MOD_ID,"void")));

    public static final RegistryObject<SoundEvent> CS_UNSUMMON = ModSounds.STAND_UNSUMMON_DEFAULT;


    public static final RegistryObject<SoundEvent> USER_CS = SOUNDS.register("hot_cs",
            ()->new SoundEvent(new ResourceLocation(CreamStarterAddon.MOD_ID, "hot_cs"))
            );

    public static final RegistryObject<SoundEvent> CS_SPRAY = SOUNDS.register("cs_shot",
            ()->new SoundEvent(new ResourceLocation(CreamStarterAddon.MOD_ID, "cs_shot"))
    );

    public static final RegistryObject<SoundEvent> CS_SHAKE = SOUNDS.register("cs_shake",
            ()->new SoundEvent(new ResourceLocation(CreamStarterAddon.MOD_ID, "cs_shake"))
    );

    static final OstSoundList HOT_OST = new OstSoundList(new ResourceLocation(CreamStarterAddon.MOD_ID, "cs_ost"), SOUNDS);
}
