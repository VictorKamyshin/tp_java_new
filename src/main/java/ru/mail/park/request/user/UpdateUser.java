package ru.mail.park.request.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class UpdateUser {
    private String email;
    private String about;
    private String name;

    @JsonCreator
    private UpdateUser(@JsonProperty("user") String email,
                       @JsonProperty("about") String about,
                       @JsonProperty("name") String name) {
        this.email = email;
        this.about = about;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }
}
