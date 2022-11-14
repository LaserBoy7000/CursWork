package com.labs.core.service.impl;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.labs.core.entity.User;
import com.labs.core.helper.ActionEvent;
import com.labs.core.service.IIdentityService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Singleton
public class IdentityService implements IIdentityService {
    private SessionFactory DAOFactory;
    private User CurentUser;
    private final ActionEvent event = new ActionEvent();
    private String Status = "";

    @Inject
    public IdentityService(SessionFactory factory){
        DAOFactory = factory;
    }
    
    @Override
    public boolean isUserAvailable() {
        return CurentUser != null;
    }

    @Override
    public User getCurrentUser() {
        return CurentUser;
    }

    @Override
    public boolean logOut() {
        if(CurentUser == null){
            setStatus("WARNING: No user detected to log out");
            return false;
        }
        CurentUser = null;
        event.InvokeAll();
        return true;
    }

    private User find(String name, String surname, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rt = cq.from(User.class);
        CriteriaQuery<User> all = cq.select(rt);
        all = all.where(cb.equal(rt.get("Name"), name), cb.equal(rt.get("Surname"), surname));
        List<User> rs = session.createQuery(cq).list();
        if(rs.size() == 0)
            return null;
        return  rs.stream().findFirst().get();
    }

    @Override
    public boolean register(String name, String surname, String password) {
        Session s = DAOFactory.openSession();
        User us = find(name, surname, s);

        if(us == null){
            User usr = new User();
            usr.setName(name);
            usr.setSurname(surname);
            usr.setPassword(password);
            s.persist(usr);
            s.flush();
            s.close();
            CurentUser = usr;
            event.InvokeAll();
            return true;
        }

        if(!us.getPassword().equals(password)){
            setStatus("WARNING: Incorrect password for user " + name + ' ' + surname);
            s.close();
            return false;
        }

        CurentUser = us;
        s.close();
        event.InvokeAll();
        return true;
    }

    @Override
    public boolean removeUser(String name, String surname) {
        if(CurentUser != null){
            setStatus("WARNING: You must log out to use this option");
            return false;
        }
        
        Session s = DAOFactory.openSession();
        User us = find(name, surname, s);

        if(us == null){
            setStatus("WARNING: User "+name+' '+surname+" does not exist");
            s.close();
            return false;
        }

        s.remove(us);
        s.flush();
        s.close();
       
        return true;
    }

    @Override
    public void subscribeContextChange(Runnable action) {
        event.attach(action);
    }

    @Override
    public void unsubscribeContextChange(Runnable action) {
        event.detach(action);
        
    }

    private void setStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }
    
}
