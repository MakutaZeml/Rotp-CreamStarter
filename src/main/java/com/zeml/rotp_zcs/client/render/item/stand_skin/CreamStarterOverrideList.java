package com.zeml.rotp_zcs.client.render.item.stand_skin;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.zeml.rotp_zcs.CreamStarterAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreamStarterOverrideList extends ItemOverrideList {
    private final Map<String, IBakedModel> cache = new HashMap<>();
    private final ItemOverrideList wrappedOverrides;
    private static final List<StandSkin> standSkins = new ArrayList<>();

    public CreamStarterOverrideList(ItemOverrideList wrappedOverrides) {
        this.wrappedOverrides = wrappedOverrides;
    }

    @Override
    public IBakedModel resolve(IBakedModel model, ItemStack item, @Nullable ClientWorld world, @Nullable LivingEntity entity){
        CompoundNBT nbt = item.getTag();
        if(nbt != null && nbt.contains("standSkin") && !nbt.getString("standSkin").isEmpty()){
            ItemModelMesher itemModelShaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
            String path =  nbt.getString("standSkin").split(":")[1];
            IBakedModel standSpecificModel = cache.computeIfAbsent(path, standId -> itemModelShaper.getModelManager().getModel(makeStandSpecificModelPath(path)));
            if (standSpecificModel != null && !ClientUtil.isMissingModel(standSpecificModel, itemModelShaper)) {
                model = standSpecificModel;
            }
        }
        CreamStarterAddon.LOGGER.debug("Overrides {}, map {}", wrappedOverrides.resolve(model, item, world, entity),cache);

        return wrappedOverrides.resolve(model, item, world, entity);
    }

    public static void onGettingStandSkins(StandSkin standSkin) {
        CreamStarterAddon.LOGGER.debug("Started {}?", standSkin);
        ModelLoader.addSpecialModel(makeStandSpecificModelPath(standSkin.resLoc.getPath()));

    }



    public static ResourceLocation makeStandSpecificModelPath(String path) {
        String location = "item/cream_starter_"+path;
        CreamStarterAddon.LOGGER.debug("Is this working {} {}, this should be twice", path, new ResourceLocation(CreamStarterAddon.MOD_ID,location));
        return new ResourceLocation(CreamStarterAddon.MOD_ID,location);
    }


}
