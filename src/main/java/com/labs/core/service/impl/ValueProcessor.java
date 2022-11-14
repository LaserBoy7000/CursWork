package com.labs.core.service.impl;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.labs.core.entity.Exemption;
import com.labs.core.entity.Tax;
import com.labs.core.service.IValueProcesssor;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ValueProcessor implements IValueProcesssor {

    private SessionFactory DAOFactory;
    private boolean Error = false;
    private Class<?> Type = Object.class;

    @Inject
    public ValueProcessor(SessionFactory sessionFactory){
        DAOFactory = sessionFactory;
    }

    @Override
    public boolean isSucceed(){
        return !Error;
    }

    @Override
    public Object convert(String name, String value) {
        if(name.equals("Tax")){
            Type = Tax.class;
            if(value == null) return null;
            return FindTax(value);
        }
        if(name.equals("Exemption")){
            Type = Exemption.class;
            if(value == null) return (Exemption)null;
            return FindExemption(value);
        }
        if(value.equals("true")){
            Type = boolean.class;
            return true;
        }
        if(value.equals("false")){
            Type = boolean.class;
            return false;
        }
        try{
            String lv = value.replace(',', '.');
            double n = Double.parseDouble(lv);
            if(n == (double)(int)n){
                Type = int.class;
                return (int)n;
            }
            Type = double.class;
            return n;
        } catch (Exception e) {};

        try{
            Date dt = DateFormat.getInstance().parse(value);
            Type = Date.class;
            return dt;
        } catch (Exception e) {};

        Type = String.class;
        return value;
    }

    public Tax FindTax(String title){
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Tax> cq = cb.createQuery(Tax.class);
        Root<Tax> rt = cq.from(Tax.class);
        CriteriaQuery<Tax> all = cq.select(rt);
        all.where(cb.equal(rt.get("Title"), title));
        List<Tax> rs = s.createQuery(all).list();
        s.close();
        if(rs.size() == 0)
        {
            Error = true;
            return null;
        }
        return rs.get(0);
    }

    public Exemption FindExemption(String title){
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Exemption> cq = cb.createQuery(Exemption.class);
        Root<Exemption> rt = cq.from(Exemption.class);
        CriteriaQuery<Exemption> all = cq.select(rt);
        all.where(cb.equal(rt.get("Title"), title));
        List<Exemption> rs = s.createQuery(all).list();
        s.close();
        if(rs.size() == 0){
            Error = true;
            return null;
        }
        return rs.get(0);
    }

    @Override
    public Class<?> getType() {
        return Type;
    }
}
