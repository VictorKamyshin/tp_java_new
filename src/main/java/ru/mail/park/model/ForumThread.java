package ru.mail.park.model;

/**
 * Created by victor on 13.10.16.
 */
public class ForumThread {
    private int id;
    private int user_id;
    private int forum_id;
    private String title;
    private boolean isClosed;
    private boolean isDeleted;
    private String date;
    private String message;
    private String slug;
    private Integer likes;
    private Integer dislikes;
    private Integer points;

    private Integer posts;
    private String userEmail;
    private String forumName;

    private String userInfo;
    private String forumInfo;

    public ForumThread(int id, String title, boolean isClosed, boolean isDeleted, String date,
                       String message, String slug, Integer likes, Integer dislikes, Integer points,
                       Integer posts, String userEmail, String forumName) {
        this.id = id;
        this.title = title;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.likes = likes;
        this.dislikes = dislikes;
        this.points = points;
        this.posts = posts;
        this.userEmail = userEmail;
        this.forumName = forumName;
    }

    public ForumThread(int id, int user_id, int forum_id, String title, String date, String message, String slug) {
        this.id = id;
        this.user_id = user_id;
        this.forum_id = forum_id;
        this.title = title;
        this.date = date;
        this.message = message;
        this.slug = slug;
    }

    public ForumThread(int id, int user_id, int forum_id, String title,
                       boolean isClosed, boolean isDeleted, String date,
                       String message, String slug, Integer likes, Integer dislikes, Integer points) {
        this.id = id;
        this.user_id = user_id;
        this.forum_id = forum_id;
        this.title = title;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.date = date;
        this.message = message;
        this.slug = slug;
        this.likes = likes;
        this.dislikes = dislikes;
        this.points = points;
    }

    public ForumThread(String date, Boolean isClosed, Boolean isDeleted,
                       String message, String slug, String title, String userEmail, String forumName) {
        this.id=-1;
        this.user_id = -1;
        this.forum_id = -1;
        this.date = date;
        this.isClosed = isClosed;
        this.isDeleted = isDeleted;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.userEmail = userEmail;
        this.forumName = forumName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setForum_id(int forum_id) {
        this.forum_id = forum_id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public void setForumInfo(String forumInfo) {
        this.forumInfo = forumInfo;
    }

    public Integer like(){
        likes=likes + 1;
        points=points+ 1;
        return likes;
    }

    public Integer dislike(){
        dislikes=dislikes+1;
        points=points-1;
        return dislikes;
    }

    public Integer getPoints() {
        return points;
    }

    public String insert() {
        StringBuilder builder = new StringBuilder();
        builder.append("Insert into threads (user_id, forum_id, title, date, isClosed, isDeleted, message, slug) values (");
        builder.append(user_id);
        builder.append(", ");
        builder.append(forum_id);
        builder.append(", '");
        builder.append(title);
        builder.append("', '");
        builder.append(date);
        builder.append("', ");
        builder.append(isClosed);
        builder.append(", ");
        builder.append(isDeleted);
        builder.append(", '");
        builder.append(message);
        builder.append("', '");
        builder.append(slug);
        builder.append("');");
        String result = builder.toString();
        return result;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"date\":\"");
        builder.append(date);
        builder.append("\", \"forum\":\"");
        builder.append(forumName);
        builder.append("\", \"id\":");
        builder.append(id);
        builder.append(", \"isClosed\":");
        builder.append(isClosed);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"slug\":\"");
        builder.append(slug);
        builder.append("\", \"title\":\"");
        builder.append(title);
        builder.append("\", \"user\":\"");
        builder.append(userEmail);
        builder.append("\"}");
        String result = builder.toString();
        return result;
    }

    public String toJSONDetails() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"date\":\"");
        builder.append(date);
        builder.append("\", \"dislikes\":");
        builder.append(dislikes);
        builder.append(", \"forum\":\"");
        builder.append(forumName);
        builder.append("\", \"id\":");
        builder.append(id);
        builder.append(", \"isClosed\":");
        builder.append(isClosed);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"likes\":");
        builder.append(likes);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"points\":");
        builder.append(points);
        builder.append(", \"posts\":");
        builder.append(posts);
        builder.append(", \"slug\":\"");
        builder.append(slug);
        builder.append("\", \"title\":\"");
        builder.append(title);
        builder.append("\", \"user\":\"");
        builder.append(userEmail);
        builder.append("\"}");
        String result = builder.toString();
        return result;
    }

    public String toJSONwithInfo(Boolean forumRequered, Boolean userRequered) {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"date\":\"");
        builder.append(date);
        builder.append("\", \"dislikes\":");
        builder.append(dislikes);
        builder.append(", \"forum\":");
        if(forumRequered) {
            builder.append(forumInfo);
        } else {
            builder.append("\"");
            builder.append(forumName);
            builder.append("\"");
        }
        builder.append(", \"id\":");
        builder.append(id);
        builder.append(", \"isClosed\":");
        builder.append(isClosed);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"likes\":");
        builder.append(likes);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"points\":");
        builder.append(points);
        builder.append(", \"posts\":");
        builder.append(posts);
        builder.append(", \"slug\":\"");
        builder.append(slug);
        builder.append("\", \"title\":\"");
        builder.append(title);
        builder.append("\", \"user\":");
        if(userRequered) {
            builder.append(userInfo);
        } else {
            builder.append("\"");
            builder.append(userEmail);
            builder.append("\"");
        }
        builder.append("}");
        String result = builder.toString();
        return result;
    }

}
