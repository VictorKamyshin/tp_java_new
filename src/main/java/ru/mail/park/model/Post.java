package ru.mail.park.model;

/**
 * Created by victor on 13.10.16.
 */
public class Post {
    private Integer id;
    private Integer threadId;
    private String message;
    private String date;
    private Integer userId;
    private Integer forumId;
    private Integer parent_id;
    private Boolean isSpam;
    private Boolean isApproved;
    private Boolean isHighlighted;
    private Boolean isEdited;
    private Boolean isDeleted;
    private Integer likes;
    private Integer dislikes;
    private Integer points;
    private String userEmail;
    private String forumShortName;

    private String forumInfo;
    private String threadInfo;
    private String userInfo;

    private int childs;
    private String materialPath;
    private String reverseMaterialPath;
    private int neighbors;

    public Post(String date, Integer threadId, String message, String userEmail,
                String forumShortName, Integer parent_id, Boolean isApproved, Boolean isHighlighted,
                Boolean isEdited, Boolean isSpam, Boolean isDeleted) {
        this.date = date;
        this.threadId = threadId;
        this.message = message;
        this.userEmail = userEmail;
        this.forumShortName = forumShortName;

        this.parent_id = parent_id;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isSpam = isSpam;
        this.isDeleted = isDeleted;

        likes = 0;
        dislikes = 0;
        points = 0;
    }

    public Post(Integer id, Integer threadId, String message, String date,
                Integer userId, Integer forumId, Integer parent_id, Boolean isSpam,
                Boolean isApproved, Boolean isHighlighted, Boolean isEdited, Boolean isDeleted,
                Integer likes, Integer dislikes, Integer points) {
        this.id = id;
        this.threadId = threadId;
        this.message = message;
        this.date = date;
        this.userId = userId;
        this.forumId = forumId;
        this.parent_id = parent_id;
        this.isSpam = isSpam;
        this.isApproved = isApproved;
        this.isHighlighted = isHighlighted;
        this.isEdited = isEdited;
        this.isDeleted = isDeleted;
        this.likes = likes;
        this.dislikes = dislikes;
        this.points = points;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setForumId(Integer forum_id) {
        this.forumId = forum_id;
    }

    public void setUserId(Integer user_id) {
        this.userId = user_id;
    }

    public Integer getUserId() {return this.userId; }

    public Integer getPoints() {
        return this.points;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setForumShortName(String forumShortName) {
        this.forumShortName = forumShortName;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setForumInfo(String forumInfo) {
        this.forumInfo = forumInfo;
    }

    public int getChilds() {
        return childs;
    }

    public void setChilds(int childs) {
        this.childs = childs;
    }

    public String getMaterialPath() {
        return materialPath;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
    }

    public String getReverseMaterialPath() {
        return reverseMaterialPath;
    }

    public void setReverseMaterialPath(String reverseMaterialPath) {
        this.reverseMaterialPath = reverseMaterialPath;
    }

    public int getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(int neighbors) {
        this.neighbors = neighbors;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setThreadInfo(String threadInfo) {
        this.threadInfo = threadInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public Integer like(){
        likes= likes + 1;
        points = points + 1;
        return likes;
    }

    public Integer dislike(){
        dislikes = dislikes + 1;
        points = points - 1;
        return dislikes;
    }

    public String insert() {
        StringBuilder builder = new StringBuilder();
        builder.append("Insert into posts (date, thread_id, message, user_id, forum_id, " +
                "parent_id, isApproved, isHighlighted, isEdited, isSpam, isDeleted, " +
                "materialPath, reverseMaterialPath) values ('");
        builder.append(date);
        builder.append("', ");
        builder.append(threadId);
        builder.append(", '");
        builder.append(message);
        builder.append("', ");
        builder.append(userId);
        builder.append(", ");
        builder.append(forumId);
        builder.append(", ");
        builder.append(parent_id);
        builder.append(", ");
        builder.append(isApproved);
        builder.append(", ");
        builder.append(isHighlighted);
        builder.append(", ");
        builder.append(isEdited);
        builder.append(", ");
        builder.append(isSpam);
        builder.append(", ");
        builder.append(isDeleted);
        builder.append(", \"");
        builder.append(materialPath);
        builder.append("\", \"");
        builder.append(reverseMaterialPath);
        builder.append("\");");

        String result = builder.toString();
        return result;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"date\":\"");
        builder.append(date);
        builder.append("\", \"forum\":\"");
        builder.append(forumShortName);
        builder.append("\", \"id\":");
        builder.append(id);
        builder.append(", \"isApproved\":");
        builder.append(isApproved);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"isEdited\":");
        builder.append(isEdited);
        builder.append(", \"isHighlighted\":");
        builder.append(isHighlighted);
        builder.append(", \"isSpam\":");
        builder.append(isSpam);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"parent\":");
        builder.append(parent_id);
        builder.append(", \"thread\":");
        builder.append(threadId);
        builder.append(", \"user\":\"");
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
        builder.append(forumShortName);
        builder.append("\", \"id\":");
        builder.append(id);
        builder.append(", \"isApproved\":");
        builder.append(isApproved);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"isEdited\":");
        builder.append(isEdited);
        builder.append(", \"isHighlighted\":");
        builder.append(isHighlighted);
        builder.append(", \"isSpam\":");
        builder.append(isSpam);
        builder.append(", \"likes\":");
        builder.append(likes);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"parent\":");
        builder.append(parent_id);
        builder.append(", \"points\":");
        builder.append(points);
        builder.append(", \"thread\":");
        builder.append(threadId);
        builder.append(", \"user\":\"");
        builder.append(userEmail);
        builder.append("\"}");
        String result = builder.toString();
        return result;
    }

    public String toJSONwithInfo(Boolean forumRequered, Boolean threadRequered, Boolean userRequered) {
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
            builder.append(forumShortName);
            builder.append("\"");
        }
        builder.append(", \"id\":");
        builder.append(id);
        builder.append(", \"isApproved\":");
        builder.append(isApproved);
        builder.append(", \"isDeleted\":");
        builder.append(isDeleted);
        builder.append(", \"isEdited\":");
        builder.append(isEdited);
        builder.append(", \"isHighlighted\":");
        builder.append(isHighlighted);
        builder.append(", \"isSpam\":");
        builder.append(isSpam);
        builder.append(", \"likes\":");
        builder.append(likes);
        builder.append(", \"message\":\"");
        builder.append(message);
        builder.append("\", \"parent\":");
        builder.append(parent_id);
        builder.append(", \"points\":");
        builder.append(points);
        builder.append(", \"thread\":");
        if(threadRequered) {
            builder.append(threadInfo);
        } else {
            builder.append(threadId);
        }
        builder.append(", \"user\":");
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
