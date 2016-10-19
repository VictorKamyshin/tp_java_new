package ru.mail.park.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.ForumThread;
import ru.mail.park.model.Post;
import ru.mail.park.response.SuccessResponse;

import java.sql.*;

/**
 * Created by Solovyev on 06/09/16.
 */

@RestController
public class RegistrationController{
    /*private final SessionService sessionService;

    @Autowired
    public RegistrationController(SessionService sessionService) {
        this.sessionService = sessionService;
    }*/

    static final String URL = "jdbc:mysql://localhost:3306/dbtest?characterEncoding=UTF-8";
    static final String USERNAME = "root";
    static final String PASSWORD = "Dbrnjh1993";


    @RequestMapping(path ="db/api/create", method = RequestMethod.POST)
    public ResponseEntity create() {
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String postsDrop = "Drop table if exists posts;";
            PreparedStatement preparedStatementDropTablePosts = connection.prepareStatement(postsDrop);
            preparedStatementDropTablePosts.executeUpdate();
            final String subscribeDrop = " Drop table if exists subscribe;";
            PreparedStatement preparedStatementDropTableSubscribe = connection.prepareStatement(subscribeDrop);
            preparedStatementDropTableSubscribe.executeUpdate();
            final String threadsDrop = "Drop table if exists threads;";
            PreparedStatement preparedStatementDropTableThreads = connection.prepareStatement(threadsDrop);
            preparedStatementDropTableThreads.executeUpdate();
            final String forumsDrop = "Drop table if exists forums;";
            PreparedStatement preparedStatementDropTableForums = connection.prepareStatement(forumsDrop);
            preparedStatementDropTableForums.executeUpdate();
            final String followersDrop = "Drop table if exists followers;";
            PreparedStatement preparedStatementDropTableFollowers = connection.prepareStatement(followersDrop);
            preparedStatementDropTableFollowers.executeUpdate();
            final String usersDrop = "Drop table if exists users;";
            PreparedStatement preparedStatementDropTableUsers = connection.prepareStatement(usersDrop);
            preparedStatementDropTableUsers.executeUpdate();


            final String usersCreate = "CREATE TABLE `dbtest`.`users` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `isAnonymos` TINYINT(1) NOT NULL," +
                    "  `name` VARCHAR(45) CHARACTER SET 'utf8' NULL," +
                    "  `about` LONGTEXT CHARACTER SET 'utf8' NOT NULL," +
                    "  `email` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `username` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "UNIQUE key(email)," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableUsers = connection.prepareStatement(usersCreate);
            preparedStatementCreateTableUsers.executeUpdate();
            final String followCreate = "CREATE TABLE `dbtest`.`followers` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `follower_id` INT NOT NULL," +
                    "  `following_id` INT NULL," +
                    "  foreign key (`follower_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`following_id`) references `users`(`id`) on delete cascade," +
                    "  UNIQUE key(`follower_id`, `following_id`)," +
                    "  PRIMARY KEY (`id`)" +
                    "  );";
            PreparedStatement preparedStatementCreateTableFollow = connection.prepareStatement(followCreate);
            preparedStatementCreateTableFollow.executeUpdate();
            final String forumsCreate = "  CREATE TABLE `dbtest`.`forums` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `name` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `short_name` VARCHAR(60) CHARACTER SET 'utf8' NOT NULL," +
                    "  `user_id` INT NOT NULL," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`)," +
                    "  UNIQUE INDEX `name_UNIQUE` (`name` ASC)," +
                    "  UNIQUE INDEX `short_name_UNIQUE` (`short_name` ASC)" +
                    "  );";
            PreparedStatement preparedStatementCreateTableForums = connection.prepareStatement(forumsCreate);
            preparedStatementCreateTableForums.executeUpdate();
            final String threadsCreate = "CREATE TABLE `dbtest`.`threads` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `user_id` INT NOT NULL," +
                    "  `forum_id` INT NOT NULL," +
                    "  `title` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "  `isClosed` INT(1) NOT NULL," +
                    "  `isDeleted` INT(1) NOT NULL," +
                    "  `date` DATETIME NOT NULL," +
                    "  `message` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL," +
                    "  `slug` VARCHAR(45) CHARACTER SET 'utf8' NOT NULL," +
                    "  `likes` INT NOT NULL DEFAULT 0," +
                    "  `dislikes` INT NOT NULL DEFAULT 0," +
                    "  `points` INT NOT NULL DEFAULT 0," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`forum_id`) references `forums`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableThreads = connection.prepareStatement(threadsCreate);
            preparedStatementCreateTableThreads.executeUpdate();
            final String subscribeCreate = "CREATE TABLE `dbtest`.`subscribe` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `user_id` INT NOT NULL," +
                    "  `thread_id` INT NOT NULL," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`thread_id`) references `threads`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTableSubscribe = connection.prepareStatement(subscribeCreate);
            preparedStatementCreateTableSubscribe.executeUpdate();
            final String postsCreate = "  CREATE TABLE `dbtest`.`posts` (" +
                    "  `id` INT NOT NULL AUTO_INCREMENT," +
                    "  `thread_id` INT NOT NULL," +
                    "  `message` MEDIUMTEXT CHARACTER SET 'utf8' NOT NULL," +
                    "  `date` DATETIME NOT NULL," +
                    "  `user_id` INT NOT NULL," +
                    "  `forum_id` INT NOT NULL," +
                    "  `parent_id` INT NULL," +
                    "  `isApproved` TINYINT(1) NOT NULL," +
                    "  `isHighlighted` TINYINT(1) NOT NULL," +
                    "  `isEdited` TINYINT(1) NOT NULL," +
                    "  `isSpam` TINYINT(1) NOT NULL," +
                    "  `isDeleted` TINYINT(1) NOT NULL DEFAULT 0," +
                    "  `likes` INT NOT NULL DEFAULT 0," +
                    "  `dislikes` INT NOT NULL DEFAULT 0," +
                    "  `points` INT NOT NULL DEFAULT 0," +
                    "  `materialPath` char(45) NOT NULL DEFAULT 'a'," +
                    "  `reverseMaterialPath` char(45) NOT NULL DEFAULT 'Z'," +
                    "  foreign key (`user_id`) references `users`(`id`) on delete cascade," +
                    "  foreign key (`forum_id`) references `forums`(`id`) on delete cascade," +
                    "  foreign key (`thread_id`) references `threads`(`id`) on delete cascade," +
                    "  foreign key (`parent_id`) references `posts`(`id`) on delete cascade," +
                    "  PRIMARY KEY (`id`));";
            PreparedStatement preparedStatementCreateTablePosts = connection.prepareStatement(postsCreate);
            preparedStatementCreateTablePosts.executeUpdate();

            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    SuccessResponse response = new SuccessResponse(0,"\"OK\"");
        return ResponseEntity.ok(response.createJSONResponce());

    }

    @RequestMapping(path = "db/api/clear", method = RequestMethod.POST)
    public ResponseEntity clear() {
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String postsDrop = "Delete from posts;";
            PreparedStatement preparedStatementDropTablePosts = connection.prepareStatement(postsDrop);
            preparedStatementDropTablePosts.executeUpdate();
            final String subscribeDrop = "Delete from subscribe;";
            PreparedStatement preparedStatementDropTableSubscribe = connection.prepareStatement(subscribeDrop);
            preparedStatementDropTableSubscribe.executeUpdate();
            final String threadsDrop = "Delete from threads;";
            PreparedStatement preparedStatementDropTableThreads = connection.prepareStatement(threadsDrop);
            preparedStatementDropTableThreads.executeUpdate();
            final String forumsDrop = "Delete from forums;";
            PreparedStatement preparedStatementDropTableForums = connection.prepareStatement(forumsDrop);
            preparedStatementDropTableForums.executeUpdate();
            final String followersDrop = "Delete from followers;";
            PreparedStatement preparedStatementDropTableFollowers = connection.prepareStatement(followersDrop);
            preparedStatementDropTableFollowers.executeUpdate();
            final String usersDrop = "Delete from users;";
            PreparedStatement preparedStatementDropTableUsers = connection.prepareStatement(usersDrop);
            preparedStatementDropTableUsers.executeUpdate();
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        SuccessResponse response = new SuccessResponse(0,"\"OK\"");
        return ResponseEntity.ok(response.createJSONResponce());
    }

    @RequestMapping(path = "db/api/status/", method = RequestMethod.GET)
    public ResponseEntity status() {
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            String response = "{\"user\": ";
            final String usersCount = "Select count(*) from users;";
            ResultSet resultSet = connection.createStatement().executeQuery(usersCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"thread\": ";
            final String threadsCount = "Select count(*) from threads;";
            resultSet = connection.createStatement().executeQuery(threadsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"forum\": ";
            final String forumsCount = "Select count(*) from forums;";
            resultSet = connection.createStatement().executeQuery(forumsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + ", \"post\": ";
            final String postsCount = "Select count(*) from posts;";
            resultSet = connection.createStatement().executeQuery(postsCount);
            while(resultSet.next()) {
                response = response + resultSet.getInt(1);
            }
            response = response + " }";
            connection.close();
            SuccessResponse successResponse = new SuccessResponse(0,response);
            return ResponseEntity.ok(successResponse.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        SuccessResponse response = new SuccessResponse(0,"Something go wrong");
        return ResponseEntity.ok(response.createJSONResponce());
    }


    @RequestMapping(path = "/db/api/thread/details/", method = RequestMethod.GET)
    public ResponseEntity threadDetails(@RequestParam Integer thread,
                                        @RequestParam(required = false) String[] related) {
        Boolean userInfoRequered = false;
        Boolean forumInfoRequered = false;
        if(related!=null)
            for(Integer i = 0; i < related.length;++i) {
                if(related[i].equals("user")){ userInfoRequered = true; }
                if(related[i].equals("forum")){ forumInfoRequered = true; }
                if (!related[i].equals("user") && !related[i].equals("forum")) {
                    SuccessResponse response = new SuccessResponse(3,"\"SyntaxError\"");
                    return  ResponseEntity.ok(response.createJSONResponce());
                }
            }
        Integer id = thread;
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
                isClosed = resultSet.getBoolean("isClosed");
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
            builder.append("Select short_name from forums where id =");
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
            builder.append(" and isDeleted<>1;");
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



    @RequestMapping(path = "/db/api/post/create/", method = RequestMethod.POST)
    public ResponseEntity dbtest(@RequestBody CreatePostRequest body) {
        final String date = body.getDate();
        final Integer threadId = body.getThreadId();
        final String message = body.getMessage();
        final String userEmail = body.getUserEmail();
        final String forumShortName = body.getForumShortName();
        final Integer parentId = body.getParentId();
        Boolean isApproved = body.getApproved();
        if(isApproved==null){
            isApproved = false;
        }
        Boolean isHighlighted = body.getHoghlighted();
        if(isHighlighted==null){
            isHighlighted = false;
        }
        Boolean isEdited = body.getEdited();
        if(isEdited==null){
            isEdited = false;
        }
        Boolean isSpam = body.getSpam();
        if(isSpam==null){
            isSpam = false;
        }
        Boolean isDeleted = body.getDeleted();
        if(isDeleted==null){
            isDeleted = false;
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Post post = new Post(date, threadId, message, userEmail, forumShortName,
                    parentId, isApproved, isHighlighted, isEdited, isSpam, isDeleted);

            final String getUserId = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while (resultSet.next()) {
                post.setUserId(resultSet.getInt(1));
            }

            final String getForumId = "Select id from forums where short_name = '" + forumShortName + "';";
            resultSet = connection.createStatement().executeQuery(getForumId);
            while (resultSet.next()) {
                post.setForumId(resultSet.getInt(1));
            }

            String getNeighbors = "";
            if (parentId == null) {
                getNeighbors = "Select count(*) from posts where thread_id = "
                        + threadId + " and parent_id is Null";
            } else {
                getNeighbors = "Select count(*) from posts where thread_id = "
                        + threadId + " and parent_id=" + parentId;
            }

            resultSet = connection.createStatement().executeQuery(getNeighbors);
            while (resultSet.next()) {
                post.setNeighbors(resultSet.getInt(1));
            }
            StringBuilder pathBuilder = new StringBuilder();
            char rightPath = ' ';
            char reversePath = ' ';
            if (parentId == null) {
                rightPath = ((char) (post.getNeighbors() + 65));
                reversePath = ((char) (122 - post.getNeighbors()));
            }else {
                rightPath = ((char) (post.getNeighbors() + 65));
                reversePath = ((char) (post.getNeighbors() + 65));
            }
            String getPath = "";
            if(parentId!=null){
                getPath = "Select materialPath, reverseMaterialPath from posts where thread_id = "
                        + threadId + " and id=" + parentId;
                resultSet = connection.createStatement().executeQuery(getPath);
                while(resultSet.next()) {
                    post.setMaterialPath(resultSet.getString(1)+rightPath);
                    post.setReverseMaterialPath(resultSet.getString(2)+reversePath);
                }
            } else {
                post.setMaterialPath(Character.toString(rightPath));
                post.setReverseMaterialPath(Character.toString(reversePath));
            }


            final String createPost = post.insert();
            System.out.println(createPost);
            PreparedStatement preparedStatementCreatePost = connection.prepareStatement(createPost);
            preparedStatementCreatePost.executeUpdate();

            StringBuilder builder = new StringBuilder();
            builder.append("Select id from posts where date = '");
            builder.append(date);
            builder.append("' and message = '");
            builder.append(message);
            builder.append("' and user_id = '");
            builder.append(post.getUserId());
            builder.append("'");
            final String getPostId = builder.toString();//"Select id from threads where title = '" + title + "' and;"; //title - не уникальный
            resultSet = connection.createStatement().executeQuery(getPostId);
            while(resultSet.next()) {
                post.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,post.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/vote/", method = RequestMethod.POST)
    public ResponseEntity postVote(@RequestBody VotePostRequest body) {
        Integer postId = body.getPostId();
        Integer vote = body.getVote(); //здесь надо разворачивать, если вот больше +-1
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(postId);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer id = postId;
            Integer threadId = -1;
            String message = "";
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                threadId = resultSet.getInt("thread_id");
                message = resultSet.getString("message");
                date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }
            if(parentId.equals(0)){
                parentId = null;
            }

            Post post = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                        isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                post.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                post.setForumShortName(resultSet.getString(1));
            }

            if(vote>0) {
                builder.setLength(0);
                builder.append("Update posts set likes = ");
                builder.append(post.like());
                builder.append(", points = ");
                builder.append(post.getLikes() - post.getDislikes());
                builder.append(" where id = ");
                builder.append(id);
                String setLikes = builder.toString();
                PreparedStatement preparedStatementSetLikes = connection.prepareStatement(setLikes);
                System.out.println(setLikes);
                preparedStatementSetLikes.executeUpdate();
            } else {
                builder.setLength(0);
                builder.append("Update posts set dislikes = ");
                builder.append(post.dislike());
                builder.append(", points = ");
                builder.append(post.getLikes() - post.getDislikes());
                builder.append(" where id = ");
                builder.append(id);
                String setDislikes = builder.toString();
                PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(setDislikes);
                preparedStatementSetDislikes.executeUpdate();
            }

            responseBody = post.toJSONDetails();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/details/", method = RequestMethod.GET)
    public ResponseEntity postDetails(@RequestParam Integer post ) {
        Integer id = post;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(id);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer threadId = -1;
            String message = "";
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;
            if(resultSet.next()){
                resultSet.previous();
            } else {
                SuccessResponse response = new SuccessResponse(1,"\"NotFound\"");
                return  ResponseEntity.ok(response.createJSONResponce());
            }
            while(resultSet.next()) {
                threadId = resultSet.getInt("thread_id");
                message = resultSet.getString("message");
                date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            if(parentId.equals(0)){
                parentId = null;
            }

            Post tempPost = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                    isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                tempPost.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                tempPost.setForumShortName(resultSet.getString(1));
            }

            responseBody = tempPost.toJSONDetails();
            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/list/", method = RequestMethod.GET)
    public ResponseEntity postList(@RequestParam(required = false) String forum,
                                   @RequestParam(required = false) Integer thread,
                                   @RequestParam(required = false) String since,
                                   @RequestParam(required = false) Integer limit,
                                   @RequestParam(required = false) String order) {
        if(thread==null && forum == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("parameters is missing");
        }
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            String forumShortName = "";
            if(forum!=null){
                forumShortName = forum;
            } else {
                final String getForumShortName = "Select short_name from threads left join forums " +
                        "on (forums.id = threads.forum_id) WHERE threads.id = " + thread + ";";
                ResultSet resultSet = connection.createStatement().executeQuery(getForumShortName);
                while(resultSet.next()) {
                    forumShortName = resultSet.getString(1);
                }
            }
            StringBuilder builder = new StringBuilder();
            if(forum!=null) {
                final String getForumId = "Select id from forums where short_name = '" + forum + "';";
                ResultSet resultSet = connection.createStatement().executeQuery(getForumId);
                Integer forumId = -1;
                while(resultSet.next()) {
                    forumId=resultSet.getInt(1);
                } //получили айди форума
                builder.append("Select * from posts where forum_id = ");
                builder.append(forumId);
            } else {
                builder.append("Select * from posts where thread_id = ");
                builder.append(thread);
            }
            if(since!=null) {
                builder.append(" and date > \"");
                builder.append(since);
                builder.append("\" ");
            }
            builder.append("order by date ");
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
            String getPostsList = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPostsList);
            builder.setLength(0);
            builder.append("[ ");
            while(resultSet.next()) {
                Integer postId = resultSet.getInt("id");
                Integer threadId = resultSet.getInt("thread_id");
                String message = resultSet.getString("message");
                String date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                Integer userId = resultSet.getInt("user_id");
                Integer forumId = resultSet.getInt("forum_id");
                Integer parentId = resultSet.getInt("parent_id");
                Boolean isSpam = resultSet.getBoolean("isSpam");
                Boolean isApproved = resultSet.getBoolean("isApproved");
                Boolean isHighlighted = resultSet.getBoolean("isHighlighted");
                Boolean isEdited = resultSet.getBoolean("isEdited");
                Boolean isDeleted = resultSet.getBoolean("isDeleted");
                Integer likes = resultSet.getInt("likes");
                Integer dislikes = resultSet.getInt("dislikes");
                Integer points = resultSet.getInt("points");

                if(parentId.equals(0)){
                    parentId = null;
                }

                Post tempPost = new Post(postId, threadId, message, date, userId, forumId, parentId, isSpam,
                        isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

                final String getUserEmail = "Select email from users where id = '" + userId + "';";
                ResultSet localResultSet = connection.createStatement().executeQuery(getUserEmail);
                while(localResultSet.next()) {
                    tempPost.setUserEmail(localResultSet.getString("email"));
                }
                tempPost.setForumShortName(forumShortName);
                builder.append(tempPost.toJSONDetails());
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

    @RequestMapping(path = "/db/api/post/remove/", method = RequestMethod.POST)
    public ResponseEntity postRemove(@RequestBody RemoveRestorePostRequest body) {
        Integer id = body.getPostId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update posts set isDeleted = ");
            builder.append(1);
            builder.append("  where id = ");
            builder.append(id);
            String removePost = builder.toString();
            PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(removePost);
            preparedStatementSetDislikes.executeUpdate();

            builder.setLength(0);
            builder.append("{\"post\":");
            builder.append(id);
            builder.append("}");
            String responseBody = builder.toString();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/restore/", method = RequestMethod.POST)
    public ResponseEntity postRestore(@RequestBody RemoveRestorePostRequest body) {
        Integer id = body.getPostId();
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Update posts set isDeleted = ");
            builder.append(false);
            builder.append("  where id = ");
            builder.append(id);

            String removePost = builder.toString();
            PreparedStatement preparedStatementSetDislikes = connection.prepareStatement(removePost);
            preparedStatementSetDislikes.executeUpdate();

            builder.setLength(0);
            builder.append("{\"post\":");
            builder.append(id);
            builder.append("}");
            String responseBody = builder.toString();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/post/update/", method = RequestMethod.POST)
    public ResponseEntity postUpdate(@RequestBody UpdatePostRequest body) {
        Integer postId = body.getPostId();
        String message = body.getMessage(); //здесь надо разворачивать, если вот больше +-1
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            StringBuilder builder = new StringBuilder();
            builder.append("Select * from posts where id = ");
            builder.append(postId);
            String getPost = builder.toString();
            ResultSet resultSet = connection.createStatement().executeQuery(getPost);
            String responseBody = "";
            Integer id = -1;
            Integer threadId = -1;
            String date = "";
            Integer userId = -1;
            Integer forumId = -1;
            Integer parentId = -1;
            Boolean isSpam = false;
            Boolean isApproved = false;
            Boolean isHighlighted = false;
            Boolean isEdited = false;
            Boolean isDeleted = false;
            Integer likes = 0;
            Integer dislikes = 0;
            Integer points = 0;

            while(resultSet.next()) {
                id = resultSet.getInt("id");
                threadId = resultSet.getInt("thread_id");
                date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
                userId = resultSet.getInt("user_id");
                forumId = resultSet.getInt("forum_id");
                parentId = resultSet.getInt("parent_id");
                isSpam = resultSet.getBoolean("isSpam");
                isApproved = resultSet.getBoolean("isApproved");
                isHighlighted = resultSet.getBoolean("isHighlighted");
                isEdited = resultSet.getBoolean("isEdited");
                isDeleted = resultSet.getBoolean("isDeleted");
                likes = resultSet.getInt("likes");
                dislikes = resultSet.getInt("dislikes");
                points = resultSet.getInt("points");
            }

            if(parentId.equals(0)){
                parentId = null;
            }

            Post post = new Post(id, threadId, message, date, userId, forumId, parentId, isSpam,
                    isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

            final String getUserEmail = "Select email from users where id = '" + userId + "';";
            resultSet = connection.createStatement().executeQuery(getUserEmail);
            while(resultSet.next()) {
                post.setUserEmail(resultSet.getString(1));
            }

            final String getForumShortName = "Select short_name from forums where id = '" + forumId + "';";
            resultSet = connection.createStatement().executeQuery(getForumShortName);
            while(resultSet.next()) {
                post.setForumShortName(resultSet.getString(1));
            }

            builder.setLength(0);
            builder.append("Update posts set message = \"");
            builder.append(message);
            builder.append("\", points = ");
            builder.append(post.getPoints());
            builder.append(" where id = ");
            builder.append(id);
            String setMessage = builder.toString();
            PreparedStatement preparedStatementSetMessage = connection.prepareStatement(setMessage);
            preparedStatementSetMessage.executeUpdate();

            responseBody = post.toJSONDetails();

            connection.close();
            SuccessResponse response = new SuccessResponse(0,responseBody);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    private static final class UpdatePostRequest{
        private String message;
        private Integer postId;

        @JsonCreator
        private UpdatePostRequest(@JsonProperty("message") String message,
                                @JsonProperty("post") Integer postId) {
            this.message = message;
            this.postId = postId;
        }

        public String getMessage() {
            return message;
        }

        public Integer getPostId() {
            return postId;
        }
    }

    private static final class RemoveRestorePostRequest{
        private Integer postId;

        @JsonCreator
        private RemoveRestorePostRequest(@JsonProperty("post") Integer postId) {
            this.postId = postId;
        }

        public Integer getPostId() {
            return postId;
        }
    }

    private static final class VotePostRequest{
        private Integer vote;
        private Integer postId;

        @JsonCreator
        private VotePostRequest(@JsonProperty("vote") Integer vote,
                                   @JsonProperty("post") Integer postId) {
            this.vote = vote;
            this.postId = postId;
        }

        public Integer getVote() {
            return vote;
        }

        public Integer getPostId() {
            return postId;
        }
    }


    private static final class CreatePostRequest {
        private String date;
        private Integer threadId;
        private String message;
        private String userEmail;
        private String forumShortName; //обязательные параметры
        private Integer parentId;
        private Boolean isApproved;
        private Boolean isHoghlighted;
        private Boolean isEdited;
        private Boolean isSpam;
        private Boolean isDeleted;

        @JsonCreator
        private CreatePostRequest(@JsonProperty("date") String date,
                                  @JsonProperty("thread") Integer threadId,
                                  @JsonProperty("message") String message,
                                   @JsonProperty("user") String userEmail,
                                   @JsonProperty("forum") String forumShortName,
                                  @JsonProperty("parent") Integer parentId,
                                  @JsonProperty("isApproved") Boolean isApproved,
                                  @JsonProperty("isHighlighted") Boolean isHighlighted,
                                  @JsonProperty("isEdited") Boolean isEdited,
                                  @JsonProperty("isSpam") Boolean isSpam,
                                  @JsonProperty("isDeleted") Boolean isDeleted) {
            this.date = date;
            this.threadId = threadId;
            this.message = message;
            this.userEmail = userEmail;
            this.forumShortName = forumShortName;
            this.parentId = parentId;
            this.isApproved = isApproved;
            this.isHoghlighted = isHighlighted;
            this.isEdited = isEdited;
            this.isSpam = isSpam;
            this.isDeleted = isDeleted;
        }

        public String getDate() {
            return date;
        }

        public Integer getThreadId() {
            return threadId;
        }

        public String getMessage() {
            return message;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getForumShortName() {
            return forumShortName;
        }

        public Integer getParentId() {
            return parentId;
        }

        public Boolean getApproved() {
            return isApproved;
        }

        public Boolean getHoghlighted() {
            return isHoghlighted;
        }

        public Boolean getEdited() {
            return isEdited;
        }

        public Boolean getSpam() {
            return isSpam;
        }

        public Boolean getDeleted() {
            return isDeleted;
        }
    }

}
