package com.zeml.rotp_zcs.init;

import com.github.standobyte.jojo.action.stand.StandEntityAction;
import com.zeml.rotp_zcs.CreamStarterAddon;
import com.zeml.rotp_zcs.action.GiveCreamStarter;
import com.zeml.rotp_zcs.entity.stand.stands.CSEntity;
import com.github.standobyte.jojo.init.power.stand.ModStandsInit;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;

import com.github.standobyte.jojo.action.Action;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;
import com.github.standobyte.jojo.power.impl.stand.type.StandType;

import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class InitStands {
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<Action<?>> ACTIONS = DeferredRegister.create(
            (Class<Action<?>>) ((Class<?>) Action.class), CreamStarterAddon.MOD_ID);
    @SuppressWarnings("unchecked")
    public static final DeferredRegister<StandType<?>> STANDS = DeferredRegister.create(
            (Class<StandType<?>>) ((Class<?>) StandType.class), CreamStarterAddon.MOD_ID);

    // ======================================== The Emperor ========================================


    public static final RegistryObject<StandEntityAction> CS_GIVE = ACTIONS.register("give_cs",
            ()->new GiveCreamStarter(new StandEntityAction.Builder().staminaCost(1)));



    public static final EntityStandRegistryObject<EntityStandType<StandStats>, StandEntityType<CSEntity>> STAND_CREAM_STARTER =
            new EntityStandRegistryObject<>("cream_starter",
                    STANDS,
                    () -> new EntityStandType.Builder<StandStats>()
                            .color(0xF6F095)
                            .storyPartName(ModStandsInit.PART_7_NAME)
                            .leftClickHotbar(
                            )
                            .rightClickHotbar(
                                    CS_GIVE.get()
                            )
                            .defaultStats(StandStats.class, new StandStats.Builder()
                                    .tier(2)
                                    .power(6.0)
                                    .speed(10.0)
                                    .range(9.0)
                                    .durability(16.0)
                                    .precision(2.0)
                                    .randomWeight(2)
                            )
                            .addOst(InitSounds.HOT_OST)
                            .disableManualControl().disableStandLeap()
                            .addSummonShout(InitSounds.USER_CS)
                            .build(),

                    InitEntities.ENTITIES,
                    () -> new StandEntityType<CSEntity>(CSEntity::new, 0.65F, 1.8F)
                            .summonSound(InitSounds.CS_SUMMON)
                            .unsummonSound(InitSounds.CS_UNSUMMON))
                    .withDefaultStandAttributes();
}
