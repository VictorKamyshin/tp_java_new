package ru.mail.park.request.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public final class CreateThread {
    private String date;
    private String forum_name;
    private boolean isClosed;
    private boolean isDeleted;
    private String message;
    private String slug;
    private String title;
    private String user_email;

    @JsonCreator
    public CreateThread(@JsonProperty("date") String date,
                                @JsonProperty("forum") String forum_name,
                                @JsonProperty("isClosed") boolean isClosed,
                                @JsonProperty("isDeleted") boolean isDeleted,
                                @JsonProperty("message") String message,
                                @JsonProperty("slug") String slug,
                                @JsonProperty("title") String title,
                                @JsonProperty("user") String user_email) {
        this.date = date;
        this.forum_name = forum_name;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.user_email = user_email;
    }

    public String getDate() {
        return date;
    }

    public String getForum_name() {
        return forum_name;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_email() {
        return user_email;
    }
}