package ru.ermilov.servicecenter;

public class Orders {
    public  String Condition, Problema, DateStart, DateEnd;

    public Orders(){

    }

    public Orders(String Condition, String Problema, String DateStart, String DateEnd){

        this.Condition = Condition;
        this.Problema = Problema;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
    }
}
