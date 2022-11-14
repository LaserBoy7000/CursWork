package com.labs.core.service;
import com.google.inject.ImplementedBy;
import com.labs.core.entity.User;
import com.labs.core.service.impl.IdentityService;

//Serves for tracking and providing curent user
@ImplementedBy(IdentityService.class)
public interface IIdentityService {
    public boolean isUserAvailable();
    public User getCurrentUser();
    public boolean logOut();
    public boolean register(String name, String surname, String Password);
    public boolean removeUser(String name, String surname);
    public void subscribeContextChange(Runnable action);
    public void unsubscribeContextChange(Runnable action);
    public String getLastOperationStatus();
}
