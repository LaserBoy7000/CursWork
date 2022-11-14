package com.labs.core.helper;

import java.lang.reflect.Method;

public class ReflectionSetter {

    public boolean hasSettableProperty(Class<?> type, String property, Class<?> paramType){
        return tryGetSetter(type, property, paramType) != null;
    }

    public boolean set(Object object, String propperty, Object value, Class<?> paramType){
;
        Method m = tryGetSetter(object.getClass(), propperty, paramType);
        if(m == null)
            return false;

        try{
            m.invoke(object, value);
        } catch(Exception e){
            return false;
        }

        return true;
    }

    private Method tryGetSetter(Class<?> type, String property, Class<?> paramType){
        if(property == null)
            return null;
        try
        {
            return type.getDeclaredMethod("set" + property, paramType);
        } 
        catch (NoSuchMethodException e) { }
        try
        {
            return type.getDeclaredMethod("set" + property, int.class);
        } 
        catch (NoSuchMethodException e) { }
        try
        {
            return type.getDeclaredMethod("set" + property, double.class);
        } 
        catch (NoSuchMethodException e) { }

        return null;
    }
}
