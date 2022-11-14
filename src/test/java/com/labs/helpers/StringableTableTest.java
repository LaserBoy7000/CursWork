package com.labs.helpers;

import org.junit.Assert;
import org.junit.Test;

import com.labs.core.entity.Tax;
import com.labs.core.helper.StringableTable;

public class StringableTableTest {
    
    @Test
    public void TestFromArray(){
        Tax[] l = new Tax[]{new Tax()};
        l[0].setTitle("Title");
        l[0].setTIPP(99.992);
        StringableTable<Tax> t = new StringableTable<Tax>(l, true, 2, "Title", "TIPP");
        String v = t.toString();
        String pv = 
        "---------------------\n" +
        "|#  |Title  |TIPP   |\n" +
        "---------------------\n" +
        "|1  |Title  |99,99  |\n" +
        "---------------------\n";
        Assert.assertTrue(v.equals(pv));
    }

    @Test
    public void TestFromCell(){
        Object[][] blk = new Object[][]{{"T", 10}};
        StringableTable<Tax> t = new StringableTable<Tax>(blk);
        String v = t.toString();
        String pv = 
        "----------\n" +
        "|T  |10  |\n" +
        "----------\n";
        Assert.assertTrue(v.equals(pv));
    }
}
