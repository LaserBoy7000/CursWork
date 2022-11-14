package com.labs.sevices;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import com.labs.core.service.impl.Selector;
import com.labs.testingutils.FakeDB;

public class SelectorTest {
    static ISelector tst;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        Tax tx1 = new Tax();
        tx1.setTitle("tax1");
        Exemption ex = new Exemption();
        ex.setTitle("exemption1");
        User us = new User();
        Income inc1 = new Income();
        inc1.setExemption(ex);
        inc1.setValue(2);
        Income inc2 = new Income();
        inc2.setValue(6);
        Income inc3 = new Income();
        inc3.setValue(18);
        inc1.setTax(tx1);
        inc1.setExemption(ex);
        List<Income> inc = new ArrayList<Income>();
        inc.add(inc1);
        inc.add(inc2);
        inc.add(inc3);
        us.setIncomes(inc);
        s.persist(tx1);
        s.persist(ex);
        s.persist(us);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    static public void getTestSelector(){
        FakeDB db = createDummyDB();

        IIdentityService mk = mock(IIdentityService.class);
        User us = new User();
        us.setID(1);
        when(mk.getCurrentUser()).thenReturn(us);

        Selector sl = new Selector(mk, db.getDAO());

        tst =  (ISelector)sl;
    }

    @Test
    public void selectUserTest(){
        User[] us = tst.selectUser();
        Assert.assertTrue(us.length == 1);
        Assert.assertTrue(us[0].getID() == 1);
        tst.setFromPrevious(true);
        us = tst.selectUser();
        Assert.assertTrue(us.length == 1);
        Assert.assertTrue(us[0].getID() == 1);
        tst.setFromPrevious(false);
    }

    @Test
    public void selectTaxTest(){
        Tax[] tx = tst.selectTax();
        Assert.assertTrue(tx.length == 1);
        Assert.assertTrue(tx[0].getTitle().equals("tax1"));
        tst.setFromPrevious(true);
        tx = tst.selectTax();
        Assert.assertTrue(tx.length == 1);
        Assert.assertTrue(tx[0].getTitle().equals("tax1"));
        tst.setFromPrevious(false);
        tx = tst.selectTax();
        Assert.assertTrue(tx.length == 1);
        Assert.assertTrue(tx[0].getTitle().equals("tax1"));
    }

    @Test
    public void selectExemptionTest(){
        Exemption[] ex = tst.selectExemption();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getTitle().equals("exemption1"));
        tst.setFromPrevious(true);
        ex = tst.selectExemption();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getTitle().equals("exemption1"));
        tst.setFromPrevious(false);
    }

    @Test
    public void selectIncomeTest(){
        Income[] inc = tst.selectIncome();
        Assert.assertTrue(inc.length == 3);
        Assert.assertTrue(inc[0].getValue()==2);
        Assert.assertTrue(inc[0].getExemption().getTitle().equals("exemption1"));
        tst.setFromPrevious(true);
        inc = tst.selectIncome();
        Assert.assertTrue(inc.length == 3);
        Assert.assertTrue(inc[0].getValue()==2);
        Assert.assertTrue(inc[0].getExemption().getTitle().equals("exemption1"));
        tst.setFromPrevious(false);
    }

    @Test
    public void filteringTestBounds(){
        tst.setBounds("Value", 3.0, 7.0);
        Income[] ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 2);
        tst.setBounds(null,null,null);
        tst.selectIncome();
        tst.setBounds("Value", 3.0, 7.0);
        tst.setFromPrevious(true);
        
        ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 2);
        tst.setFromPrevious(false);
        tst.setBounds(null,null,null);
    }

    @Test
    public void TestBounds(){
        tst.setBounds("Value", 3.0, 7.0);
        Income[] ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 2);

        tst.setFromPrevious(true);
        tst.setBounds(null,null,null);
        ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 2);
        tst.setFromPrevious(false);
    }

    @Test
    public void filteringTestLike(){
        tst.setStringTemplate("Value", "2");
        Income[] ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 1);

        tst.setStringTemplate(null, null);
        tst.selectIncome();
        tst.setStringTemplate("Value", "2");

        tst.setFromPrevious(true);
        ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getID() == 1);

        tst.setStringTemplate(null, null);
        tst.setFromPrevious(false);
    }

    @Test
    public void filteringTestOrder(){
        tst.setOrder("Value", true);
        Income[] ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 3);
        Assert.assertTrue(ex[0].getID() == 3);
        Assert.assertTrue(ex[2].getID() == 1);
        tst.setOrder(null, false);

        ex = tst.selectIncome();
        tst.setOrder("Value", true);
        tst.setFromPrevious(true);
        ex = tst.selectIncome();
        Assert.assertTrue(ex.length == 3);
        Assert.assertTrue(ex[0].getID() == 3);
        Assert.assertTrue(ex[2].getID() == 1);

        tst.setOrder(null, false);
        tst.setFromPrevious(false);
    }

    @Test
    public void getSingleAndSelectionTest(){
        tst.selectIncome();
        tst.selectOfIndex(1, Income.class);
        Income i = (Income)tst.getSelected();
        Class<?> t = tst.getSelectedType();
        Assert.assertTrue(t.equals(Income.class));
        Assert.assertTrue(i.getID() == 2);
        Object[] s = tst.getLastSelection();
        Assert.assertTrue(s.length == 3);
        Assert.assertTrue(s[0] instanceof Income);
    }

}
