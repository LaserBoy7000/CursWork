package com.labs.UIAPI.command.select;

import com.labs.core.service.ISelector;

public class SelectorConfiguration {
    public String MinmaxParam = null;
    public Double Min = null;
    public Double Max = null; 
    public String LexemParam = null; 
    public String Lexem = null;
    public String SortParam = null; 
    public boolean Desc = false;
    public boolean FromPrevious = false;
    public Integer SelectN = null;

    public void clear(){
        MinmaxParam = null;
        Min = null;
        Max = null; 
        LexemParam = null; 
        Lexem = null;
        SortParam = null; 
        Desc = false;
        FromPrevious = false;
        SelectN = null;
    }

    public void configure(ISelector selector){
        selector.setBounds(MinmaxParam, Min, Max);
        selector.setStringTemplate(LexemParam, Lexem);
        selector.setFromPrevious(FromPrevious);
        selector.setOrder(SortParam, Desc);
    }
}
