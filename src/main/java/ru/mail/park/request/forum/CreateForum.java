package ru.mail.park.request.forum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by victor on 17.10.16.
 */
public class CreateForum {
    private String name;
    private String short_name;
    private String user_email;

    @JsonCreator
    public CreateForum(@JsonProperty("name") String name,
                               @JsonProperty("short_name") String short_name,
                               @JsonProperty("user") String user_email) {
/*        String str = name.split(" ")[0];
        str = str.replace("\\","");
        String[] arr = str.split("u");
        String text = "";
        for(int i = 1; i < arr.length; i++){
            int hexVal = Integer.parseInt(arr[i], 16);
            text += (char)hexVal;
        } */
        this.name = name;
        this.short_name = short_name;
        this.user_email = user_email;
    }

    public String getName() {
        return name;
    }

    public String getShort_name() {
        return short_name;
    }

    public String getUser_email() {
        return user_email;
    }
}