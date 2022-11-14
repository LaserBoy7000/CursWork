package com.labs.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.helper.ReflectionGetter;
import com.labs.core.helper.UniComparator;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Singleton
public class Selector implements ISelector {
    private SessionFactory DAOFactory;
    private IIdentityService IdentityContext;

    private Object Selected;
    private Class<?> SelectedType;
    private Class<?> LastSelectionType;
    private List<Tax> Taxes;
    private List<Income> Incomes;
    private List<Exemption> Exemptions;
    private List<User> Users;

    private Double Min;
    private Double Max;
    private String NumericalSecectionProperty;
    private String StringSelectionProperty;
    private String Lexem;
    private String OrderByPropperty;
    private boolean OrderByDescending = false;
    private boolean FromPrevious = false;

    private String Status;

    @Inject
    public Selector(IIdentityService identity, SessionFactory factory){
        identity.subscribeContextChange(()->clearContext());
        IdentityContext = identity;
        DAOFactory = factory;
    }


    private boolean checkConstraints(Class<?> type, boolean canNumerical, boolean canByString, boolean canOrder, boolean canPrevious){
        ReflectionGetter gt = new ReflectionGetter();

        if(canNumerical && NumericalSecectionProperty != null){
            if(!gt.hasAccessibleProperty(type, NumericalSecectionProperty)){
                setStatus("ERROR: type "+type.getSimpleName()+" does not have property " + NumericalSecectionProperty);
                return false;
            }
            if(!gt.isNumerical(type, NumericalSecectionProperty)){
                setStatus("ERROR: property " + NumericalSecectionProperty + " cannot be used for diapasone selection");
                return false;
            }
        }

        if(canByString && StringSelectionProperty != null && !gt.hasAccessibleProperty(type, StringSelectionProperty)){
            setStatus("ERROR: type "+type.getSimpleName()+" does not have property " + StringSelectionProperty);
            return false;
        }

        if(canOrder && OrderByPropperty != null && !gt.hasAccessibleProperty(type, OrderByPropperty)){
            setStatus("ERROR: type "+type.getSimpleName()+" does not have property " + OrderByPropperty);
            return false;
        }

        if(canPrevious && FromPrevious){
            List<?> ls = GetBuferByType(type);
            if(ls == null || ls.size() == 0){
                setStatus("ERROR: type "+type.getSimpleName()+" does not have data in previous selection");
                return false;
            }
        }

        return true;
    }


    private List<?> GetBuferByType(Class<?> type){
        if(type == Tax.class)
            return Taxes;
        if(type == User.class)
            return Users;
        if(type == Exemption.class)
            return Exemptions;
        if(type == Income.class)
            return Incomes;
        return null;
    }


    private <T> Stream<T> ApplyNumericalFiltering(Stream<T> stream, Class<?> type){
        ReflectionGetter gt = new ReflectionGetter();
        return stream.filter((x)->{
            double v = ((Number)gt.buildReflectedGetter(type, NumericalSecectionProperty).apply(x)).doubleValue();
            boolean rs = true;
            if(Max != null)
                rs = rs && v <= Max;
            if(Min != null)
                rs = rs && v >= Min;
            return rs;
        });
    }


    private <T> Stream<T> ApplyStringFiltering(Stream<T> stream, Class<?> type){
        ReflectionGetter gt = new ReflectionGetter();
        return stream.filter((x)->{
            String v = gt.buildReflectedGetter(type, StringSelectionProperty).apply(x).toString();
            return v.contains(Lexem);
        });
    }


    private <T> Stream<T> ApplyOrdering(Stream<T> stream, Class<?> type){
        UniComparator cmp = new UniComparator();
        ReflectionGetter gt = new ReflectionGetter();
        return stream.sorted((x,y) -> {
            Object v1 = gt.buildReflectedGetter(type, OrderByPropperty).apply(x);
            Object v2 = gt.buildReflectedGetter(type, OrderByPropperty).apply(y);
            if(OrderByDescending)
                return cmp.le(v1, v2) ? 1 : -1;
            else return cmp.ge(v1, v2) ? 1 : -1;
        });
    }


    private <T> Stream<T> TypicalInternalSelector(Class<?> type){
        @SuppressWarnings("unchecked")
        Stream<T> act = (Stream<T>)GetBuferByType(type).stream();

        if(NumericalSecectionProperty != null && (Max != null || Min != null))
            act = ApplyNumericalFiltering(act, type);
        
        if(StringSelectionProperty != null && Lexem != null & Lexem.length() != 0)
            act = ApplyStringFiltering(act, type);

        if(OrderByPropperty != null)
            act = ApplyOrdering(act, type);

        return act;
    } 



    @Override
    public Tax[] selectTax() {
        if(!checkConstraints(Tax.class, true, true, true, true))
        return null;

        if(FromPrevious)
            return selectTaxPrev();
        return selectTaxDB();   
    }

