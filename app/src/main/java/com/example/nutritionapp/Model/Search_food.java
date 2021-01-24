package com.example.nutritionapp.Model;

public class Search_food {
    String name, calorii, image;

    public Search_food() {
    }

    public Search_food(String name, String calorii, String image) {
        this.name = name;
        this.calorii = calorii;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalorii() {
        return calorii;
    }

    public void setCalorii(String calorii) {
        this.calorii = calorii;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
