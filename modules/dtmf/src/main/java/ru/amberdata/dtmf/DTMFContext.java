package ru.amberdata.dtmf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import ru.amberdata.dtmf.action.Action;
import ru.amberdata.dtmf.configuration.dtmf.DTMFDetectorConfig;
import ru.amberdata.dtmf.configuration.Utils;
import ru.amberdata.dtmf.configuration.external.Elemental.ElementalConfig;
import ru.amberdata.dtmf.configuration.external.ExternalConfig;

/**
 * Created by zhenya on 2016-12-16.
 */
@Singleton
public class DTMFContext {

    private static final String DTMF_CONF = "./dtmfConfig.xml";
    private static final String MGMT_CONF = "./managementConfig.xml";
    private Action externalAction;

    private static final DTMFDetectorConfig DTMF_CONFIG = Utils.importConfiguration(DTMF_CONF, DTMFDetectorConfig.class);

    private static final ExternalConfig MANAGE_CONFIG = Utils.importConfiguration(MGMT_CONF, ElementalConfig.class);

    public DTMFContext() {
    }

    @Inject
    public void setExternalAction(@Named("externalAction") Action externalAction) {
        this.externalAction = externalAction;
    }

    public Action getExternalAction() {
        return externalAction;
    }

    public static DTMFDetectorConfig getDtmfConfig() {
        return DTMF_CONFIG;
    }

    public static ExternalConfig getManageConfig() {
        return MANAGE_CONFIG;
    }
}
