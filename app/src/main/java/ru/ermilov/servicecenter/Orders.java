package ru.ermilov.servicecenter;

public class Orders {
    public  String  Category, Condition, Problema, DateStart, DateEnd;

    public Orders(){

    }

    public Orders(  String Category, String Condition, String Problema, String DateStart, String DateEnd){
        //this.Image = Image;
        this.Category = Category;
        this.Condition = Condition;
        this.Problema = Problema;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
    }
}
