package ru.amberdata.dtmf;

import ru.amberdata.dtmf.configuration.dtmf.DTMFDetectorConfig;
import ru.amberdata.dtmf.configuration.Utils;

/**
 * Created by zhenya on 2016-12-16.
 */
public class DTMFContext {

    private static String GENERAL_CONF = "dtmfconfig.xml";

    public static DTMFDetectorConfig dtmfConfig = Utils.importConfiguration(GENERAL_CONF, DTMFDetectorConfig.class);
}
