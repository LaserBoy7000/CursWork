package com.labs.core.service.impl;

import java.util.Date;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.google.inject.Inject;
import com.labs.core.entity.*;
import com.labs.core.service.IConsistencyService;
import com.labs.core.service.ICreator;
import com.labs.core.service.IIdentityService;

public class CreatorService implements ICreator {
    private SessionFactory DAOFactory;
    private IIdentityService IDContext;
    private IConsistencyService CS;

    private String Status = "";

    @Inject
    public CreatorService(SessionFactory factory, IIdentityService identity, IConsistencyService consistency) {
        DAOFactory = factory;
        IDContext = identity;
        CS = consistency;
    }
    

    @Override
    public boolean createTax(String name, double tipp, double wc, boolean allowsExemption, String formula) {
        Tax tx = new Tax();
        tx.setTitle(name);
        tx.setTIPP(tipp);
        tx.setWC(wc);
        tx.setAllowsExemption(allowsExemption);
        tx.setFormula(formula);
        Session s = DAOFactory.openSession();
        s.persist(tx);
        s.flush();
        s.close();
        return true;
    }

    @Override
    public boolean createIncome(Date date, String title, double value) {
        int usid = IDContext.getCurrentUser().getID();
        Session s = DAOFactory.openSession();
        User us = s.get(User.class, usid);
        Hibernate.initialize(us.getExemption());
        s.close();
        Income inc = new Income();
        inc.setTitle(title);
        inc.setDate(date);
        inc.setValue(value);
        inc.setUser(us);
        boolean rs = CS.recalcIncome(inc);
        if(!rs){
            SetStatus(CS.getLastOperationResult());
            return false;
        }

        return true;
    }

    @Override
    public boolean createExemption(String name, String formula) {
        Exemption ex  = new Exemption();
        ex.setTitle(name);
        ex.setFormula(formula);
        Session s = DAOFactory.openSession();
        s.persist(ex);
        s.flush();
        s.close();
        return true;
    }

    private void SetStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }
    
}
