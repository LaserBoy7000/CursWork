package com.labs.core.service.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.google.inject.Inject;
import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.ICalculator;
import com.labs.core.service.IConsistencyService;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ConsistencyService implements IConsistencyService {
    private SessionFactory DAOFactory;
    private ICalculator calc;
    private String Status;

    @Inject
    public ConsistencyService(SessionFactory factory, ICalculator calculator){
        DAOFactory = factory;
        calc = calculator;
    }

    // @Override
    // public boolean recalcWhereTax(Tax tax) {
    //     Session s = DAOFactory.openSession();
    //     CriteriaBuilder cb = s.getCriteriaBuilder();
    //     CriteriaQuery<User> cq = cb.createQuery(User.class);
    //     Root<User> rt = cq.from(User.class);
    //     Join<User, Income> join = rt.join("Incomes");
    //     cq.where(cb.equal(join.get("Tax"), tax));
    //     List<User> usr = s.createQuery(cq).list();
    //     for (User user : usr){
    //         for (Income inc : user.getIncomes())
    //             if(!calcIncome(inc, inc.getTax(), user))
    //                 return false;
    //         s.merge(usr);
    //     }
    //     s.flush();
    //     s.close();
    //     return true;
    // }

    // @Override
    // public boolean recalcWhereUser(User user) {
    //     Session s = DAOFactory.openSession();
    //     CriteriaBuilder cb = s.getCriteriaBuilder();
    //     CriteriaQuery<User> cq = cb.createQuery(User.class);
    //     Root<User> rt = cq.from(User.class);
    //     rt.join("Incomes");
    //     cq.where(cb.equal(rt.get("ID"), user.getID()));
    //     List<User> usr = s.createQuery(cq).list();
    //     for (User userr : usr){
    //         for (Income inc : userr.getIncomes())
    //             if(!calcIncome(inc, inc.getTax(), userr))
    //                 return false;
    //         s.merge(usr);
    //     }
    //     s.flush();
    //     s.close();
    //     return true;
    // }

    // @Override
    // public boolean recalcWhereExemption(Exemption exemption) {
    //     Session s = DAOFactory.openSession();
    //     CriteriaBuilder cb = s.getCriteriaBuilder();
    //     CriteriaQuery<User> cq = cb.createQuery(User.class);
    //     Root<User> rt = cq.from(User.class);
    //     Join<User, Income> join = rt.join("Incomes");
    //     cq.where(cb.equal(join.get("Exemption"), exemption));
    //     List<User> usr = s.createQuery(cq).list();
    //     for (User user : usr){
    //         for (Income inc : user.getIncomes())
    //             if(!calcIncome(inc, inc.getTax(), user))
    //                 return false;
    //         s.merge(usr);
    //     }
    //     s.flush();
    //     s.close();
    //     return true;
    // }

    // @Override
    // public boolean recalcWhereConst(Constant constant) {
    //     Session s = DAOFactory.openSession();
    //     CriteriaBuilder cb = s.getCriteriaBuilder();
    //     CriteriaQuery<User> cq = cb.createQuery(User.class);
    //     Root<User> rt = cq.from(User.class);
    //     Join<User, Income> incomes = rt.join("Incomes");
    //     Join<Income, Exemption> exemptions = incomes.join("Exemption");
    //     Join<Income, Tax> taxes = incomes.join("Tax");
    //     cq.where(cb.or(cb.like(taxes.get("Formula"), constant.getTitle()), cb.like(exemptions.get("Formula"), constant.getTitle())));
    //     List<User> usr = s.createQuery(cq).list();
    //     for (User user : usr){
    //         for (Income inc : user.getIncomes())
    //             if(!calcIncome(inc, inc.getTax(), user))
    //                 return false;
    //         s.merge(usr);
    //      }
    //     s.flush();
    //     s.close();
    //     return true;
    // }

    @Override
    public boolean recalcIncome(Income income) {
        Session session = DAOFactory.openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Tax> cq = cb.createQuery(Tax.class);
        Root<Tax> rt = cq.from(Tax.class);
        CriteriaQuery<Tax> all = cq.select(rt);
        all.where(cb.equal(rt.get("Title"), income.getTitle()));
        List<Tax> tx = session.createQuery(all).list();

        if(tx.size() > 0){
            boolean rs = calcIncome(income, tx.stream().findFirst().get(), income.getUser());
            if(!rs)
                return rs;
        } else {
            income.setTax(null);
            income.setValueTaxed(0);
        }

        session.merge(income);
        session.flush();
        session.close();
        return true;
    }

    private boolean calcIncome(Income income, Tax tax, User user){
        Exemption ex = user.getExemption();
        Boolean aple = false;
        if(ex != null)
            aple = calc.isExemptionApplyable(ex, user, income);
        if(aple == null){
            setStatus(calc.getLastOperaionStatus());
            return false;
        }
        if(aple)
            income.setExemption(ex);
        else income.setExemption(null);

        Boolean aplt = calc.isTaxApplyable(tax, user, income);
        if(aplt == null){
            setStatus(calc.getLastOperaionStatus());
            return false;
        }

        if(!aplt){
            income.setValueTaxed(0.0);
            income.setExemption(null);
            income.setTax(null);
            return true;
        }

        income.setTax(tax);
        double txv = calc.calculateTax(income.getTax(), income, income.getExemption(), user);
        
        if(Double.isNaN(txv)){
            setStatus(calc.getLastOperaionStatus());
            return false;
        }

        income.setValueTaxed(txv);
        return true;
    }

    private void setStatus(String status){
        Status = status;
    }

    @Override
    public String getLastOperationResult() {
        return Status;
    } 
}
