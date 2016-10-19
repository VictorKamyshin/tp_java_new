package ru.mail.park.main;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.ForumThread;
import ru.mail.park.model.Post;
import ru.mail.park.model.Subcribe;
import ru.mail.park.request.thread.*;
import ru.mail.park.response.SuccessResponse;

import java.sql.*;

/**
 * Created by victor on 17.10.16.
 */
@RestController
public class ThreadController {
    static final String URL = "jdbc:mysql://localhost:3306/dbtest?characterEncoding=UTF-8";
    static final String USERNAME = "root";
    static final String PASSWORD = "Dbrnjh1993";

    @RequestMapping(path = "/db/api/thread/create/", method = RequestMethod.POST)
    public ResponseEntity threadCreate(@RequestBody CreateThread body) {
        final String date = body.getDate();
        final String forumName = body.getForum_name();
        final Boolean isClosed = body.isClosed();
        Boolean isDeleted = body.isDeleted();
        if(isDeleted==null){
            isDeleted = false;
        }
        final String message = body.getMessage();
        final String slug = body.getSlug();
        final String title = body.getTitle();
        final String userEmail = body.getUser_email();

        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            ForumThread forumThread = new ForumThread(date, isClosed, isDeleted, message,
                    slug, title, userEmail, forumName);
            final String getUserId = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                forumThread.setUser_id(resultSet.getInt(1));
            }

            final String getForumId = "Select id from forums where short_name = '" + forumName + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while(resultSet.next()) {
                forumThread.setForum_id(resultSet.getInt(1));
            }

            final String createThread = forumThread.insert();
            PreparedStatement preparedStatementCreateThread = connection.prepareStatement(createThread);
            preparedStatementCreateThread.executeUpdate();

