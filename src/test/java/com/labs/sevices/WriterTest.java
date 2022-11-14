package com.labs.sevices;

import static org.mockito.ArgumentMatchers.any;
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
import com.labs.core.service.IConsistencyService;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;
import com.labs.core.service.IWriter;
import com.labs.core.service.impl.Selector;
import com.labs.core.service.impl.Writer;
import com.labs.testingutils.FakeDB;

public class WriterTest {
    private static IWriter tst;
    private static ISelector slc;
    private static Income[] incs;
    private static Income[] incsn;
    private static Tax tx;
    private static Exemption exm;

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
        inc1.setUser(us);
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
        tx = tx1;
        exm = ex;
        incs = new Income[] {inc1, inc2, inc3};
        incsn = new Income[] {inc2, inc3};
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    static public void getTestWriter(){
        FakeDB db = createDummyDB();

        IIdentityService mk = mock(IIdentityService.class);
        User us = new User();
        us.setID(1);
        when(mk.getCurrentUser()).thenReturn(us);
        IConsistencyService cmk = mock(IConsistencyService.class);
        when(cmk.recalcIncome(any(Income.class))).thenReturn(true);

        slc = new Selector(mk, db.getDAO());
        ISelector sl = mock(ISelector.class);
        when(sl.getSelected()).thenReturn(incs[0]);
        when(sl.getLastSelection()).thenReturn(incsn);
        Writer wr = new Writer(sl, db.getDAO(), cmk);

        tst = wr;
    }

    @Test
    public void updateRemoveTest(){
        tst.update("Value", 10, double.class);
        tst.update("Exemption", exm, Exemption.class);
        tst.update("Tax", tx, Tax.class);
        Income[] inc = slc.selectIncome();
        Assert.assertTrue(inc[0].getValue() == 10 && inc[0].getTax().getID() == 1 && inc[0].getExemption().getID() == 1);
        tst.remove(false);
        inc = slc.selectIncome();
        Assert.assertTrue(inc.length == 2);
        tst.remove(true);
        inc = slc.selectIncome();
        Assert.assertTrue(inc.length == 0);
        Assert.assertTrue(tst.getLastOperationStatus().equals(""));
    }
}
