package com.example.messimusic;

public class Person {
    int Img;
    String Name;
    String Des;

    public Person(int img, String name, String des) {
        Img = img;
        Name = name;
        Des = des;
    }

    public int getImg() {
        return Img;
    }

    public void setImg(int img) {
        Img = img;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDes() {
        return Des;
    }

    public void setDes(String des) {
        Des = des;
    }
}
