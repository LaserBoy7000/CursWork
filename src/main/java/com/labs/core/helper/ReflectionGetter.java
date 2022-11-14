package com.labs.core.helper;

import java.lang.reflect.Method;

import com.google.common.base.Defaults;
import com.google.common.base.Function;

public class ReflectionGetter {

    public boolean hasAccessibleProperty(Class<?> type, String property){
        return tryGetGetter(type, property) != null;
    }

    public Function<Object, Object> buildReflectedGetter(Class<?> type, String property){
        Method gt = tryGetGetter(type, property);
        if(gt == null)
            return null;
        return (x) -> {
            try{
                return gt.invoke(x);
            } catch(Exception e){
                return null;
            }
        };
    }

    public boolean isNumerical(Class<?> type, String property){
        Method m = tryGetGetter(type, property);
        if(m == null) return false;
        Class<?> t = m.getReturnType();
        if(t.isPrimitive()){
            try{
            Double.parseDouble(Defaults.defaultValue(t).toString());
            return true;
            } catch (Exception e) {return false;}
        }
        if(Number.class.isAssignableFrom(t))
            return true;
        return false;
    }

    private Method tryGetGetter(Class<?> type, String property){
        Method rs;
        if(property == null)
            return null;
        try
        {
            rs = type.getMethod("get" + property);
        } 
        catch (NoSuchMethodException e)
        {
            try
            {
                rs =type.getMethod("is" + property);
            } 
            catch (NoSuchMethodException e2) 
            {
                return null;
            }
        }

        return rs;
    }
    

}
