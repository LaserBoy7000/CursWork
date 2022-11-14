package com.labs.helpers;

import org.junit.Assert;

import com.labs.core.helper.ActionEvent;

public class EventTest {
    private static String succ = "no";

    @org.junit.Test
    public void Test(){
        ActionEvent ev = new ActionEvent();
        Runnable r = () -> succ = "yes";
        ev.attach(r);
        ev.InvokeAll();
        Assert.assertTrue(succ.equals("yes"));
        succ = "no";
        ev.detach(r);
        ev.InvokeAll();
        Assert.assertTrue(!succ.equals("yes"));
    }
}
