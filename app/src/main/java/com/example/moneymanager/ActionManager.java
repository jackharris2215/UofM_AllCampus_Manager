package com.example.moneymanager;

public class ActionManager {

    Action first;

    public ActionManager(Action first){
        this.first = first;
    }

    public Action pop(){
        if(this.first != null){
            Action action = this.first;
            this.first = action.next;
            return action;
        }
        return null;
    }

    public void push(Action action){
        action.next = this.first;
        this.first = action;
    }
}
