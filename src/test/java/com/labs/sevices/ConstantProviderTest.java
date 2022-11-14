package com.labs.sevices;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Constant;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.impl.ConstantProvider;
import com.labs.testingutils.FakeDB;

public class ConstantProviderTest {
    static IConstantProvider tst;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        Constant c = new Constant();
        c.setTitle("X");
        c.setValue(2);
        s.persist(c);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    static public void getTestProvider(){
        FakeDB db = createDummyDB();
        IConstantProvider sl = new ConstantProvider(db.getDAO());
        tst = sl;
        Assert.assertTrue(tst.getLastOperationStatus().equals(""));
    }

    @Test
    public void getSetTest(){
        Assert.assertTrue(tst.getConstant("X") == 2);
        tst.setConstant("D", 4);
        tst.setConstant("X", 3);
        Assert.assertTrue(tst.getConstant("X") * tst.getConstant("D") == 12);
        Assert.assertTrue(tst.getLastOperationStatus().equals(""));
    }
}
