package ru.mail.park.model;

/**
 * Created by victor on 13.10.16.
 */
public class Subcribe {
    private Integer id;
    private Integer userId;
    private Integer threadId;
    private String userEmail;

    public Subcribe(Integer threadId, String userEmail) {
        this.id = -1;
        this.userId = -1;
        this.threadId = threadId;
        this.userEmail = userEmail;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public String insert () {
        StringBuilder builder = new StringBuilder();
        builder.append("Insert subscribe (user_id, thread_id) values (");
        builder.append(userId);
        builder.append(", ");
        builder.append(threadId);
        builder.append(");");
        final String result = builder.toString();
        return result;
    }

    public String delete () {
        StringBuilder builder = new StringBuilder();
        builder.append("Delete from subscribe where user_id = \"");
        builder.append(userId);
        builder.append("\" and thread_id = \"");
        builder.append(threadId);
        builder.append("\";");
        final String result = builder.toString();
        return result;
    }

    public String toJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"thread\": ");
        builder.append(threadId);
        builder.append(", \"user\": \"");
        builder.append(userEmail);
        builder.append("\"}");
        final String result = builder.toString();
        return result;
    }
}
