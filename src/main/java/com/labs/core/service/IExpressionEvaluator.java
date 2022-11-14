package com.labs.core.service;

import com.google.inject.ImplementedBy;
import com.labs.core.service.impl.ExpressionEvaluator;

//Service used to directly estimate formulas
//based on existing constants and objective context,
//which means, that it will extract required values from the
//given object space
@ImplementedBy(ExpressionEvaluator.class)
public interface IExpressionEvaluator {
    public Boolean calcBool(String expression, Object... contexts);
    public double calcVal(String expression, Object... contexts);
    public String getLastOperationStatus();
}
