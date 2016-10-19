package ru.mail.park.request.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class FollowUnfollowUser {
    private String follower;
    private String followee;

    @JsonCreator
    private FollowUnfollowUser(@JsonProperty("follower") String follower,
                               @JsonProperty("followee") String followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public String getFollower() {
        return follower;
    }

    public String getFollowee() {
        return followee;
    }
}
