package com.zeml.rotp_zcs.init;

import com.zeml.rotp_zcs.entity.stand.stands.CSEntity;
import com.github.standobyte.jojo.power.impl.stand.stats.StandStats;
import com.github.standobyte.jojo.entity.stand.StandEntityType;
import com.github.standobyte.jojo.init.power.stand.EntityStandRegistryObject.EntityStandSupplier;
import com.github.standobyte.jojo.power.impl.stand.type.EntityStandType;

public class AddonStands {

    public static final EntityStandSupplier<EntityStandType<StandStats>, StandEntityType<CSEntity>>
            CREAM_STARTER_STAND = new EntityStandSupplier<>(InitStands.STAND_CREAM_STARTER);
}