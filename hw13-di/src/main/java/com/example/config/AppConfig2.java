package com.example.config;

import com.example.appcontainer.api.AppComponent;
import com.example.appcontainer.api.AppComponentsContainerConfig;
import com.example.services.*;

@AppComponentsContainerConfig(order = 2)
public class AppConfig2 {
    @AppComponent(order = 2, name = "gameProcessor")
    public GameProcessor gameProcessor(IOService ioService,
                                       PlayerService playerService,
                                       EquationPreparer equationPreparer) {
        return new GameProcessorImpl(ioService, equationPreparer, playerService);
    }

    @AppComponent(order = 0, name = "ioService")
    public IOService ioService() {
        return new IOServiceStreams(System.out, System.in);
    }

}
