package ru.mail.park.main;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.Forum;
import ru.mail.park.model.ForumThread;
import ru.mail.park.model.Post;
import ru.mail.park.model.UserProfile;
import ru.mail.park.request.forum.*;
import ru.mail.park.response.SuccessResponse;

import java.sql.*;

/**
 * Created by victor on 17.10.16.
 */
@RestController
public class ForumController {
    static final String URL = "jdbc:mysql://localhost:3306/dbtest?characterEncoding=UTF-8";
    static final String USERNAME = "root";
    static final String PASSWORD = "Dbrnjh1993";

    @RequestMapping(path = "/db/api/forum/create/", method= RequestMethod.POST)
    public ResponseEntity forumCreate(@RequestBody CreateForum body) {
        final String name = body.getName();
        final String short_name = body.getShort_name();
        final String user_email = body.getUser_email();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            Forum forum = new Forum(name, short_name, user_email);
            final String getUserId = "Select id from users where email = '" + user_email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                forum.setUser_id(resultSet.getInt(1));
            }
            final String createForum = forum.incert();
            PreparedStatement preparedStatementCreateForum = connection.prepareStatement(createForum);
            preparedStatementCreateForum.executeUpdate();
            final String getForumId = "Select id from forums where short_name = '" + short_name + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()) {
                forum.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,forum.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/forum/listUsers/", method= RequestMethod.GET)
    public ResponseEntity forumListUsers(@RequestParam String forum,
                                         @RequestParam(required = false) String since_id,
                                         @RequestParam(required = false) Integer limit,
                                         @RequestParam(required = false) String order) {
        final String forumShortName = forum;
        Integer forumId = -1;
        if(order==null){
            order = "desc";
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String getForumId = "Select id from forums where short_name =\"" + forumShortName+"\"";
            ResultSet resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()){
                forumId = resultSet.getInt(1);
            } //получили айди форума

            StringBuilder userListBuilder = new StringBuilder();
            userListBuilder.append("[");

            //надо обмазаться параметрами
            StringBuilder getUserListBuilder = new StringBuilder();
            getUserListBuilder.append("Select DISTINCT posts.user_id, users.name from posts left join users on " +
                    "posts.user_id = users.id where forum_id = ");
            getUserListBuilder.append(forumId);
            if(since_id!=null){
                getUserListBuilder.append(" and user_id >= ");
                getUserListBuilder.append(since_id);
            }
            getUserListBuilder.append(" order by users.name ");
            getUserListBuilder.append(order);
            if(limit!=null){
                getUserListBuilder.append(" limit ");
                getUserListBuilder.append(limit);
            }
            getUserListBuilder.append(";");
            final String getUsersList = getUserListBuilder.toString();
          //  System.out.println(getUserListBuilder);
            resultSet = connection.createStatement().executeQuery(getUsersList);
            while(resultSet.next()){
                StringBuilder builder = new StringBuilder();

                final Integer userId = resultSet.getInt("posts.user_id");
                String userEmail = "";
                String about = "";
                Boolean isAnonymos = true;
                String name = "";
                String username = "";

                final String getUser = "Select * from users where id = '" + userId + "';";
                ResultSet userResultSet = connection.createStatement().executeQuery(getUser);
                while(userResultSet.next()) {
                    about = userResultSet.getString("about");
                    userEmail = userResultSet.getString("email");
                    isAnonymos = userResultSet.getBoolean("isAnonymos");
                    name = userResultSet.getString("name");
                    username = userResultSet.getString("username");
                }
                UserProfile newUser = new UserProfile(about, userEmail, userId, isAnonymos, name, username);

                builder.append("[");
                final String getFollowers = "Select follower_id from followers where following_id= '" + newUser.getId() + "';";
                userResultSet = connection.createStatement().executeQuery(getFollowers);
                while(userResultSet.next()) {
                    final String getFollowerEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                    while(innerResultSet.next()){
                        builder.append(innerResultSet.getString(1));
                    }
                    if(userResultSet.next()){
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                newUser.setListOfFollower(builder.toString());
                builder.setLength(0);

                builder.append("[");
                final String getFollowing = "Select following_id from followers where follower_id= "
                        + newUser.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getFollowing);
                while(userResultSet.next()) {
                    final String getFollowingEmails = "Select email from users where id = " +
                            userResultSet.getInt(1) + ";";

                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                    while(innerResultSet.next()){
                        builder.append(innerResultSet.getString(1));
                    }
                    if(userResultSet.next()){
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                newUser.setListOfFollowing(builder.toString());
                builder.setLength(0);

                builder.append("[");
                final String getSubscription = "Select thread_id from subscribe where user_id= " + newUser.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getSubscription);
                while(userResultSet.next()) {
                    builder.append(userResultSet.getInt(1));
                    if(userResultSet.next()){
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                newUser.setListOfSubscriptions(builder.toString());
                builder.setLength(0);

                userListBuilder.append(newUser.toJSONDetails());
                if(resultSet.next()){
                    userListBuilder.append(", ");
                    resultSet.previous();
                }
            }
            userListBuilder.append("]");

            connection.close();
            SuccessResponse response = new SuccessResponse(0,userListBuilder.toString());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/forum/listPosts/", method= RequestMethod.GET)
    public ResponseEntity forumListPosts(@RequestParam(required = false) String[] related,
                                         @RequestParam String forum,
                                         @RequestParam(required = false) String since,
                                         @RequestParam(required = false) Integer limit,
                                         @RequestParam(required = false) String order) {
        Boolean userInfoRequered = false;
        Boolean threadInfoRequered = false;
        Boolean forumInfoRequered = false;
        if(related!=null)
        for(Integer i = 0; i < related.length;++i) {
            if(related[i].equals("user")){ userInfoRequered = true; }
            if(related[i].equals("forum")){ forumInfoRequered = true; }
            if(related[i].equals("thread")){ threadInfoRequered = true; }
        }

        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            StringBuilder builder = new StringBuilder();

            builder.append("Select * from ((posts left join users on posts.user_id = users.id) " +
                    "left join threads on posts.thread_id = threads.id) " +
                    "left join forums on posts.forum_id = forums.id where forums.short_name =\"");
            builder.append(forum);
            builder.append("\"");
            if(since!=null){
                builder.append("and posts.date > \"");
                builder.append(since);
                builder.append("\" ");
            }
            if(order!= null){
                builder.append(" order by posts.date ");
                builder.append(order);
            } else {
                builder.append(" order by posts.date desc ");
            }
            if(limit!=null){
                builder.append(" limit ");
                builder.append(limit);
            }
            builder.append(";");
            final String getPostsList = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPostsList);
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.setLength(0);
            resultBuilder.append("[ ");
            while(resultSet.next()) {
                Integer postId = resultSet.getInt("posts.id");
                Integer threadId = resultSet.getInt("posts.thread_id");
                String message = resultSet.getString("posts.message");
                String date = resultSet.getString("posts.date").substring(0,resultSet.getString("posts.date").length()-2);
                Integer userId = resultSet.getInt("posts.user_id");
                Integer forumId = resultSet.getInt("posts.forum_id");
                Integer parentId = resultSet.getInt("posts.parent_id");
                Boolean isSpam = resultSet.getBoolean("posts.isSpam");
                Boolean isApproved = resultSet.getBoolean("posts.isApproved");
                Boolean isHighlighted = resultSet.getBoolean("posts.isHighlighted");
                Boolean isEdited = resultSet.getBoolean("posts.isEdited");
                Boolean isDeleted = resultSet.getBoolean("posts.isDeleted");
                Integer likes = resultSet.getInt("posts.likes");
                Integer dislikes = resultSet.getInt("posts.dislikes");
                Integer points = resultSet.getInt("posts.points");
                String userEmail = resultSet.getString("users.email");
                if(parentId==0) {
                    parentId = null;
                }

                Post tempPost = new Post(postId, threadId, message, date, userId, forumId, parentId, isSpam,
                        isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);
                tempPost.setUserEmail(userEmail);


                String forumName = resultSet.getString("forums.name");
                String forumShortName = resultSet.getString("forums.short_name");
                Integer forumUserId = resultSet.getInt("forums.user_id");
                tempPost.setForumShortName(forumShortName);
                String forumUserEmail = "";

                final String getUserEmail = "Select email from users where id = " + forumUserId + ";";
                ResultSet localResultSet = connection.createStatement().executeQuery(getUserEmail);
                while(localResultSet.next()) {
                    forumUserEmail = localResultSet.getString(1);
                }
                Forum tempForum = new Forum(forumId, forumName, forumShortName, forumUserEmail);

                String threadDate = resultSet.getString("threads.date").substring(0,resultSet.getString("threads.date").length()-2);
                Integer threadDislikes = resultSet.getInt("threads.dislikes");
                String threadForum = forumShortName;
                Boolean threadIsClosed = resultSet.getBoolean("threads.isClosed");
                Boolean threadsIsDeleted = resultSet.getBoolean("threads.isDeleted");
                Integer threadLikes = resultSet.getInt("threads.likes");
                String threadMessage = resultSet.getString("threads.message");
                Integer threadPoints = resultSet.getInt("threads.points");
                Integer threadPosts = -1;
                String threadSlug = resultSet.getString("threads.slug");
                String threadTitle = resultSet.getString("threads.title");
                String threadUserEmail = "";
                Integer threadUserId = resultSet.getInt("threads.user_id");

                final String getThreadUserEmail = "Select email from users where id = " + threadUserId + ";";
                localResultSet = connection.createStatement().executeQuery(getThreadUserEmail);
                while(localResultSet.next()) {
                    threadUserEmail = localResultSet.getString(1);
                }
                final String getThreadPostsCount = "Select count(*) from posts where thread_id = " + threadId + ";";
                localResultSet = connection.createStatement().executeQuery(getThreadPostsCount);
                while(localResultSet.next()) {
                    threadPosts = localResultSet.getInt(1);
                }

                ForumThread tempThread = new ForumThread(threadId,threadTitle, threadIsClosed, threadsIsDeleted, threadDate,
                        threadMessage, threadSlug, threadLikes, threadDislikes, threadPoints, threadPosts,
                        threadUserEmail, threadForum);


                String userAbout = resultSet.getString("users.about");
                //String userEmail
                //String userId
                Boolean userIsAnonymos = resultSet.getBoolean("users.isAnonymos");
                String userName = resultSet.getString("users.name");
                String userUserName = resultSet.getString("users.username");
                UserProfile user = new UserProfile(userAbout, userEmail, userId, userIsAnonymos, userName, userUserName);

                builder.setLength(0);
                builder.append("[");
                final String getFollowers = "Select follower_id from followers where following_id= '" + user.getId() + "';";
                ResultSet userResultSet = connection.createStatement().executeQuery(getFollowers);
                while (userResultSet.next()) {
                    final String getFollowerEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                    while (innerResultSet.next()) {
                        builder.append(innerResultSet.getString(1));
                    }
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfFollower(builder.toString());

                builder.setLength(0);
                builder.append("[");
                final String getFollowing = "Select following_id from followers where follower_id= "
                        + user.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getFollowing);
                while (userResultSet.next()) {
                    final String getFollowingEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                    while (innerResultSet.next()) {
                        builder.append(innerResultSet.getString(1));
                    }
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfFollowing(builder.toString());

                builder.setLength(0);
                builder.append("[");
                final String getSubscription = "Select thread_id from subscribe where user_id= " + user.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getSubscription);
                while (userResultSet.next()) {
                    builder.append(userResultSet.getInt(1));
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfSubscriptions(builder.toString());
                //теперь у нас есть вся нужная информация
                tempPost.setUserInfo(user.toJSONDetails());
                tempPost.setForumInfo(tempForum.toJSON());
                tempPost.setThreadInfo(tempThread.toJSONDetails());
                resultBuilder.append(tempPost.toJSONwithInfo(forumInfoRequered, threadInfoRequered, userInfoRequered));
                if(resultSet.next()){
                    resultBuilder.append(", ");
                    resultSet.previous();
                }
            }
            resultBuilder.append("]");
            final String responseBody = resultBuilder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/forum/listThreads/", method= RequestMethod.GET)
    public ResponseEntity forumListThreads(@RequestParam(required = false) String[] related,
                                         @RequestParam String forum,
                                         @RequestParam(required = false) String since,
                                         @RequestParam(required = false) Integer limit,
                                         @RequestParam(required = false) String order) {
        Boolean userInfoRequered = false;
        Boolean forumInfoRequered = false;
        if(related!=null)
        for(Integer i = 0; i < related.length;++i) {
            if(related[i].equals("user")){ userInfoRequered = true; }
            if(related[i].equals("forum")){ forumInfoRequered = true; }
        }

        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            StringBuilder builder = new StringBuilder();

            builder.append("Select * from (threads left join users on threads.user_id = users.id) "
                     + "left join forums on threads.forum_id = forums.id where forums.short_name =\"");
            builder.append(forum);
            builder.append("\"");
            if(since!=null){
                builder.append("and threads.date > \"");
                builder.append(since);
                builder.append("\" ");
            }
            if(order!= null){
                builder.append(" order by threads.date ");
                builder.append(order);
            } else {
                builder.append(" order by threads.date desc ");
            }
            if(limit!=null){
                builder.append(" limit ");
                builder.append(limit);
            }
            builder.append(";");
            final String getThreadsList = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getThreadsList);
            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.setLength(0);
            resultBuilder.append("[ ");
            while(resultSet.next()) {
                Integer threadId = resultSet.getInt("threads.id");

                Integer userId = resultSet.getInt("threads.user_id");
                Integer forumId = resultSet.getInt("threads.forum_id");

                String userEmail = resultSet.getString("users.email");


                String forumName = resultSet.getString("forums.name");
                String forumShortName = resultSet.getString("forums.short_name");
                Integer forumUserId = resultSet.getInt("forums.user_id");
                String forumUserEmail = "";

                final String getUserEmail = "Select email from users where id = " + forumUserId + ";";
                ResultSet localResultSet = connection.createStatement().executeQuery(getUserEmail);
                while(localResultSet.next()) {
                    forumUserEmail = localResultSet.getString(1);
                }
                Forum tempForum = new Forum(forumId, forumName, forumShortName, forumUserEmail);

                String threadDate = resultSet.getString("threads.date").substring(0,
                        resultSet.getString("threads.date").length()-2);
                Integer threadDislikes = resultSet.getInt("threads.dislikes");
                String threadForum = forumShortName;
                Boolean threadIsClosed = resultSet.getBoolean("threads.isClosed");
                Boolean threadsIsDeleted = resultSet.getBoolean("threads.isDeleted");
                Integer threadLikes = resultSet.getInt("threads.likes");
                String threadMessage = resultSet.getString("threads.message");
                Integer threadPoints = resultSet.getInt("threads.points");
                Integer threadPosts = -1;
                String threadSlug = resultSet.getString("threads.slug");
                String threadTitle = resultSet.getString("threads.title");
                String threadUserEmail = "";
                Integer threadUserId = resultSet.getInt("threads.user_id");

                final String getThreadUserEmail = "Select email from users where id = " + threadUserId + ";";
                localResultSet = connection.createStatement().executeQuery(getThreadUserEmail);
                while(localResultSet.next()) {
                    threadUserEmail = localResultSet.getString(1);
                }
                final String getThreadPostsCount = "Select count(*) from posts where thread_id = " + threadId + ";";
                localResultSet = connection.createStatement().executeQuery(getThreadPostsCount);
                while(localResultSet.next()) {
                    threadPosts = localResultSet.getInt(1);
                }

                ForumThread tempThread = new ForumThread(threadId,threadTitle, threadIsClosed, threadsIsDeleted, threadDate,
                        threadMessage, threadSlug, threadLikes, threadDislikes, threadPoints, threadPosts,
                        threadUserEmail, threadForum);
                tempThread.setUserEmail(userEmail);

                String userAbout = resultSet.getString("users.about");
                //String userEmail
                //String userId
                Boolean userIsAnonymos = resultSet.getBoolean("users.isAnonymos");
                String userName = resultSet.getString("users.name");
                String userUserName = resultSet.getString("users.username");
                UserProfile user = new UserProfile(userAbout, userEmail, userId, userIsAnonymos, userName, userUserName);

                builder.setLength(0);
                builder.append("[");
                final String getFollowers = "Select follower_id from followers where following_id= '" + user.getId() + "';";
                ResultSet userResultSet = connection.createStatement().executeQuery(getFollowers);
                while (userResultSet.next()) {
                    final String getFollowerEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                    while (innerResultSet.next()) {
                        builder.append("\"");
                        builder.append(innerResultSet.getString(1));
                        builder.append("\"");
                    }
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfFollower(builder.toString());

                builder.setLength(0);
                builder.append("[");
                final String getFollowing = "Select following_id from followers where follower_id= "
                        + user.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getFollowing);
                while (userResultSet.next()) {
                    final String getFollowingEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                    ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                    while (innerResultSet.next()) {
                        builder.append("\"");
                        builder.append(innerResultSet.getString(1));
                        builder.append("\"");
                    }
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfFollowing(builder.toString());

                builder.setLength(0);
                builder.append("[");
                final String getSubscription = "Select thread_id from subscribe where user_id= " + user.getId() + ";";
                userResultSet = connection.createStatement().executeQuery(getSubscription);
                while (userResultSet.next()) {
                    builder.append(userResultSet.getInt(1));
                    if (userResultSet.next()) {
                        builder.append(", ");
                        userResultSet.previous();
                    }
                }
                builder.append("]");
                user.setListOfSubscriptions(builder.toString());
                //теперь у нас есть вся нужная информация
                tempThread.setUserInfo(user.toJSONDetails());
                tempThread.setForumInfo(tempForum.toJSON());
                resultBuilder.append(tempThread.toJSONwithInfo(forumInfoRequered, userInfoRequered));
                if(resultSet.next()){
                    resultBuilder.append(", ");
                    resultSet.previous();
                }
            }
            resultBuilder.append("]");
            final String responseBody = resultBuilder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/forum/details/", method= RequestMethod.GET)
    public ResponseEntity forumDetails(@RequestParam(required = false) String[] related,
                                       @RequestParam String forum) {
        String short_name = forum;
        Boolean userInfoRequered = false;
        if(related!=null) {
            for (Integer i = 0; i < related.length; ++i) {
                if (related[i].equals("user")) {
                    userInfoRequered = true;
                }
            }
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from forums left join users on forums.user_id = users.id where forums.short_name =\"");
            builder.append(short_name);
            builder.append("\";");
            final String getForumDetails = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getForumDetails);
            Integer forumId = -1;
            String forumName = "";
            String forumShortName = "";
            String userEmail = "";
            String userAbout = "";
            Integer userId = -1;
            Boolean userIsAnonymos = false;
            String userName = "";
            String userUserName = "";
            String forumUserInfo = "";
            while(resultSet.next()){
                forumId = resultSet.getInt("forums.id");
                forumName = resultSet.getString("forums.name");
                forumShortName = resultSet.getString("forums.short_name");
                userEmail = resultSet.getString("users.email");
                if(userInfoRequered.equals(true)){
                    userAbout = resultSet.getString("users.about");
                    userId = resultSet.getInt("users.id");
                    userIsAnonymos = resultSet.getBoolean("users.isAnonymos");
                    userName = resultSet.getString("users.name");
                    userUserName = resultSet.getString("users.username");
                }
                if(userInfoRequered.equals(true)) {
                    UserProfile user = new UserProfile(userAbout, userEmail, userId, userIsAnonymos, userName, userUserName);
                    builder.setLength(0);
                    builder.append("[");
                    final String getFollowers = "Select follower_id from followers where following_id= '" + user.getId() + "';";
                    ResultSet userResultSet = connection.createStatement().executeQuery(getFollowers);
                    while (userResultSet.next()) {
                        final String getFollowerEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                        while (innerResultSet.next()) {
                            builder.append(innerResultSet.getString(1));
                        }
                        if (userResultSet.next()) {
                            builder.append(", ");
                            userResultSet.previous();
                        }
                    }
                    builder.append("]");
                    user.setListOfFollower(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getFollowing = "Select following_id from followers where follower_id= "
                            + user.getId() + ";";
                    userResultSet = connection.createStatement().executeQuery(getFollowing);
                    while (userResultSet.next()) {
                        final String getFollowingEmails = "Select email from users where id = " + userResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                        while (innerResultSet.next()) {
                            builder.append(innerResultSet.getString(1));
                        }
                        if (userResultSet.next()) {
                            builder.append(", ");
                            userResultSet.previous();
                        }
                    }
                    builder.append("]");
                    user.setListOfFollowing(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getSubscription = "Select thread_id from subscribe where user_id= " + user.getId() + ";";
                    userResultSet = connection.createStatement().executeQuery(getSubscription);
                    while (userResultSet.next()) {
                        builder.append(userResultSet.getInt(1));
                        if (userResultSet.next()) {
                            builder.append(", ");
                            userResultSet.previous();
                        }
                    }
                    builder.append("]");
                    user.setListOfSubscriptions(builder.toString());
                    forumUserInfo = user.toJSONDetails();
                }

            }
            Forum forumObj = new Forum(forumId,forumName, forumShortName, userEmail);
            forumObj.setUserInfo(forumUserInfo);

            connection.close();
            if(userInfoRequered.equals(true)) {
                SuccessResponse response = new SuccessResponse(0, forumObj.toJSONUserInfo());
                return ResponseEntity.ok(response.createJSONResponce());
            } else {
                SuccessResponse response = new SuccessResponse(0, forumObj.toJSON());
                return ResponseEntity.ok(response.createJSONResponce());
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");

    }

    }
