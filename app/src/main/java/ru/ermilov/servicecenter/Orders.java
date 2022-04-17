package ru.ermilov.servicecenter;

public class Orders {
    public String Category, Condition, Problema, DateStart, DateEnd, ImageUri;

    public Orders(){

    }

    public Orders(  String Category, String Condition, String Problema, String DateStart, String DateEnd, String ImageUri){
        //this.Image = Image;
        this.Category = Category;
        this.Condition = Condition;
        this.Problema = Problema;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
        this.ImageUri = ImageUri;
    }
}
