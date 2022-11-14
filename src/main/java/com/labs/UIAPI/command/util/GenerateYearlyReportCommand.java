package com.labs.UIAPI.command.util;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.UserCommand;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.IEssentials;
import com.labs.core.service.IIdentityService;

public class GenerateYearlyReportCommand extends UserCommand<StringableTable<?>> {

    private IEssentials Essentials;

    public void setServices(IIdentityService identity, IEssentials essentials){
        Id = identity; Essentials = essentials;
    }
    
    @Override
    protected CommandResult<StringableTable<?>> exectuteP() {
        Object[][] rs = Essentials.GenerateYearlyReport();
        if(rs == null)
            return new CommandResult<StringableTable<?>>(null, Essentials.getLastOperationStatus(), false);
        
        StringableTable<?> t = new StringableTable<Object>(rs);
        return new CommandResult<StringableTable<?>>(t, "Your report for this year", true);
    }

    @Override
    protected CommandResult<Object> exectuteAsObjectiveP() {
        Object[][] rs = Essentials.GenerateYearlyReport();
        if(rs == null)
            return new CommandResult<Object>(null, Essentials.getLastOperationStatus(), false);
        
        StringableTable<?> t = new StringableTable<Object>(rs);
        return new CommandResult<Object>(t, "Your report for this year", true);
    }
    
}
