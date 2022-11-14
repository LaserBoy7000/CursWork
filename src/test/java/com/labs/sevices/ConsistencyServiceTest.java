package com.labs.sevices;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.ICalculator;
import com.labs.core.service.IConsistencyService;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.IExpressionEvaluator;
import com.labs.core.service.impl.Calculator;
import com.labs.core.service.impl.ConsistencyService;
import com.labs.core.service.impl.ConstantProvider;
import com.labs.core.service.impl.ExpressionEvaluator;
import com.labs.testingutils.FakeDB;

public class ConsistencyServiceTest {
    private static IConsistencyService tst;  
    private static IConstantProvider p;
    private static Income inc1;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        return db;
    }  

    @BeforeClass
    public static void Init(){
        FakeDB d = createDummyDB();
        Session s = d.getDAO().openSession();
        Tax tx = new Tax();
        tx.setTitle("tx");
        tx.setTIPP(0.1);
        tx.setWC(0.1);
        tx.setAllowsExemption(true);
        tx.setFormula("(value<2000)#value");
        Exemption ex = new Exemption();
        ex.setFormula("(%Children%>3)&&(value<200)#value/2.0");
        ex.setTitle("ex");
        User us = new User();
        inc1 = new Income();
        inc1.setTitle("tx");
        inc1.setValue(100);
        inc1.setUser(us);
        us.setExemption(ex);
        us.setChildren(4);
        List<Income> incs = new ArrayList<Income>();
        incs.add(inc1);
        us.setIncomes(incs);
        s.persist(tx);
        s.persist(ex);
        s.persist(us);
        s.persist(inc1);
        s.flush();
        s.close();
        p = new ConstantProvider(d.getDAO());
        IExpressionEvaluator ev = new ExpressionEvaluator(p);
        ICalculator calc = new Calculator(ev);
        tst = new ConsistencyService(d.getDAO(), calc);
    } 

    @Test
    public void recalcIncome(){
        inc1.setTax(null); inc1.setExemption(null); inc1.setValueTaxed(0);
        Assert.assertTrue(tst.recalcIncome(inc1));
        Assert.assertTrue(inc1.getValueTaxed() == 10);
    }

    @Test
    public void recalcIncomeNoEx(){
        inc1.setTax(null); inc1.setExemption(null); inc1.setValueTaxed(0);
        inc1.setValue(300);
        Assert.assertTrue(tst.recalcIncome(inc1));
        Assert.assertTrue(inc1.getValueTaxed() == 60);
    }

    @Test
    public void recalcIncomeNoTax(){
        inc1.setTax(null); inc1.setExemption(null); inc1.setValueTaxed(0);
        inc1.setValue(3000);
        Assert.assertTrue(tst.recalcIncome(inc1));
        Assert.assertTrue(inc1.getValueTaxed() == 0);
    }
}
