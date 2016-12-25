package ru.amberdata.dtmf;

import ru.amberdata.dtmf.configuration.dtmf.DTMFDetectorConfig;
import ru.amberdata.dtmf.configuration.Utils;
import ru.amberdata.dtmf.configuration.external.Elemental.ElementalConfig;
import ru.amberdata.dtmf.configuration.external.IExternalConfig;

/**
 * Created by zhenya on 2016-12-16.
 */
public class DTMFContext {

    private static final String DTMF_CONF = "dtmfConfig.xml";
    private static final String MGMT_CONF = "managementConfig";
    private static DTMFContext instance;

    public static final DTMFDetectorConfig DTMF_CONFIG = Utils.importConfiguration(DTMF_CONF, DTMFDetectorConfig.class);

    public static final IExternalConfig MANAGE_CONFIG = Utils.importConfiguration(MGMT_CONF, ElementalConfig.class);

    private DTMFContext() {

    }

    public static DTMFContext getDTMFContext() {
        if (instance == null)
            instance = new DTMFContext();
        return instance;
    }
}
