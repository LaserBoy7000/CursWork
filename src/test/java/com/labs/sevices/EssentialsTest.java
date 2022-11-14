package com.labs.sevices;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.IEssentials;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.impl.Essentials;
import com.labs.core.service.impl.Selector;
import com.labs.testingutils.FakeDB;

public class EssentialsTest {
    static IEssentials tst;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        Tax tx1 = new Tax();
        tx1.setTitle("tax1");
        Tax tx2 = new Tax();
        tx2.setTitle("tax2");
        User us = new User();
        Income inc1 = new Income();
        inc1.setTax(tx1);
        inc1.setValueTaxed(2);
        inc1.setDate(Calendar.getInstance().getTime());
        Income inc2 = new Income();
        inc2.setTax(tx2);
        inc2.setValueTaxed(6);
        inc2.setDate(Calendar.getInstance().getTime());
        Income inc3 = new Income();
        inc3.setTax(tx2);
        inc3.setValueTaxed(18);
        inc3.setDate(Calendar.getInstance().getTime());
        List<Income> inc = new ArrayList<Income>();
        s.persist(tx1);
        s.persist(tx2);
        inc.add(inc1);
        inc.add(inc2);
        inc.add(inc3);
        us.setIncomes(inc);
        s.persist(us);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    public static void Init(){
        FakeDB db = createDummyDB();

        IIdentityService mk = mock(IIdentityService.class);
        User us = new User();
        us.setID(1);
        when(mk.getCurrentUser()).thenReturn(us);

        Selector sl = new Selector(mk, db.getDAO());

        tst = new Essentials(sl);
    }

    @Test
    public void reportTest(){
        Object[][] r = tst.GenerateYearlyReport();
        Assert.assertTrue(r[1][0].toString().equals("tax1"));
        Assert.assertTrue((double)r[1][1] == 2);
        Assert.assertTrue(r[2][0].toString().equals("tax2"));
        Assert.assertTrue((double)r[2][1] == 24);
        Assert.assertTrue((double)r[3][1] == 26);
    }
}
