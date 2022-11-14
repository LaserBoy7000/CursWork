package com.labs.core.service;
import com.google.inject.ImplementedBy;
import com.labs.core.entity.*;
import com.labs.core.service.impl.ConsistencyService;

//Service used to keep data correct and recalculate updated(new) values;
@ImplementedBy(ConsistencyService.class)
public interface IConsistencyService {
    //public boolean recalcWhereTax(Tax tax);
    //public boolean recalcWhereUser(User user);
    //public boolean recalcWhereExemption(Exemption exemption);
    //public boolean recalcWhereConst(Constant constant);
    public boolean recalcIncome(Income income);
    public String getLastOperationResult();
}
