package com.zeml.rotp_zcs.action;

import com.github.standobyte.jojo.action.stand.StandEntityAction;

public class GiveCreamStarter extends StandEntityAction {

    public GiveCreamStarter(StandEntityAction.Builder builder) {
        super(builder);
    }

    @Override
    public boolean enabledInHudDefault() {
        return false;
    }

}