    private Tax[] selectTaxPrev() {
        Stream<Tax> act = TypicalInternalSelector(Tax.class);
        List<Tax> ls = act.toList();
        if(ls.size() > 0){
            LastSelectionType = Tax.class;
            Selected = null; SelectedType = null;
            Taxes = ls;
        }
        return Arrays.copyOf(ls.toArray(), ls.size(), Tax[].class);
    }

    private Tax[] selectTaxDB(){
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Tax> cq = cb.createQuery(Tax.class);
        Root<Tax> rt = cq.from(Tax.class);
        CriteriaQuery<Tax> all = cq.select(rt);
        List<Predicate> wheres = new ArrayList<Predicate>(); 
 
        if(NumericalSecectionProperty != null){
            if(Min != null)
                wheres.add(cb.ge(rt.get(NumericalSecectionProperty), Min));
            if(Max != null)
                wheres.add(cb.le(rt.get(NumericalSecectionProperty), Max));
        }

        if(StringSelectionProperty != null)
            wheres.add(cb.like(rt.get(StringSelectionProperty).as(String.class), '%'+Lexem+'%'));

        if(wheres.size() > 0)
            all = all.where(wheres.toArray(new Predicate[wheres.size()]));
        
        if(OrderByPropperty != null)
            if(!OrderByDescending)
                all = all.orderBy(cb.asc(rt.get(OrderByPropperty)));
            else all = all.orderBy(cb.desc(rt.get(OrderByPropperty)));

        List<Tax> ex = s.createQuery(all).list();
        s.close();

        if(ex.size() > 0)
        {
            LastSelectionType = Tax.class;
            Selected = null; SelectedType = null;
            Taxes = ex;
        }
    
        return Arrays.copyOf(ex.toArray(), ex.size(), Tax[].class);
    }


    @Override
    public Income[] selectIncome() {
        if(!checkConstraints(Income.class, true, true, true, true))
            return null;

        if(FromPrevious)
            return selectIncomePrev();
        return selectIncomeDB();   
    }

    private Income[] selectIncomePrev() {
        Stream<Income> act = TypicalInternalSelector(Income.class);
        List<Income> ls = act.toList();
        if(ls.size() > 0){
            LastSelectionType = Income.class;
            Selected = null; SelectedType = null;
            Incomes = ls;
        }
        return Arrays.copyOf(ls.toArray(), ls.size(), Income[].class);
    }

    private Income[] selectIncomeDB() {
        int userId = IdentityContext.getCurrentUser().getID();
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Income> cq = cb.createQuery(Income.class);
        List<Predicate> wheres = new ArrayList<Predicate>(); 
        Root<Income> rt = cq.from(Income.class);
        Join<Income, User> join = rt.join("User");
        wheres.add(cb.equal(join.get("ID"), userId));

        
        if(NumericalSecectionProperty != null){
            if(Min != null)
                wheres.add(cb.ge(rt.get(NumericalSecectionProperty), Min));
            if(Max != null)
                wheres.add(cb.le(rt.get(NumericalSecectionProperty), Max));
        }

        if(StringSelectionProperty != null)
            wheres.add(cb.like(rt.get(StringSelectionProperty).as(String.class), '%'+Lexem+'%'));

        cq = cq.where(wheres.toArray(new Predicate[0]));
        
        if(OrderByPropperty != null)
            if(!OrderByDescending)
                cq = cq.orderBy(cb.asc(rt.get(OrderByPropperty)));
            else cq = cq.orderBy(cb.desc(rt.get(OrderByPropperty)));

        List<Income> rs = s.createQuery(cq).list();

        if(rs.size() > 0){
            LastSelectionType = Income.class;
            Selected = null; SelectedType = null;
            Incomes = rs;
        }

        s.close();
        
        return Arrays.copyOf(rs.toArray(), rs.size(), Income[].class);
    }


    @Override
    public Exemption[] selectExemption() {
        if(!checkConstraints(Exemption.class, true, true, true, true))
            return null;

        if(FromPrevious)
            return selectExemptionPrev();
        return selectExemptionDB();   
    }

    private Exemption[] selectExemptionPrev(){
        Stream<Exemption> act = TypicalInternalSelector(Exemption.class);
        List<Exemption> ls = act.toList();
        if(ls.size() > 0){
            LastSelectionType = Exemption.class;
            Selected = null; SelectedType = null;
            Exemptions = ls;
        }
        return Arrays.copyOf(ls.toArray(), ls.size(), Exemption[].class);
    }

