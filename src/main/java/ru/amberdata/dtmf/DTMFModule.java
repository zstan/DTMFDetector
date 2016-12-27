package ru.amberdata.dtmf;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import ru.amberdata.dtmf.action.Action;
import ru.amberdata.dtmf.action.HttpPostAction;

/**
 * Created by zstan on 26.12.16.
 */
public class DTMFModule extends AbstractModule {

    //private Action httpAction = new HttpPostAction();

    @Override
    protected void configure() {
        /*bind(Action.class)
                .annotatedWith(Names.named("externalAction"))
                .toInstance(httpAction);*/
        bind(Action.class)
                .annotatedWith(Names.named("externalAction"))
                .to(HttpPostAction.class);
    }
}
