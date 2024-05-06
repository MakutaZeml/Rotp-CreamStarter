package com.zeml.rotp_zcs;

import com.zeml.rotp_zcs.init.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CreamStarterAddon.MOD_ID)
public class CreamStarterAddon {
    // The value here should match an entry in the META-INF/mods.toml file
    public static final String MOD_ID = "rotp_zcs";
    public static final Logger LOGGER = LogManager.getLogger();

    public CreamStarterAddon() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        InitEntities.ENTITIES.register(modEventBus);
        InitSounds.SOUNDS.register(modEventBus);
        InitStands.ACTIONS.register(modEventBus);
        InitStands.STANDS.register(modEventBus);
        InitItems.ITEMS.register(modEventBus);
        InitParticles.PARTICLES.register(modEventBus);

        IntTags.iniTags();
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
