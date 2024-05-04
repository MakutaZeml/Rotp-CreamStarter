package com.zeml.rotp_zcs.client;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.particle.HamonSparkParticle;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.client.render.entity.renderer.damaging.projectile.SprayHealRenderer;
import com.zeml.rotp_zcs.client.render.entity.renderer.damaging.projectile.SprayRenderer;
import com.zeml.rotp_zcs.client.render.entity.renderer.stand.CreamStarterRenderer;
import com.zeml.rotp_zcs.init.AddonStands;

import com.zeml.rotp_zcs.init.InitEntities;
import com.zeml.rotp_zcs.init.InitParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = CreamStarterAddon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientInit {

    private static final IItemPropertyGetter STAND_ITEM_INVISIBLE = (itemStack, clientWorld, livingEntity) -> {
        return !ClientUtil.canSeeStands() ? 1 : 0;
    };

    @SubscribeEvent
    public static void onFMLClientSetup(FMLClientSetupEvent event) {
        Minecraft mc = event.getMinecraftSupplier().get();;

        RenderingRegistry.registerEntityRenderingHandler(AddonStands.CREAM_STARTER_STAND.getEntityType(), CreamStarterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.CREAM_SPRAY.get(), SprayRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(InitEntities.CREAM_HEAL.get(), SprayHealRenderer::new);

    }


    @SubscribeEvent
    public static void onMcConstructor(ParticleFactoryRegisterEvent event){
        Minecraft mc = Minecraft.getInstance();

        mc.particleEngine.register(InitParticles.MEAT.get(), HamonSparkParticle.HamonParticleFactory::new);


    }

}
