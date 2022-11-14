package com.labs;

import java.util.logging.Level;
import com.labs.UI.impl.jfx.MainFX;
import com.labs.core.service.DependenciesInjector;
import com.labs.core.service.module.ServiceConfigModule;

public class Main 
{
    public static void main(String[] args)
    {
       java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
       MainFX.setDI(new DependenciesInjector(new ServiceConfigModule()));
       MainFX.main(args);
    }
}
