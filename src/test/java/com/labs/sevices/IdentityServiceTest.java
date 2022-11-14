package com.labs.sevices;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.User;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.impl.IdentityService;
import com.labs.testingutils.FakeDB;

public class IdentityServiceTest {
    static IIdentityService tst;
    static String rs = "";

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        User us = new User();
        us.setName("a");
        us.setSurname("b");
        us.setPassword("c");
        s.persist(us);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    static public void getTestSelector(){
        FakeDB db = createDummyDB();
        IIdentityService sl = new IdentityService(db.getDAO());
        tst = sl;
    }

    @Test
    public void ComplexTest(){
        rs = "";
        Runnable r = ()->rs="bingo!";
        tst.subscribeContextChange(r);
        Assert.assertTrue(!tst.register("a", "b", "b"));
        tst.register("a", "b", "c");
        Assert.assertTrue(tst.isUserAvailable());
        Assert.assertTrue(rs.equals("bingo!"));
        Assert.assertTrue(tst.getCurrentUser().getID() == 1);
        rs = "";
        tst.logOut();
        Assert.assertTrue(!tst.isUserAvailable());
        Assert.assertTrue(rs.equals("bingo!"));
        tst.logOut();
        Assert.assertTrue(!tst.getLastOperationStatus().isEmpty());
        tst.register("b", "b", "b");
        Assert.assertTrue(tst.getCurrentUser().getName().equals("b"));
        rs = "";
        tst.unsubscribeContextChange(r);
        Assert.assertTrue(!rs.equals("bingo!"));
        Assert.assertTrue(!tst.removeUser("a", "b"));
        tst.logOut();
        Assert.assertTrue(tst.removeUser("a", "b"));
        Assert.assertTrue(!tst.removeUser("a", "b"));
    }
}
