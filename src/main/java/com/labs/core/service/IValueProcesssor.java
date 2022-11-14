package com.labs.core.service;

import com.google.inject.ImplementedBy;
import com.labs.core.service.impl.ValueProcessor;

//Service tries to approprietly interpretate users input
@ImplementedBy(ValueProcessor.class)
public interface IValueProcesssor {
    public Object convert(String name, String value);
    public boolean isSucceed();
    public Class<?> getType();
}
