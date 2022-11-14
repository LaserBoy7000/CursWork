package com.labs.sevices;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.labs.core.entity.Exemption;
import com.labs.core.entity.Income;
import com.labs.core.entity.Tax;
import com.labs.core.entity.User;
import com.labs.core.service.ICalculator;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.IExpressionEvaluator;
import com.labs.core.service.impl.Calculator;
import com.labs.core.service.impl.ConstantProvider;
import com.labs.core.service.impl.ExpressionEvaluator;
import com.labs.testingutils.FakeDB;

public class CalculatorTest {
    private static ICalculator calc;
    private static IConstantProvider p;

    static private FakeDB createDummyDB(){
        FakeDB db = new FakeDB();
        return db;
    }

    @BeforeClass
    public static void Init(){
        p = new ConstantProvider(createDummyDB().getDAO());
        IExpressionEvaluator evaluator = new ExpressionEvaluator(p);
        calc = new Calculator(evaluator);
        p.setConstant("CON", 10);
    }

    @Test
    public void isExemptionApplyable(){
        Exemption ex = new Exemption();
        ex.setTitle("ex1");
        ex.setFormula("value*CON<100#%Value%-1");
        Tax tx = new Tax();
        tx.setTitle("tax1");
        tx.setTIPP(0.1);
        tx.setFormula("value*CON<80#value-%Children%");
        User us = new User();
        us.setChildren(5);
        Income i1 = new Income();
        i1.setValue(9);
        Income i2 = new Income();
        i2.setValue(7);
        Income i3 = new Income();
        i3.setValue(10);

        Assert.assertTrue(calc.isExemptionApplyable(ex, us, i1));
        Assert.assertTrue(calc.isExemptionApplyable(ex, us, i2));
        Assert.assertTrue(!calc.isExemptionApplyable(ex, us, i3));

        Assert.assertTrue(!calc.isTaxApplyable(tx, us, i1));
        Assert.assertTrue(calc.isTaxApplyable(tx, us, i2));
        Assert.assertTrue(!calc.isTaxApplyable(tx, us, i3));

        Assert.assertTrue(calc.calculateExemption(ex, i1, us) == 8);
        Assert.assertTrue(calc.calculateExemption(ex, i2, us) == 6);

        Assert.assertTrue(calc.calculateTax(tx, i2, ex, us) == 0.1);

        Assert.assertTrue(calc.getLastOperaionStatus().equals(""));
    }


}