            StringBuilder builder = new StringBuilder();
            builder.append("Select id from threads where title = '");
            builder.append(title);
            builder.append("' and message = '");
            builder.append(message);
            builder.append("' and slug = '");
            builder.append(slug);
            builder.append("' and date = '");
            builder.append(date);
            builder.append("'");
            final String getThreadId = builder.toString();//"Select id from threads where title = '" + title + "' and;"; //title - не уникальный
            resultSet = connection.createStatement().executeQuery(getThreadId);
            while(resultSet.next()) {
                forumThread.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSON());
            System.out.println(forumThread.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/subscribe/", method = RequestMethod.POST)
    public ResponseEntity threadSubscribe(@RequestBody SubscribeThread body) {
        Integer threadId = body.getThreadId();
        String userEmail = body.getUserEmail();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            Subcribe subcribe = new Subcribe(threadId, userEmail);
            builder.setLength(0);
            builder.append("Select id from users where email = \"");
            builder.append(userEmail);
            builder.append("\";");
            final String getUserId = builder.toString();
            System.out.println(getUserId);
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                subcribe.setUserId(resultSet.getInt(1));
            }

            final String createSubscription = subcribe.insert();
            PreparedStatement preparedStatementCreateSubscription = connection.prepareStatement(createSubscription);
            preparedStatementCreateSubscription.executeUpdate();


            connection.close();
            SuccessResponse response = new SuccessResponse(0, subcribe.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/unsubscribe/", method = RequestMethod.POST)
    public ResponseEntity threadUnsubscribe(@RequestBody SubscribeThread body) {
        Integer threadId = body.getThreadId();
        String userEmail = body.getUserEmail();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            Subcribe subcribe = new Subcribe(threadId, userEmail);
            builder.setLength(0);
            builder.append("Select id from users where email = \"");
            builder.append(userEmail);
            builder.append("\";");
            final String getUserId = builder.toString();
            System.out.println(getUserId);
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                subcribe.setUserId(resultSet.getInt(1));
            }

            final String deleteSubscription = subcribe.delete();
            System.out.println(deleteSubscription);
            PreparedStatement preparedStatementDeleteSubscription = connection.prepareStatement(deleteSubscription);
            preparedStatementDeleteSubscription.executeUpdate();


            connection.close();
            SuccessResponse response = new SuccessResponse(0, subcribe.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/update/", method = RequestMethod.POST)
    public ResponseEntity threadUpdate(@RequestBody UpdateThread body) {
        String message = body.getMessage();
        String slug = body.getSlug();
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update threads set message = \"");
            builder.append(message);
            builder.append("\", slug = \"");
            builder.append(slug);
            builder.append("\"  where id = ");
            builder.append(id);
            String UpdateThread = builder.toString();
            PreparedStatement preparedStatementUpdateThread = connection.prepareStatement(UpdateThread);
            preparedStatementUpdateThread.executeUpdate();

            builder.setLength(0);
            builder.append("Select * from threads where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            Integer userId = -1;
            Integer forumId = -1;
            String title = "";
            Boolean isClosed = true;
            Boolean isDeleted = false;
            String date = "";
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                title = resultSet.getString("title");

                isDeleted = resultSet.getBoolean("isDeleted");
                date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);


                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            ForumThread forumThread = new ForumThread(id, userId, forumId, title,
                    isClosed, isDeleted, date, message, slug, likes, dislikes, points);

            builder.setLength(0);
            builder.append("Select email from users where id = ");
            builder.append(userId);
            builder.append(";");
            final String getUserEmail = builder.toString();
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                forumThread.setUserEmail(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select name from forums where id =");
            builder.append(forumId);
            builder.append(";");
            final String getForumName = builder.toString();
            resultSet = connection.createStatement().executeQuery(getForumName);
            while(resultSet.next()) {
                forumThread.setForumName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select count(*) from posts where thread_id =");
            builder.append(id);
            builder.append(";");
            final String getPostsCount = builder.toString();
            resultSet = connection.createStatement().executeQuery(getPostsCount);
            while(resultSet.next()) {
                forumThread.setPosts(resultSet.getInt(1));
            }

            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/close/", method = RequestMethod.POST)
    public ResponseEntity threadClose(@RequestBody StatusChange body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isClosed = ");
            builder.append(true);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementCloseThread = connection.prepareStatement(closeThread);
            preparedStatementCloseThread.executeUpdate();

            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/open/", method = RequestMethod.POST)
    public ResponseEntity threadOpen(@RequestBody StatusChange body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isClosed = ");
            builder.append(false);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementOpenThread = connection.prepareStatement(closeThread);
            preparedStatementOpenThread.executeUpdate();

            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/listPosts/", method = RequestMethod.GET)
    public ResponseEntity postListPosts(@RequestParam String thread,
                                   @RequestParam(required = false) String since,
                                   @RequestParam(required = false) Integer limit,
                                   @RequestParam(required = false) String order,
                                        @RequestParam(required = false) String sort) {
        if(sort == null){
            sort = "flat";
        }
        if(order == null ){
            order = "desc";
        }
        char lim = '1';
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            if(sort.equals("parent_tree")){
                builder.append("Select ");
                if (order.equals("asc") && limit!=null){
                    builder.append("materialPath");
                }
                if(order.equals("desc") && limit!=null){
                    builder.append("reverseMaterialPath");
                }
                builder.append(" from posts where thread_id = ");
                builder.append(thread);
                builder.append(" and parent_id is null");
                builder.append(" order by ");
                if(order.equals("asc")){
                    builder.append("materialPath");
                    builder.append(" asc");
                } else {
                    builder.append("reversematerialPath");
                    builder.append(" desc");
                }
                builder.append(" limit 1");
                String getLimit = builder.toString();
                ResultSet resultSet = connection.createStatement().executeQuery(getLimit);

                while(resultSet.next()) {
                    lim = resultSet.getString(1).charAt(0);
                }

            }
            builder.setLength(0);
            builder.append("Select posts.date, posts.dislikes, forums.short_name, " +
                    "posts.id, posts.isApproved, posts.isDeleted, posts.isEdited, " +
                    "posts.isHighlighted, posts.isSpam, posts.likes, " +
                    "posts.message, posts.parent_id, posts.points, posts.thread_id, users.email " +
                    "from (posts left join forums on posts.forum_id = forums.id) left join " +
                    "users on posts.user_id = users.id where posts.thread_id =");
            builder.append(thread);
            if(since!=null) {
                builder.append(" and posts.date > \"");
                builder.append(since);
                builder.append("\" ");
            }
            if(sort.equals("parent_tree")&&lim!='1'){
                if(order.equals("asc")) {
                    builder.append("and materialPath< \'");
                    builder.append((char)(lim + limit));
                } else {
                    builder.append("and reverseMaterialPath> \'");
                    builder.append((char)(lim - limit));
                }
                builder.append("\'");
            }
            builder.append(" order by ");
            if(sort.equals("flat")) {
                builder.append("date ");
            } else {
                if(order.equals( "asc")){
                    builder.append("materialPath ");
                } else {
                    builder.append("reverseMaterialPath ");
                }
                builder.append("asc ");
            }
            if(order!=null&&sort.equals("flat")) {
                builder.append(order);
            }
            if(limit!=null && !sort.equals("parent_tree")) {
                builder.append(" limit ");
                builder.append(limit);
            }
            builder.append(";");
            String getPostsList = builder.toString();
            System.out.println(getPostsList);
            ResultSet resultSet = connection.createStatement().executeQuery(getPostsList);
            builder.setLength(0);
            builder.append("[");
            while(resultSet.next()){
                String date = resultSet.getString("posts.date").substring(0,resultSet.getString("posts.date").length()-2);
                Integer dislikes = resultSet.getInt("posts.dislikes");
                String forumShortName = resultSet.getString("forums.short_name");
                Integer postId = resultSet.getInt("posts.id");
                Boolean isApproved = resultSet.getBoolean("posts.isApproved");
                Boolean isDeleted = resultSet.getBoolean("posts.isDeleted");
                Boolean isEdited = resultSet.getBoolean("posts.isEdited");
                Boolean isHighlighted = resultSet.getBoolean("posts.isHighlighted");
                Boolean isSpam = resultSet.getBoolean("posts.isSpam");
                Integer likes = resultSet.getInt("posts.likes");
                String message = resultSet.getString("posts.message");
                Integer parentId = resultSet.getInt("posts.parent_id");
                Integer points = resultSet.getInt("posts.points");
                Integer threadId = resultSet.getInt("posts.thread_id");
                String userEmail = resultSet.getString("users.email");

                if(parentId==0) {
                    parentId=null;
                }
                Post post = new Post(postId, threadId, message, date, -1,-1, parentId, isSpam,
                        isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

                post.setForumShortName(forumShortName);
                post.setUserEmail(userEmail);
                builder.append(post.toJSONDetails());

                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/remove/", method = RequestMethod.POST)
    public ResponseEntity threadRemove(@RequestBody StatusChange body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isDeleted = ");
            builder.append(true);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementRemoveThread = connection.prepareStatement(closeThread);
            preparedStatementRemoveThread.executeUpdate();

            builder.setLength(0);
            builder.append("Update posts set isDeleted = ");
            builder.append(true);
            builder.append("  where thread_id = ");
            builder.append(id);
            String deleteThreadPosts = builder.toString();
            PreparedStatement preparedStatementRemoveThreadPosts = connection.prepareStatement(deleteThreadPosts);
            preparedStatementRemoveThreadPosts.executeUpdate();


            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/restore/", method = RequestMethod.POST)
    public ResponseEntity threadRestore(@RequestBody StatusChange body) {
        Integer id = body.getThreadId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();

            builder.setLength(0);
            builder.append("Update threads set isDeleted = ");
            builder.append(0);
            builder.append("  where id = ");
            builder.append(id);
            String closeThread = builder.toString();
            PreparedStatement preparedStatementRestoreThread = connection.prepareStatement(closeThread);
            preparedStatementRestoreThread.executeUpdate();

            builder.setLength(0);
            builder.append("Update posts set isDeleted = ");
            builder.append(0);
            builder.append("  where thread_id = ");
            builder.append(id);
            String restorePosts = builder.toString();
            PreparedStatement preparedStatementRestorePosts = connection.prepareStatement(restorePosts);
            preparedStatementRestorePosts.executeUpdate();


            connection.close();
            builder.setLength(0);
            builder.append("{ \"thread\": ");
            builder.append(id);
            builder.append(" }");
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/vote/", method = RequestMethod.POST)
    public ResponseEntity threadVore(@RequestBody VoteThread body) {
        Integer id = body.getThreadId();
        Integer vote = body.getVote();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from threads where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            Integer userId = -1;
            Integer forumId = -1;
            String title = "";
            Boolean isClosed = false;
            Boolean isDeleted = false;
            String date = "";
            String message = "";
            String slug = "";
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                title = resultSet.getString("title");

                isDeleted = resultSet.getBoolean("isDeleted");
                date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                message = resultSet.getString("message");
                slug = resultSet.getString("slug");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            ForumThread forumThread = new ForumThread(id, userId, forumId, title,
                    isClosed, isDeleted, date, message, slug, likes, dislikes, points);

            builder.setLength(0);
            builder.append("Select email from users where id = ");
            builder.append(userId);
            builder.append(";");
            final String getUserEmail = builder.toString();
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                forumThread.setUserEmail(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select name from forums where id =");
            builder.append(forumId);
            builder.append(";");
            final String getForumName = builder.toString();
            resultSet = connection.createStatement().executeQuery(getForumName);
            while(resultSet.next()) {
                forumThread.setForumName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Select count(*) from posts where thread_id =");
            builder.append(id);
            builder.append(";");
            final String getPostsCount = builder.toString();
            resultSet = connection.createStatement().executeQuery(getPostsCount);
            while(resultSet.next()) {
                forumThread.setPosts(resultSet.getInt(1));
            }

            if(vote>0) {
                builder.setLength(0);
                builder.append("Update threads set likes = ");
                builder.append(forumThread.like());
                builder.append(", points = ");
                builder.append(forumThread.getPoints());
                builder.append(" where id = ");
                builder.append(id);
                String setLikes = builder.toString();
                PreparedStatement preparedStatementSetLikes = connection.prepareStatement(setLikes);
                preparedStatementSetLikes.executeUpdate();
            } else {
                builder.setLength(0);
                builder.append("Update threads set dislikes = ");
                builder.append(forumThread.dislike());
                builder.append(", points = ");
                builder.append(forumThread.getPoints());
                builder.append(" where id = ");
                builder.append(id);
                String setDislikes = builder.toString();
                PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(setDislikes);
                preparedStatementSetDislikes.executeUpdate();
            }

            connection.close();
            SuccessResponse response = new SuccessResponse(0,forumThread.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/thread/list/", method = RequestMethod.GET)
    public ResponseEntity postList(@RequestParam(required = false) String forum,
                                   @RequestParam(required = false) String user,
                                   @RequestParam(required = false) String since,
                                   @RequestParam(required = false) Integer limit,
                                   @RequestParam(required = false) String order) {
        if(user==null && forum == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("parameters is missing");
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            //printeres
            StringBuilder builder = new StringBuilder();
            builder.append("Select threads.date, threads.dislikes, forums.short_name, " +
                    "threads.id, threads.isClosed, threads.isDeleted, threads.likes, threads.message, " +
                    "threads.points, threads.slug, threads.title, users.email from (threads left join forums " +
                    "on threads.forum_id = forums.id) left join users on threads.user_id = users.id where ");
            if(forum!=null) {
                builder.append("forums.short_name = \"");
                builder.append(forum);
                builder.append("\"");
            } else {
                builder.append("users.email= \"");
                builder.append(user);
                builder.append("\"");
            }

            if(since!=null) {
                builder.append(" and threads.date > \"");
                builder.append(since);
                builder.append("\" ");
            }
            builder.append("order by threads.date ");
            if(order!=null) {
                builder.append(order);
            } else {
                builder.append("desc");
            }
            if(limit!=null) {
                builder.append(" limit ");
                builder.append(limit);
            }
            builder.append(";");
            String getThreadsList = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getThreadsList);
            builder.setLength(0);
            builder.append("[ ");
            while(resultSet.next()){
                final String date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                final Integer dislikes = resultSet.getInt("dislikes");
                final String forumShortName = resultSet.getString("short_name");
                final Integer id = resultSet.getInt("id");
                final Boolean isClosed = resultSet.getBoolean("isClosed");
                final Boolean isDeleted = resultSet.getBoolean("isDeleted");
                final Integer likes = resultSet.getInt("likes");
                final String message = resultSet.getString("message");
                final Integer points = resultSet.getInt("points");
                final String slug = resultSet.getString("slug");
                final String title = resultSet.getString("title");
                final String email = resultSet.getString("email");

                Integer postsCount = -1;
                String getPostsCount = "Select count(*) from posts where thread_id = " + id + ';';
                ResultSet threadResultSet = connection.createStatement().executeQuery(getPostsCount);
                while(threadResultSet.next()){
                    postsCount = threadResultSet.getInt(1);
                }
                ForumThread tempThread = new ForumThread(id, title, isClosed, isDeleted, date, message,
                        slug, likes, dislikes, points, postsCount, email,forumShortName);
                builder.append(tempThread.toJSONDetails());
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            connection.close();
            final String responseBody = builder.toString();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }

            return  ResponseEntity.ok("OK");
    }


}
