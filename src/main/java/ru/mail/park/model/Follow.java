package ru.mail.park.model;

/**
 * Created by victor on 12.10.16.
 */
public class Follow {
    private int followerId; //тот, кто фолловит?
    private int follwingId; // тот, кого фолловят

    public Follow(int followerId, int follwingId) {
        this.followerId = followerId;
        this.follwingId = follwingId;
    }


    public int getFollowerId() {
        return followerId;
    }

    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }

    public int getFollwingId() {
        return follwingId;
    }

    public void setFollwingId(int follwingId) {
        this.follwingId = follwingId;
    }

    public String insert() {
        StringBuilder builder = new StringBuilder();
        builder.append("Insert into followers (follower_id, following_id) values (");
        builder.append(followerId);
        builder.append(", ");
        builder.append(follwingId);
        builder.append(");");
        String result = builder.toString();
        return result;
    }

    public String delete() {
        StringBuilder builder = new StringBuilder();
        builder.append("Delete from followers where follower_id = ");
        builder.append(followerId);
        builder.append(" and following_id =  ");
        builder.append(follwingId);
        builder.append(";");
        String result = builder.toString();
        return result;
    }
}
