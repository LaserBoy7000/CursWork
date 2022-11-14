package com.labs.testingutils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class FakeDB {
    private final SessionFactory fact;
    
    public FakeDB(){
    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
			.configure("com/labs/testingutils/testdbconfig.cfg.xml")
			.build();
        try {
            fact = (SessionFactory)new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            StandardServiceRegistryBuilder.destroy( registry );
            throw e;
        }
    }
    
    public SessionFactory getDAO(){
        return fact;
    }
}
