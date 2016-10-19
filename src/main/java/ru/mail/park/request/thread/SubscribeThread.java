package ru.mail.park.request.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class SubscribeThread{
    private String userEmail;
    private Integer threadId;

    @JsonCreator
    private SubscribeThread(@JsonProperty("thread") Integer threadId,
                                   @JsonProperty("user") String userEmail) {
        this.userEmail = userEmail;
        this.threadId = threadId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getThreadId() {
        return threadId;
    }
}