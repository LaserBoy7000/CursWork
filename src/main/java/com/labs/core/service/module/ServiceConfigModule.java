package com.labs.core.service.module;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ServiceConfigModule extends AbstractModule {
    
    @Singleton
    @Provides
    public SessionFactory provideSessionFactory(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
			.configure()
			.build();
        try {
            return (SessionFactory)new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
            throw e;
        }
    }

    @Provides
    public Session provideSession(SessionFactory factory){
        return factory.openSession();
    }
}