    private Exemption[] selectExemptionDB(){
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<Exemption> cq = cb.createQuery(Exemption.class);
        Root<Exemption> rt = cq.from(Exemption.class);
        CriteriaQuery<Exemption> all = cq.select(rt);
        List<Predicate> wheres = new ArrayList<Predicate>(); 
        
        if(NumericalSecectionProperty != null){
            if(Min != null)
                wheres.add(cb.ge(rt.get(NumericalSecectionProperty), Min));
            if(Max != null)
                wheres.add(cb.le(rt.get(NumericalSecectionProperty), Max));
        }

        if(StringSelectionProperty != null)
            wheres.add(cb.like(rt.get(StringSelectionProperty).as(String.class), '%'+Lexem+'%'));

        if(wheres.size() > 0)
            all = all.where(wheres.toArray(new Predicate[wheres.size()]));
        
        if(OrderByPropperty != null)
            if(!OrderByDescending)
                all = all.orderBy(cb.asc(rt.get(OrderByPropperty)));
            else all = all.orderBy(cb.desc(rt.get(OrderByPropperty)));

        List<Exemption> ex = s.createQuery(all).list();
        s.close();

        if(ex.size() > 0){
        LastSelectionType = Exemption.class;
        Selected = null; SelectedType = null;
        Exemptions = ex;
        }
    
        return Arrays.copyOf(ex.toArray(), ex.size(), Exemption[].class);
    }


    @Override
    public User[] selectUser() {
        if(!checkConstraints(User.class, true, true, true, true))
            return null;

        if(FromPrevious)
            return selectUserPrev();
        return selectUserDB();   
    }

    private User[] selectUserPrev(){
        Stream<User> act = TypicalInternalSelector(User.class);
        List<User> ls = act.toList();
        if(ls.size() > 0){
            LastSelectionType = User.class;
            Selected = null; SelectedType = null;
            Users = ls;
        }
        return Arrays.copyOf(ls.toArray(), ls.size(), User[].class);
    }

    public User[] selectUserDB(){
        Session s = DAOFactory.openSession();
        CriteriaBuilder cb = s.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> rt = cq.from(User.class);
        CriteriaQuery<User> all = cq.select(rt);
        List<Predicate> wheres = new ArrayList<Predicate>(); 
        
        if(NumericalSecectionProperty != null){
            if(Min != null)
                wheres.add(cb.ge(rt.get(NumericalSecectionProperty), Min));
            if(Max != null)
                wheres.add(cb.le(rt.get(NumericalSecectionProperty), Max));
        }

        if(StringSelectionProperty != null)
            wheres.add(cb.like(rt.get(StringSelectionProperty).as(String.class), '%'+Lexem+'%'));

        if(wheres.size() > 0)
            all = all.where(wheres.toArray(new Predicate[wheres.size()]));
        
        if(OrderByPropperty != null)
            if(!OrderByDescending)
                all = all.orderBy(cb.asc(rt.get(OrderByPropperty)));
            else all = all.orderBy(cb.desc(rt.get(OrderByPropperty)));

        List<User> ex = s.createQuery(all).list();
        s.close();

        if(ex.size() > 0){
            LastSelectionType = User.class;
            Selected = null; SelectedType = null;
            Users = ex;
        }
    
        return Arrays.copyOf(ex.toArray(), ex.size(), User[].class);
    }


    @Override
    public boolean selectOfIndex(int i, Class<?> type){
        List<?> act = GetBuferByType(type);
        if(act == null || act.size() == 0){
            setStatus("ERROR: Cannot select from previous of "+type.getSimpleName()+", because it is empty");
            return false;
        }
        if(act.size() <= i){
            setStatus("ERROR: Index out of bounds for "+type.getSimpleName()+" selection");
            return false;
        }
        SelectedType = type;
        Selected = act.get(i);
        return true;
    }

    @Override
    public void setBounds(String property, Double min, Double max) {
        Max = max;
        Min = min;
        NumericalSecectionProperty = property;
    }


    @Override
    public void setOrder(String property, boolean descending) {
        OrderByPropperty = property;
        OrderByDescending = descending;
    }


    @Override
    public void setStringTemplate(String property, String text) {
        StringSelectionProperty = property;
        Lexem = text;
    }


    @Override
    public void setFromPrevious(boolean fromPrevious) {
        FromPrevious = fromPrevious;
    }



    @Override
    public String getLastOperationStatus() {
        return Status;
    }


    @Override
    public Object getSelected() {
        return Selected;
    }


    @Override
    public Class<?> getSelectedType() {
        return SelectedType;
    }

    public Class<?> getLastSelectionType() {
        return LastSelectionType;
    }

    private void setStatus(String status){
        Status = status;
    }


    public void clearContext(){
        Taxes = null; Incomes = null;
        Exemptions = null; Users = null;
    }


    @Override
    public Object[] getLastSelection() {
        if(LastSelectionType == null)
            return null;
        List<?> ls = GetBuferByType(LastSelectionType);
        if(ls == null)
            return null;
        return ls.toArray(new Object[ls.size()]);
    }
}
