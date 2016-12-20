package ru.amberdata.dtmf;

import ru.amberdata.dtmf.configuration.Channel;
import ru.amberdata.dtmf.http.IAction;

import java.util.function.Function;

/**
 * Created by zstan on 20.12.16.
 */
public class AdBreaksManager {

    private final Channel ch;
    private Function<? extends IAction, Boolean> action;

    public AdBreaksManager(Channel ch) {
        this.ch = ch;
    }
}
