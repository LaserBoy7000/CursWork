package com.labs.UIAPI.command.select;

import com.labs.UIAPI.CommandResult;
import com.labs.UIAPI.UserCommand;
import com.labs.core.entity.Income;
import com.labs.core.helper.StringableTable;
import com.labs.core.service.IIdentityService;
import com.labs.core.service.ISelector;

public class SelectIncomeCommand extends UserCommand<StringableTable<Income>> {

    private ISelector Selector;
    private SelectorConfiguration conf;
    private boolean Configured = false;

    public SelectIncomeCommand(SelectorConfiguration configuration){
        conf = configuration;
    }
    

    public void SetServices(ISelector selector, IIdentityService identity){
        Selector = selector;
        Id = identity;
        Configured = true;
    }

    @Override
    protected CommandResult<StringableTable<Income>> exectuteP(){
        if(!Configured)
            return new CommandResult<StringableTable<Income>>(null, "FATAL: Command was not configured", false);

        conf.configure(Selector);
        Income[] rs = Selector.selectIncome();
        if(rs == null)
            return new CommandResult<StringableTable<Income>>(null, Selector.getLastOperationStatus(), false);
        if(conf.SelectN != null && !Selector.selectOfIndex(conf.SelectN-1, Income.class))
            return new CommandResult<StringableTable<Income>>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Income> resultive = new StringableTable<>(rs, true, 2, "Title", "Date", "Value", "ValueTaxed", "Exemption", "Tax");
        return new CommandResult<StringableTable<Income>>(resultive, "Found " + resultive.size() + " Entries", true);
    }
    

    @Override
    protected CommandResult<Object> exectuteAsObjectiveP(){
        if(!Configured)
            return new CommandResult<Object>(null, "FATAL: Command was not configured", false);

        conf.configure(Selector);
        Income[] rs = Selector.selectIncome();
        if(rs == null)
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);

        if(conf.SelectN != null && !Selector.selectOfIndex(conf.SelectN-1, Income.class))
            return new CommandResult<Object>(null, Selector.getLastOperationStatus(), false);

        StringableTable<Income> resultive = new StringableTable<>(rs, true, 2, "Title", "Date", "Value", "ValueTaxed", "Exemption", "Tax");
        return new CommandResult<Object>(resultive, "Found " + resultive.size() + " Entries", true);
    }
}
