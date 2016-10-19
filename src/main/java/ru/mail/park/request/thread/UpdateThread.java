package ru.mail.park.request.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class UpdateThread{
    private String message;
    private String slug;
    private Integer threadId;

    @JsonCreator
    public UpdateThread(@JsonProperty("thread") Integer threadId,
                                @JsonProperty("message") String message,
                                @JsonProperty("slug") String slug) {
        this.message = message;
        this.slug = slug;
        this.threadId = threadId;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getThreadId() {
        return threadId;
    }
}