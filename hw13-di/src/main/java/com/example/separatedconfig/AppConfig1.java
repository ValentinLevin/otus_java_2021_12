package com.example.separatedconfig;

import com.example.appcontainer.api.AppComponent;
import com.example.appcontainer.api.AppComponentsContainerConfig;
import com.example.services.*;

@AppComponentsContainerConfig(order = 1)
public class AppConfig1 {

    @AppComponent(order = 0, name = "equationPreparer")
    public EquationPreparer equationPreparer(){
        return new EquationPreparerImpl();
    }

    @AppComponent(order = 1, name = "playerService")
    public PlayerService playerService(IOService ioService) {
        return new PlayerServiceImpl(ioService);
    }
}
