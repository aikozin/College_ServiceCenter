package ru.ermilov.servicecenter;

public class Orders {
    public String Fio, CategoryKey, Condition, Problema, DateStart, DateEnd, ImageUri;
    public String keyOrder;
    public String Category;

    public Orders(){

    }

    public Orders( String Fio ,String CategoryKey, String Condition, String Problema, String DateStart, String DateEnd, String ImageUri){

        this.Fio = Fio;
        this.CategoryKey = CategoryKey;
        this.Condition = Condition;
        this.Problema = Problema;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
        this.ImageUri = ImageUri;
    }
}
