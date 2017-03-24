package com.opensis.shanu.opensis_attendant;

/**
 * Created by Shanu on 2/24/2017.
 */

public class CalendarEvent {
    private Integer id;
    private Integer event_id;
    private String date;
    private String title;
    private String message;


    public CalendarEvent(){

    }

    public CalendarEvent(Integer event_id, String title, String message, String date) {
        this.event_id = event_id;
        this.date = date;
        this.title = title;
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEvent_id() {
        return event_id;
    }

    public void setEvent_id(Integer event_id) {
        this.event_id = event_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
