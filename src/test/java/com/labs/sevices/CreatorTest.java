package com.labs.sevices;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;

import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.IConsistencyService;
import com.labs.core.service.ICreator;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import com.labs.core.service.impl.CreatorService;
import com.labs.core.service.impl.Selector;
import com.labs.testingutils.FakeDB;

public class CreatorTest {
    static private ISelector slc;
    static private ICreator csv;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        return db;
    }

    @BeforeClass
    static public void getTestSelector(){
        FakeDB db = createDummyDB();

        IIdentityService mk = mock(IIdentityService.class);
        User us = new User();
        Session s = db.getDAO().openSession();
        s.persist(us);
        s.flush();
        s.close();
        when(mk.getCurrentUser()).thenReturn(us);
    
        IConsistencyService cmk = mock(IConsistencyService.class);
        when(cmk.recalcIncome(any(Income.class)))
        .thenAnswer(new org.mockito.stubbing.Answer<Object>()  {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Session s = db.getDAO().openSession();
                s.persist((Income)invocation.getArguments()[0]);
                s.flush();
                s.close();
                return true;
            }});

        slc = new Selector(mk, db.getDAO());
        csv = new CreatorService(db.getDAO(), mk, cmk);
    }

    @Test
    public void CreateTaxTest(){
        Assert.assertTrue(csv.createTax("tax", 0, 0, false, null));
        Tax[] tx = slc.selectTax();
        Assert.assertTrue(tx.length == 1);
        Assert.assertTrue(tx[0].getTitle().equals("tax"));
        Assert.assertTrue(csv.getLastOperationStatus().equals(""));
    }

    @Test
    public void CreateExemptionTest(){
        Assert.assertTrue(csv.createExemption("exemption", null));
        Exemption[] ex = slc.selectExemption();
        Assert.assertTrue(ex.length == 1);
        Assert.assertTrue(ex[0].getTitle().equals("exemption"));
    }

    @Test
    public void CreateIncome(){
        Assert.assertTrue(csv.createIncome(Calendar.getInstance().getTime(), "ttl", 0));
        Income[] in = slc.selectIncome();
        Assert.assertTrue(in.length == 1);
        Assert.assertTrue(in[0].getTitle().equals("ttl"));
    }
}
