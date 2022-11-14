package com.labs.core.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.labs.core.entity.Income;
import com.labs.core.helper.ReflectionSetter;
import com.labs.core.service.IConsistencyService;
import com.labs.core.service.ISelector;
import com.labs.core.service.IWriter;

public class Writer implements IWriter {

    private String Status = "";
    private ISelector Selector;
    private SessionFactory DAOFactory;
    private IConsistencyService CS;

    @Inject
    public Writer(ISelector selector, SessionFactory daofactory, IConsistencyService consistancyService){
        Selector = selector; DAOFactory = daofactory; CS = consistancyService;
    }

    @Override
    public boolean update(String param, Object value, Class<?> type) {
        Object s = Selector.getSelected();
        if(s == null){
            setStatus("WARNING: No available selection to update");
            return false;
        }
        ReflectionSetter st = new ReflectionSetter();
        if(!st.hasSettableProperty(s.getClass(), param, type)){
            setStatus("WARNING: Object of type "+s.getClass().getSimpleName()+" does not have property like " + param);
            return false;
        }
        if(!st.set(s, param, value, type)){
            setStatus("WARNING: Invalid value, could not set "+param+" of "+s.getClass().getSimpleName());
            return false;
        }

        if(s instanceof Income && !CS.recalcIncome((Income)s)){
            setStatus(CS.getLastOperationResult());
            return false;
        }

        Session ses = DAOFactory.openSession();
        ses.merge(s);
        ses.flush();
        ses.close();
        return true;
    }

    @Override
    public boolean remove(boolean allSelection) {
        Session ses = DAOFactory.openSession();
        if(allSelection){
            Object[] rm = Selector.getLastSelection();
            if(rm == null || rm.length == 0){
                setStatus("WARNING: Could not delete from last selection cause it was empty");
                return false;
            }
            for (Object o : rm)
                ses.remove(o);
            ses.flush();
            ses.close();
            return true;
        }

        Object rm = Selector.getSelected();
        if(rm == null){
            setStatus("WARNING: Could not delete from last selected cause it was empty");
            return false;
        }
        ses.remove(rm);
        ses.flush();
        ses.close();
        return true;
    }

    private void setStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }
    
}
