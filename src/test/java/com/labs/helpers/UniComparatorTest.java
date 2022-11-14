package com.labs.helpers;

import org.junit.Assert;
import org.junit.Test;

import com.labs.core.helper.UniComparator;

public class UniComparatorTest {
    @Test
    public void testNull(){
        UniComparator cp = new UniComparator();
        Assert.assertTrue(cp.ge(null, null));
        Assert.assertTrue(!cp.le(null, "A"));
    }

    @Test
    public void testNum(){
        UniComparator cp = new UniComparator();
        Assert.assertTrue(cp.ge(10.0, 5));
        Assert.assertTrue(cp.ge(10.0, 10));
        Assert.assertTrue(!cp.le(10.0, 5));
        Assert.assertTrue(cp.le(10, 10.0));
    }

    @Test
    public void testStr(){
        UniComparator cp = new UniComparator();
        Assert.assertTrue(cp.ge("A", "A"));
        Assert.assertTrue(cp.le("A", "C"));
        Assert.assertTrue(!cp.ge("A", "C"));
        Assert.assertTrue(cp.le("A", "A"));
    }
}
