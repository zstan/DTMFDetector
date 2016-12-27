package ru.amberdata.dtmf.action;

import ru.amberdata.dtmf.configuration.dtmf.Channel;

/**
 * Created by zstan on 20.12.16.
 */
public interface Action {
    boolean apply(Channel data);
}
