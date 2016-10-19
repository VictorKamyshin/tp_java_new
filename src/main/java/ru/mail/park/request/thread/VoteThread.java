package ru.mail.park.request.thread;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class VoteThread{
    private Integer vote;
    private Integer threadId;

    @JsonCreator
    private VoteThread(@JsonProperty("thread") Integer threadId,
                              @JsonProperty("vote") Integer vote) {
        this.vote = vote;
        this.threadId = threadId;
    }

    public Integer getVote() {
        return vote;
    }

    public Integer getThreadId() {
        return threadId;
    }
}