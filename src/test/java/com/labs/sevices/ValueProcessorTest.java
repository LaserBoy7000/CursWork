package com.labs.sevices;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Exemption;
import com.labs.core.entity.Tax;
import com.labs.core.service.IValueProcesssor;
import com.labs.core.service.impl.ValueProcessor;
import com.labs.testingutils.FakeDB;

public class ValueProcessorTest {
    private static IValueProcesssor vp;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        SessionFactory f = db.getDAO();
        Session s = f.openSession();
        Tax tx = new Tax();
        tx.setTitle("t");
        Exemption ex = new Exemption();
        ex.setTitle("e");
        s.persist(ex);
        s.persist(tx);
        s.flush();
        s.close();
        return db;
    }

    @BeforeClass
    public static void Init(){
        SessionFactory f = createDummyDB().getDAO();
        vp = new ValueProcessor(f);
    }

    @Test
    public void doubleTest(){
        Object v = vp.convert("SomeDouble", "22.2");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(double.class));
        Assert.assertTrue((double)v == 22.2);
    }

    @Test
    public void intTest(){
        Object v = vp.convert("SomeInt", "22");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(int.class));
        Assert.assertTrue((int)v == 22);
    }

    @Test
    public void boolTest(){
        Object v = vp.convert("SomeBool", "true");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(boolean.class));
        Assert.assertTrue((boolean)v == true);
    }

    @Test
    public void taxTest(){
        Object v = vp.convert("Tax", "t");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(Tax.class));
        Assert.assertTrue(((Tax)v).getID() == 1);
    }

    @Test
    public void exmTest(){
        Object v = vp.convert("Exemption", "e");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(Exemption.class));
        Assert.assertTrue(((Exemption)v).getID() == 1);
    }

    @Test
    public void strTest(){
        Object v = vp.convert("SomeDouble", "notany");
        Class<?> t = vp.getType();
        Assert.assertTrue(t.equals(String.class));
        Assert.assertTrue(((String)v).equals("notany"));
    }
}
