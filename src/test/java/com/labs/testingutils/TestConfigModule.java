package com.labs.testingutils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class TestConfigModule extends AbstractModule {
    @Singleton
    @Provides
    public SessionFactory provideSessionFactory(){
        FakeDB db = new FakeDB();
        return db.getDAO();
    }

    @Provides
    public Session provideSession(SessionFactory factory){
        return factory.openSession();
    }
}
