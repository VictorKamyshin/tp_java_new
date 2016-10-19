package ru.mail.park.model;

/**
 * Created by Solovyev on 17/09/16.
 */
public class UserProfile {
    private long id;
    private Boolean isAnonymos;
    private String email;
    private String name;
    private String about;
    private String username;
    private String listOfFollower;
    private String listOfFollowing;
    private String listOfSubscriptions;


    public UserProfile(String about, String email, int id, Boolean isAnonymos,String name,
                       String username) {
        this.id = id;
        this.isAnonymos = isAnonymos;
        this.email = email;
        this.name = name;
        this.about = about;
        this.username = username;
        this.listOfFollower = "[ ]";
        this.listOfFollowing = "[ ]";
        this.listOfSubscriptions = "[ ]";
    }

    public UserProfile(Boolean isAnonymos, String email, String name, String about, String username) {
        this.id = -1;
        if(isAnonymos.equals(null)){
            isAnonymos = true;
        }
        this.isAnonymos = isAnonymos;
        this.email = email;
        this.name = name;
        this.about = about;
        this.username = username;
        this.listOfFollower = "[ ]";
        this.listOfFollowing = "[ ]";
        this.listOfSubscriptions = "[ ]";
    }

    public UserProfile(String email, String name, String about, String username) {
        this.id = -1;
        this.isAnonymos = false;
        this.email = email;
        this.name = name;
        this.about = about;
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public Boolean getIsAnonymos() {
        return isAnonymos;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAbout() {
        return about;
    }

    public String getUsername() {
        return username;
    }

    public void setId(Integer id) {this.id = id; }

    public void setIsAnonymos(boolean isAnonymos) {
        this.isAnonymos = isAnonymos;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setListOfFollower(String listOfFollower) {
        this.listOfFollower = listOfFollower;
    }

    public void setListOfFollowing(String listOfFollowing) {
        this.listOfFollowing = listOfFollowing;
    }

    public void setListOfSubscriptions(String listOfSubscriptions) {
        this.listOfSubscriptions = listOfSubscriptions;
    }

    public String getListOfSubscriptions() {
        return listOfSubscriptions;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        if(about!=null) {
            builder.append("\"about\": ");
            builder.append("\"");
            builder.append(about);
            builder.append("\",");
        }
        builder.append("\"email\": \"");
        builder.append(email);
        builder.append("\", \"id\":");
        builder.append(id);
        builder.append(", \"isAnonymous\":");
        builder.append(isAnonymos);
        if(name!=null) {
            builder.append(", \"name\": ");
            builder.append("\"");
            builder.append(name);
            builder.append("\"");
        }
        if(username!=null) {
            builder.append(", \"username\": ");
            builder.append("\"");
            builder.append(username);
            builder.append("\"");
        }
        builder.append("}");
        String result = builder.toString();
       // String result = "\"about\":"+about + "";
        result = result.replaceAll("\"null\"","null");
        return result;
    }

    public String toJSONDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"about\":");
        if(about!=null) {
            builder.append("\"");
            builder.append(about);
            builder.append("\"");
        } else {
            builder.append("null");
        }
        builder.append(", \"email\": \"");
        builder.append(email);
        builder.append("\", \"followers\": ");
        builder.append(listOfFollower);
        builder.append(", \"following\": ");
        builder.append(listOfFollowing);
        builder.append(", \"id\":");
        builder.append(id);
        builder.append(", \"isAnonymous\":");
        builder.append(isAnonymos);
        builder.append(", \"name\":");
        if(name!=null) {
            builder.append("\"");
            builder.append(name);
            builder.append("\"");
        } else {
            builder.append("null");
        }
        builder.append(", \"subscriptions\": ");
        builder.append(listOfSubscriptions);
        builder.append(", \"username\":");
        if(username!=null) {
            builder.append("\"");
            builder.append(username);
            builder.append("\"");
        } else {
            builder.append("null");
        }
        builder.append("}");
        String result = builder.toString();
        result = result.replaceAll("\"null\"","null");
        return result;
    }

    public String incert(String tablename) {
        String result = "";
        if(name==null){
            result = "Insert into " + tablename + "(isAnonymos, username, about,  " +
                    "email) values (" + isAnonymos+ ",'" + username +  "','" + about +"', '"+ email + "');";
        } else {
            result = "Insert into " + tablename + "(isAnonymos, username, about, name, email) values (" +
                    isAnonymos + ",'" + username + "','" + about+ "', '" + name + "', '" + email + "');";
        }
        return result;
    }

    public String update(){
        StringBuilder builder = new StringBuilder();
        builder.append("Update users set about = \"");
        builder.append(about);
        builder.append("\", name = \"");
        builder.append(name);
        builder.append("\" where id = ");
        builder.append(id);
        builder.append(";");
        final String result =  builder.toString();
        return result;
    }

}
