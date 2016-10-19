package ru.mail.park.request.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class CreateUser {
    private String username;
    private String about;
    private boolean isAnonymos;
    private String name;
    private String email;

    @JsonCreator
    public CreateUser(@JsonProperty("username") String username,
                              @JsonProperty("about") String about,
                              @JsonProperty("isAnonymous") boolean isAnonymos,
                              @JsonProperty("name") String name,
                              @JsonProperty("email") String email) {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//        JsonNode df = mapper.readValue(x, JsonNode.class)
        this.username = username;
        this.about = about;
        this.isAnonymos = isAnonymos;
        this.name = name;
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public String getAbout() {
        return about;
    }

    public boolean getAnonymos() {
        return isAnonymos;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
