package ru.ermilov.servicecenter;

public class Orders {
    public String Fio, Category, Condition, Problema, DateStart, DateEnd, ImageUri;
    public String key;

    public Orders(){

    }

    public Orders( String Fio ,String Category, String Condition, String Problema, String DateStart, String DateEnd, String ImageUri){

        this.Fio = Fio;
        this.Category = Category;
        this.Condition = Condition;
        this.Problema = Problema;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
        this.ImageUri = ImageUri;
    }
}
