package ru.mail.park.request.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class StatusChange{
    private Integer threadId;

    @JsonCreator
    public StatusChange(@JsonProperty("thread") Integer threadId) {
        this.threadId = threadId;
    }

    public Integer getThreadId() {
        return threadId;
    }
}