package com.labs.helpers;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

import com.labs.core.entity.Tax;
import com.labs.core.helper.ReflectionGetter;


public class ReflectionGetterTest {
    @Test
    public void Test(){
        ReflectionGetter gt = new ReflectionGetter();
        Tax tx = new Tax();
        tx.setTitle("tx");
        Function<Object, Object> get = gt.buildReflectedGetter(Tax.class, "Title");
        Assert.assertTrue(get.apply(tx).equals("tx"));

        Assert.assertTrue(!gt.isNumerical(Tax.class, "Title"));
        Assert.assertTrue(gt.isNumerical(Tax.class, "TIPP"));
    }
}
