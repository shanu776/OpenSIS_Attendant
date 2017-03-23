package com.opensis.shanu.opensis_attendant;

/**
 * Created by Shanu on 2/22/2017.
 */

public class StudentBean {
    Integer id;
    String name;
    String beacon_id;
    String email;
    String image_title;
    String image;

    public StudentBean() {

    }

    public StudentBean(String name, String beacon_id, String email, String image_title, String image) {
        this.name = name;
        this.beacon_id = beacon_id;
        this.email = email;
        this.image_title = image_title;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeacon_id() {
        return beacon_id;
    }

    public void setBeacon_id(String beacon_id) {
        this.beacon_id = beacon_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage_title() {
        return image_title;
    }

    public void setImage_title(String image_title) {
        this.image_title = image_title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
