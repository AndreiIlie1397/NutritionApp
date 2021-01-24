package com.example.nutritionapp.Model;

public class UserData {

    private String name, email, date, weight, height, gender, activity, profile_image, calorii, carbohidrati, grasimi, proteine, obiectiv;

    public UserData() {
    }

    public UserData(String name, String email, String date, String weight, String height, String gender, String activity, String calorii, String carbohidrati, String grasimi, String proteine, String profile_image, String obiectiv) {
        this.name = name;
        this.email = email;
        this.date = date;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.activity = activity;
        this.calorii = calorii;
        this.carbohidrati = carbohidrati;
        this.grasimi = grasimi;
        this.proteine = proteine;
        this.profile_image = profile_image;
        this.obiectiv = obiectiv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCalorii() {
        return calorii;
    }

    public void setCalorii(String calorii) {
        this.calorii = calorii;
    }

    public String getCarbohidrati() {
        return carbohidrati;
    }

    public void setCarbohidrati(String carbohidrati) {
        this.carbohidrati = carbohidrati;
    }

    public String getGrasimi() {
        return grasimi;
    }

    public void setGrasimi(String grasimi) {
        this.grasimi = grasimi;
    }

    public String getProteine() {
        return proteine;
    }

    public void setProteine(String proteine) {
        this.proteine = proteine;
    }

    public String getObiectiv() {
        return obiectiv;
    }

    public void setObiectiv(String obiectiv) {
        this.obiectiv = obiectiv;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", date='" + date + '\'' +
                ", weight='" + weight + '\'' +
                ", height='" + height + '\'' +
                ", gender='" + gender + '\'' +
                ", activity='" + activity + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", calorii=" + calorii +
                ", carbohidrati=" + carbohidrati +
                ", grasimi=" + grasimi +
                ", proteine=" + proteine +
                ", obiectiv =" + obiectiv +
                '}';
    }
}
