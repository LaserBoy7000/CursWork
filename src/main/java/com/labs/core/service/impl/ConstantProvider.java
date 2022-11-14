package com.labs.core.service.impl;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.labs.core.entity.Constant;
//import com.labs.core.service.IConsistencyService;
import com.labs.core.service.IConstantProvider;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Singleton
public class ConstantProvider implements IConstantProvider {
    public static final String PRESERVED_VALUE_NAME = "value";
    private List<Constant> Constants;
    private SessionFactory SessionFactory;   
    //private IConsistencyService CS; 
    private String Status = "";

    @Inject
    public ConstantProvider(SessionFactory factory){
        SessionFactory = factory;
        Session session = factory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Constant> cq = cb.createQuery(Constant.class);
        Root<Constant> rootEntry = cq.from(Constant.class);
        CriteriaQuery<Constant> all = cq.select(rootEntry);
        TypedQuery<Constant> allQuery = session.createQuery(all);
        Constants = allQuery.getResultList();
        session.close();
    }

    @Override
    public double getConstant(String title) {
        Optional<Constant> val = Constants.stream().filter(x->x.getTitle().equals(title)).findFirst();
        if(!val.isPresent()){
            setStatus("FATAL: Requested Value " + title + " does not exist");
            return Double.NaN;
        }
        return val.get().getValue();
    }

    @Override
    public boolean setConstant(String title, double value) {
        if(title.equals(PRESERVED_VALUE_NAME)){
            setStatus("ERROR: The name "+PRESERVED_VALUE_NAME+" is preserved within constants context");
            return false;
        }
        Optional<Constant> val = Constants.stream().filter(x->x.getTitle().equals(title)).findFirst();
        Constant upd;
        if(!val.isPresent()){
            upd = new Constant();
            upd.setTitle(title);
            Constants.add(upd);
        } 
        else 
        {
            upd = val.get();
        }
        upd.setValue(value);
        dbSave(upd);
        //CS.recalcWhereConst(upd);
        return true;
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }

    @Override
    public Constant[] getAll(){
        return Constants.toArray(new Constant[Constants.size()]);
    }

    private void setStatus(String status){
        Status = status;
    }

    private void dbSave(Constant value){
        Session s = SessionFactory.openSession();
        s.merge(value);
        s.flush();
        s.close();
    }
    
}
