package com.labs.helpers;

import org.junit.*;

import com.labs.core.entity.Tax;
import com.labs.core.helper.ReflectionSetter;

public class ReflectionSetterTest {
    
    @Test
    public void Test(){
        ReflectionSetter st = new ReflectionSetter();
        Tax tx = new Tax();
        tx.setTitle("tx");
        Assert.assertTrue(st.hasSettableProperty(Tax.class, "Title", String.class));
        st.set(tx, "Title", "Name", String.class);
        Assert.assertTrue(tx.getTitle().equals("Name"));
    }
}
