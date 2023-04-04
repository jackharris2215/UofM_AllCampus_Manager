package com.example.moneymanager;

public class Action {
    double amount;
    int zoo;
    Action next;

    public Action(double amount, int zoo){
        this.amount = amount;
        this.zoo = zoo;
    }
}

