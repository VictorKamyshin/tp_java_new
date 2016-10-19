package ru.mail.park.response;

/**
 * Created by victor on 17.10.16.
 */
public class SuccessResponse {
    private String body;
    private Integer code;

    public SuccessResponse(Integer code, String body) {
        this.code = code;
        this.body = body;
    }

    public String createJSONResponce(){
        String responce = "{\"code\": " + code + ", \"response\": " + body + "}";
        return responce;
    }

}