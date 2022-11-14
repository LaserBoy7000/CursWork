package com.labs.core.helper;

import java.util.ArrayList;
import java.util.List;

public class ActionEvent {
    private List<Runnable> subscribers = new ArrayList<Runnable>();

    public void attach(Runnable action){
        if(!subscribers.contains(action))
            subscribers.add(action);
    }

    public void detach(Runnable action){
        if(subscribers.contains(action))
            subscribers.remove(action);
    }

    public void InvokeAll(){
        for (Runnable runnable : subscribers)
            runnable.run();
    }
}
