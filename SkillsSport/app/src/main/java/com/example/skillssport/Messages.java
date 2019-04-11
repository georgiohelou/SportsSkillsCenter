package com.example.skillssport;

/**
 * Created by User on 11/17/2018.
 */

public class Messages {
    private String message,type,from;
    boolean seen,group;
    long time;

    public Messages(){}

    public Messages(String message, String type, String from, boolean seen, boolean group, long time) {
        this.message = message;
        this.type = type;
        this.from = from;
        this.seen = seen;
        this.group = group;
        this.time = time;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isSeen() {
        return seen;
    }

    public String getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }
}
