package com.labs.core.service;

import com.google.inject.ImplementedBy;
import com.labs.core.entity.*;
import com.labs.core.service.impl.Calculator;

// Provides methods for financial calculations and estimations based on entities
@ImplementedBy(Calculator.class)
public interface ICalculator {
    public Boolean isTaxApplyable(Tax tax, User user, Income income);
    public double calculateTax(Tax tax, Income income, Exemption exemption, User user);
    public double calculateExemption(Exemption exemption, Income income, User user);
    public Boolean isExemptionApplyable(Exemption exemption, User user, Income income);
    public String getLastOperaionStatus();
}
