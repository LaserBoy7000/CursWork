package com.labs.sevices;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Constant;
import com.labs.core.entity.Income;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.impl.ConstantProvider;
import com.labs.core.service.impl.ExpressionEvaluator;
import com.labs.testingutils.FakeDB;

public class ExpressionEvaluatorTest {
    static IConstantProvider tst;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        Constant c = new Constant();
        c.setTitle("X");
        c.setValue(2);
        Constant c2 = new Constant();
        c2.setTitle("Y");
        c2.setValue(6);
        s.persist(c);
        s.persist(c2);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    static public void getTestSelector(){
        FakeDB db = createDummyDB();
        IConstantProvider sl = new ConstantProvider(db.getDAO());
        tst = sl;
    }

    @Test
    public void calcBoolValTest(){
        ExpressionEvaluator ev = new ExpressionEvaluator(tst);
        Income in =  new Income();
        in.setValue(10);
        Assert.assertTrue(ev.calcBool("X*Y+%Value%=22", in));
        Assert.assertTrue(ev.calcVal("Y/X+%Value%", in) == 13);
    }
}
