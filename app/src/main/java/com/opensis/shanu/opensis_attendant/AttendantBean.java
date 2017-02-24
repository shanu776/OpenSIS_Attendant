package com.opensis.shanu.opensis_attendant;

/**
 * Created by Shanu on 2/22/2017.
 */

public class AttendantBean {
    Integer id;
    Integer attendantId;
    String name;
    String phone;
    String email;
    String beaconId;
    String image;

    public AttendantBean() {

    }

    public AttendantBean(Integer attendantId, String name, String phone, String email, String beaconId) {
        this.attendantId = attendantId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.beaconId = beaconId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(Integer attendantId) {
        this.attendantId = attendantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBeaconId() {
        return beaconId;
    }

    public void setBeaconId(String beaconId) {
        this.beaconId = beaconId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
