package com.labs.core.service;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
//Services access point, should be used within commands!
public class DependenciesInjector {
    private Injector Injector;

    public DependenciesInjector(AbstractModule config) {
        Injector = Guice.createInjector(config);
    }

    public Injector getInjector(){
        return Injector;
    }

    public <T> T get(Class<T> type){
        return Injector.getInstance(type);
    }
}
