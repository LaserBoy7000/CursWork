package com.labs.core.service.impl;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mariuszgromada.math.mxparser.Expression;
import com.google.common.base.Function;
import com.google.inject.Inject;
import com.labs.core.helper.ReflectionGetter;
import com.labs.core.service.IConstantProvider;
import com.labs.core.service.IExpressionEvaluator;


public class ExpressionEvaluator implements IExpressionEvaluator {
    private IConstantProvider Constants;
    private String Status = "";

    @Inject
    public ExpressionEvaluator(IConstantProvider constants){
        Constants = constants;
    }

    @Override
    public Boolean calcBool(String expression, Object... contexts) {
        double rs = calcVal(expression, contexts);
        if(Double.isNaN(rs))
            return false;
        return rs == 1.0;
    }

    @Override
    public double calcVal(String expression, Object... contexts) {
        String rd = InsertContexts(expression, contexts);
        if(rd == null)
            return Double.NaN;
        rd = InsertConstants(rd);
        if(rd == null)
            return Double.NaN;
        Expression ex = new Expression(rd);
        if(!ex.checkSyntax()){
            setStatus("ERROR: Requested expression is incorrect");
            return Double.NaN;
        }
        return ex.calculate();
    }

    private String InsertContexts(String expression, Object... contexts){
        if(contexts.length == 0)
            return expression;

        Pattern p = Pattern.compile("%[a-zA-Z]+%");
        Matcher m = p.matcher(expression);
        StringBuilder out = new StringBuilder();
        int lastidx = 0;
        ReflectionGetter gt = new ReflectionGetter();
        while(m.find()){
            String str = m.group(0);
            str = str.substring(1, str.length()-1);
            double obj = Double.NaN;

            for (Object object : contexts) {
                Function<Object, Object> get = gt.buildReflectedGetter(object.getClass(), str);
                if(get != null && gt.isNumerical(object.getClass(), str))
                {
                    obj = ((Number)get.apply(object)).doubleValue();
                    break;
                }
            }
            
            if(Double.isNaN(obj)){
                setStatus("ERROR: Property "+str+" was not found within context");
                return null;
            }

            out.append(expression, lastidx, m.start());
            out.append("" + obj);
            lastidx = m.end();
        }
        if(lastidx <= expression.length())
            out.append(expression, lastidx, expression.length());
        return out.toString();
    }

    private String InsertConstants(String expression){
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher m = p.matcher(expression);
        StringBuilder out = new StringBuilder();
        int lastidx = 0;
        while(m.find()){
            String str = m.group(0);
            double c = Constants.getConstant(str);
            if(c == Double.NaN){
                setStatus(Constants.getLastOperationStatus());
                return null;
            }
            out.append(expression, lastidx, m.start());
            out.append("" + c);
            lastidx = m.end();
        }
        if(lastidx <= expression.length())
            out.append(expression, lastidx, expression.length());
        return out.toString();
    }

    @Override
    public String getLastOperationStatus() {
        return Status;
    }

    private void setStatus(String status){
        this.Status = status;
    }
}
