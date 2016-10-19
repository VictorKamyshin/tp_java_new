package ru.mail.park.model;

/**
 * Created by victor on 12.10.16.
 */
public class Forum {
    private int id;
    private String name;
    private String shortName;
    private int user_id;
    private String user_email;
    private String userInfo;

    public Forum(int id, String name, String shortName, int user_id, String user_email) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.user_id = user_id;
        this.user_email = user_email;
    }

    public Forum(String name, String shortName, String user_email) {
        this.id = -1;
        this.name = name;
        this.shortName = shortName;
        this.user_id = -1;
        this.user_email = user_email;
    }

    public Forum(int id, String name, String shortName, String user_email) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.user_email = user_email;
    }

    public void setId(Integer id) {this.id = id; }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String incert() {
        String result = "Insert into forums (name, short_name, user_id" +
                ") values ('" + name + "','" + shortName +
                "'," + user_id + ");";
        return result;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"id\":");
        builder.append(id);
        builder.append(", \"name\":\"");
        builder.append(name);
        builder.append("\", \"short_name\":\"");
        builder.append(shortName);
        builder.append("\", \"user\":\"");
        builder.append(user_email);
        builder.append("\"}");
        String result = builder.toString();
        return result;
    }

    public String toJSONUserInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"id\":");
        builder.append(id);
        builder.append(", \"name\":\"");
        builder.append(name);
        builder.append("\", \"short_name\":\"");
        builder.append(shortName);
        builder.append("\", \"user\":");
        builder.append(userInfo);
        builder.append("}");
        String result = builder.toString();
        return result;
    }
}
