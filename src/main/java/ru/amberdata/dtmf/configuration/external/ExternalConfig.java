package ru.amberdata.dtmf.configuration.external;

import ru.amberdata.dtmf.configuration.external.Elemental.Channel;

import java.util.List;

/**
 * Created by zhenya on 2016-12-24.
 */
public interface ExternalConfig {
    String getModuleType();
    void setModuleType(String moduleType);
    List<Channel> getChannel();
    void setChannel(List<Channel> channel);
}
