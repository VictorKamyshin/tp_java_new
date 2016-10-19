package ru.mail.park.main;

/**
 * Created by victor on 17.10.16.
 */

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.model.Follow;
import ru.mail.park.model.Post;
import ru.mail.park.model.UserProfile;
import ru.mail.park.request.user.CreateUser;
import ru.mail.park.request.user.FollowUnfollowUser;
import ru.mail.park.request.user.UpdateUser;
import ru.mail.park.response.SuccessResponse;

import java.sql.*;


@RestController
public class UserController {

    static final String URL = "jdbc:mysql://localhost:3306/dbtest?characterEncoding=UTF-8";
    static final String USERNAME = "root";
    static final String PASSWORD = "Dbrnjh1993";

/*
    @RequestMapping(path = "/db/api/user/create/", method=RequestMethod.POST)
    public ResponseEntity dbtest(@RequestBody String body) {
        System.out.println(body);
        System.out.println("!");
        return  ResponseEntity.ok("OK");
    } */

    @RequestMapping(path = "/db/api/user/create/", method=RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody CreateUser body){
        final String username = body.getUsername();
        final String about = body.getAbout();
        Boolean isAnonymos = body.getAnonymos();
        final String name = body.getName();
        final String email = body.getEmail();
        UserProfile user = new UserProfile(isAnonymos, email, name, about, username);
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String createUser = user.incert("users");

            PreparedStatement preparedStatementCreateUser = connection.prepareStatement(createUser);
            preparedStatementCreateUser.executeUpdate();
            final String getUserId = "Select id from users where email = '" + email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            while(resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            connection.close();
            SuccessResponse response = new SuccessResponse(0,user.toJSON());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            //e.printStackTrace();
            if(e.getErrorCode()==1062){
                SuccessResponse response = new SuccessResponse(5,"\"AlreadyExists\"");
                return  ResponseEntity.ok(response.createJSONResponce());
            }
            System.out.println(e.getErrorCode());
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/details/", method= RequestMethod.GET)
    public ResponseEntity detailsUser(@RequestParam String user){
        String userEmail = user;
        String about = "";
        Integer id = -1;
        Boolean isAnonymos = true;
        String name = "";
        String username = "";
        Connection connection;
        StringBuilder builder = new StringBuilder();
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String getUser = "Select * from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUser);
            if(resultSet.next()){
                resultSet.previous();
            } else {
                SuccessResponse response = new SuccessResponse(1,"\"NotFound\"");
                return  ResponseEntity.ok(response.createJSONResponce());
            }
            while(resultSet.next()) {
                about = resultSet.getString("about");
                id = resultSet.getInt("id");
                isAnonymos = resultSet.getBoolean("isAnonymos");
                name = resultSet.getString("name");
                username = resultSet.getString("username");

            }
            UserProfile newUser = new UserProfile(about, userEmail, id, isAnonymos, name, username);

            builder.append("[");
            final String getFollowers = "Select follower_id from followers where following_id= '" + newUser.getId() + "';";
            resultSet = connection.createStatement().executeQuery(getFollowers);
            while(resultSet.next()) {
                final String getFollowerEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            newUser.setListOfFollower(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getFollowing = "Select following_id from followers where follower_id= "
                    + newUser.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getFollowing);
            while(resultSet.next()) {
                final String getFollowingEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            newUser.setListOfFollowing(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getSubscription = "Select thread_id from subscribe where user_id= " + newUser.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getSubscription);
            while(resultSet.next()) {
                builder.append(resultSet.getInt(1));
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            newUser.setListOfSubscriptions(builder.toString());
            //System.out.println(newUser.getId());
            builder.setLength(0);

            connection.close();
            SuccessResponse response = new SuccessResponse(0,newUser.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());

        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/listFollowers/", method=RequestMethod.GET)
    public ResponseEntity followersListUser(@RequestParam String user,
                                            @RequestParam(required = false) Integer since_id,
                                            @RequestParam(required = false) Integer limit,
                                            @RequestParam(required = false) String order){
        String userEmail = user;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String getUser = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUser);

            String result = "[";
            while(resultSet.next()) {
                Integer userId = resultSet.getInt(1);
                //модификаторы-параметры запроса
                String getFollowersId = "Select follower_id from followers where following_id = " + userId;
                if(since_id!=null){
                    getFollowersId= getFollowersId + " and follower_id >" + since_id;
                }
                if(order!= null){
                    getFollowersId= getFollowersId + " order by id " + order;
                } else {
                    getFollowersId= getFollowersId + " order by id desc ";
                }
                if(limit!=null){
                    getFollowersId= getFollowersId + " limit " + limit;
                }
                ResultSet followersId = connection.createStatement().executeQuery(getFollowersId);

                while(followersId.next()) {

                    StringBuilder builder = new StringBuilder();
                    Integer followerId = followersId.getInt("follower_id");

                    String followerAbout = "";
                    String followerEmail = "";
                    Boolean followerIsAnonymos = true;
                    String followerName = "";
                    String followerUsername = "";

                    final String getFollower = "Select * from users where id = " + followerId + ";";
                    ResultSet followerResultSet = connection.createStatement().executeQuery(getFollower);

                    while (followerResultSet.next()) {
                        followerAbout = followerResultSet.getString("about");
                        followerEmail = followerResultSet.getString("email");
                        followerIsAnonymos = followerResultSet.getBoolean("isAnonymos");
                        followerName = followerResultSet.getString("name");
                        followerUsername = followerResultSet.getString("username");
                    }
                    UserProfile follower = new UserProfile(followerAbout, followerEmail, followerId,
                            followerIsAnonymos, followerName, followerUsername);

                    builder.append("[");
                    final String getFollowers = "Select follower_id from followers where following_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowers);
                    while (followerResultSet.next()) {
                        final String getFollowerEmails = "Select email from users where id = "
                                + followerResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                        while (innerResultSet.next()) {
                            builder.append("\"");
                            builder.append(innerResultSet.getString(1));
                            builder.append("\"");
                        }
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfFollower(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getFollowing = "Select following_id from followers where follower_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowing);
                    while (followerResultSet.next()) {
                        final String getFollowingEmails = "Select email from users where id = "
                                + followerResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                        while (innerResultSet.next()) {
                            builder.append("\"");
                            builder.append(innerResultSet.getString(1));
                            builder.append("\"");
                        }
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfFollowing(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getFollowerSubscription = "Select thread_id from subscribe where user_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowerSubscription);
                    while (followerResultSet.next()) {
                        builder.append(followerResultSet.getInt(1));
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfSubscriptions(builder.toString());
                    builder.setLength(0);

                    result = result + follower.toJSONDetails();
                    if (resultSet.next()) {
                        result = result + ", ";
                        resultSet.previous();
                    }
                }
            }
            result = result + "]";
            connection.close();
            SuccessResponse response = new SuccessResponse(0,result);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/listFollowing/", method=RequestMethod.GET)
    public ResponseEntity followingListUser(@RequestParam String user,
                                            @RequestParam(required = false) Integer since_id,
                                            @RequestParam(required = false) Integer limit,
                                            @RequestParam(required = false) String order){
        String userEmail = user;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String getUser = "Select id from users where email = '" + userEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUser);

            String result = "[";
            while(resultSet.next()) {
                Integer userId = resultSet.getInt(1);
                //модификаторы-параметры запроса
                String getFollowingId = "Select following_id from followers where follower_id = " + userId;
                if(since_id!=null){
                    getFollowingId= getFollowingId + " and follower_id >" + since_id;
                }
                if(order!= null){
                    getFollowingId= getFollowingId + " order by id " + order;
                } else {
                    getFollowingId= getFollowingId + " order by id desc ";
                }
                if(limit!=null){
                    getFollowingId= getFollowingId + " limit " + limit;
                }
                ResultSet followingsId = connection.createStatement().executeQuery(getFollowingId);

                while(followingsId.next()) {

                    StringBuilder builder = new StringBuilder();
                    Integer followingId = followingsId.getInt("following_id");

                    String followingAbout = "";
                    String followingEmail = "";
                    Boolean followingIsAnonymos = true;
                    String followingName = "";
                    String followerUsername = "";

                    final String getFollower = "Select * from users where id = " + followingId + ";";
                    ResultSet followerResultSet = connection.createStatement().executeQuery(getFollower);

                    while (followerResultSet.next()) {
                        followingAbout = followerResultSet.getString("about");
                        followingEmail = followerResultSet.getString("email");
                        followingIsAnonymos = followerResultSet.getBoolean("isAnonymos");
                        followingName = followerResultSet.getString("name");
                        followerUsername = followerResultSet.getString("username");
                    }
                    UserProfile follower = new UserProfile(followingAbout, followingEmail, followingId,
                            followingIsAnonymos, followingName, followerUsername);

                    builder.append("[");
                    final String getFollowers = "Select follower_id from followers where following_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowers);
                    while (followerResultSet.next()) {
                        final String getFollowerEmails = "Select email from users where id = "
                                + followerResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                        while (innerResultSet.next()) {
                            builder.append("\"");
                            builder.append(innerResultSet.getString(1));
                            builder.append("\"");
                        }
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfFollower(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getFollowing = "Select following_id from followers where follower_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowing);
                    while (followerResultSet.next()) {
                        final String getFollowingEmails = "Select email from users where id = "
                                + followerResultSet.getInt(1) + ";";
                        ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                        while (innerResultSet.next()) {
                            builder.append("\"");
                            builder.append(innerResultSet.getString(1));
                            builder.append("\"");
                        }
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfFollowing(builder.toString());
                    builder.setLength(0);

                    builder.append("[");
                    final String getFollowerSubscription = "Select thread_id from subscribe where user_id= "
                            + follower.getId() + ";";
                    followerResultSet = connection.createStatement().executeQuery(getFollowerSubscription);
                    while (followerResultSet.next()) {
                        builder.append(followerResultSet.getInt(1));
                        if (followerResultSet.next()) {
                            builder.append(", ");
                            followerResultSet.previous();
                        }
                    }
                    builder.append("]");
                    follower.setListOfSubscriptions(builder.toString());
                    builder.setLength(0);

                    result = result + follower.toJSONDetails();
                    if (resultSet.next()) {
                        result = result + ", ";
                        resultSet.previous();
                    }
                }
            }
            result = result + "]";
            connection.close();
            SuccessResponse response = new SuccessResponse(0,result);
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/follow/", method=RequestMethod.POST)
    public ResponseEntity followUser(@RequestBody FollowUnfollowUser body) {
        String followerEmail = body.getFollower();
        String followeeEmail = body.getFollowee();

        String followerAbout = "";
        Integer followerId = -1;
        Boolean followerIsAnonymos = true;
        String followerName = "";
        String followerUsername = "";


        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String getFollowerId = "Select * from users where email = '" + followerEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getFollowerId);
            while(resultSet.next()){
                followerId = resultSet.getInt("id");
                followerAbout = resultSet.getString("about");
                followerIsAnonymos = resultSet.getBoolean("isAnonymos");
                followerName = resultSet.getString("name");
                followerUsername = resultSet.getString("username");
            }

            final String getFolloweeId = "Select id from users where email = '" + followeeEmail + "';";
            resultSet = connection.createStatement().executeQuery(getFolloweeId);
            Integer followeeId = -1;
            while(resultSet.next()){
                followeeId = resultSet.getInt(1);
            }

            Follow follow = new Follow(followerId, followeeId);
            final String createFollow = follow.insert();
           // System.out.println(createFollow);
            PreparedStatement preparedStatementCreateFollow = connection.prepareStatement(createFollow);
            preparedStatementCreateFollow.executeUpdate(); //зафоловили юзера, теперь получаем информацию о нас

            UserProfile follower = new UserProfile(followerIsAnonymos, followerEmail,
                    followerName, followerAbout, followerUsername);
            follower.setId(followerId);

            StringBuilder builder = new StringBuilder();

            builder.append("[");
            final String getFollowers = "Select follower_id from followers where following_id= '" + follower.getId() + "';";
            resultSet = connection.createStatement().executeQuery(getFollowers);
            while(resultSet.next()) {
                final String getFollowerEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfFollower(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getFollowing = "Select following_id from followers where follower_id= "
                    + follower.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getFollowing);
            while(resultSet.next()) {
                final String getFollowingEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfFollowing(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getSubscription = "Select thread_id from subscribe where user_id= " + follower.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getSubscription);
            while(resultSet.next()) {
                builder.append(resultSet.getInt(1));
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfSubscriptions(builder.toString());
            builder.setLength(0);

            connection.close();

            SuccessResponse response = new SuccessResponse(0,follower.toJSONDetails());
            System.out.println("!!!");
            System.out.println(follower.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());

        } catch(SQLException e) {
            e.printStackTrace();
        }
            return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/unfollow/", method=RequestMethod.POST)
    public ResponseEntity unfollowUser(@RequestBody FollowUnfollowUser body) {
        String followerEmail = body.getFollower();
        String followeeEmail = body.getFollowee();

        String followerAbout = "";
        Integer followerId = -1;
        Boolean followerIsAnonymos = true;
        String followerName = "";
        String followerUsername = "";


        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );

            final String getFollowerId = "Select * from users where email = '" + followerEmail + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getFollowerId);
            while(resultSet.next()){
                followerId = resultSet.getInt("id");
                followerAbout = resultSet.getString("about");
                followerIsAnonymos = resultSet.getBoolean("isAnonymos");
                followerName = resultSet.getString("name");
                followerUsername = resultSet.getString("username");
            }

            final String getFolloweeId = "Select id from users where email = '" + followeeEmail + "';";
            resultSet = connection.createStatement().executeQuery(getFolloweeId);
            Integer followeeId = -1;
            while(resultSet.next()){
                followeeId = resultSet.getInt(1);
            }

            Follow follow = new Follow(followerId, followeeId);
            final String deleteFollow = follow.delete();
            PreparedStatement preparedStatementDeleteFollow = connection.prepareStatement(deleteFollow);
            preparedStatementDeleteFollow.executeUpdate(); //зафоловили юзера, теперь получаем информацию о нас

            UserProfile follower = new UserProfile(followerIsAnonymos, followerEmail,
                    followerName, followerAbout, followerUsername);
            follower.setId(followerId);

            StringBuilder builder = new StringBuilder();

            builder.append("[");
            final String getFollowers = "Select follower_id from followers where following_id= '" + follower.getId() + "';";
            resultSet = connection.createStatement().executeQuery(getFollowers);
            while(resultSet.next()) {
                final String getFollowerEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfFollower(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getFollowing = "Select following_id from followers where follower_id= "
                    + follower.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getFollowing);
            while(resultSet.next()) {
                final String getFollowingEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                while(innerResultSet.next()){
                    builder.append("\"");
                    builder.append(innerResultSet.getString(1));
                    builder.append("\"");
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfFollowing(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getSubscription = "Select thread_id from subscribe where user_id= " + follower.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getSubscription);
            while(resultSet.next()) {
                builder.append(resultSet.getInt(1));
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            follower.setListOfSubscriptions(builder.toString());
            builder.setLength(0);

            connection.close();

            SuccessResponse response = new SuccessResponse(0,follower.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/updateProfile/", method=RequestMethod.POST)
    public ResponseEntity updateUser(@RequestBody UpdateUser body) {
        final String about = body.getAbout();
        final String email = body.getEmail();
        Integer id = -1;
        final String name = body.getName();
        String username = "";
        Boolean isAnonymos = false;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            final String getUser = "Select id, username, isAnonymos from users where email = '" + email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUser);
            while(resultSet.next()){
                id = resultSet.getInt("id");
                isAnonymos = resultSet.getBoolean("isAnonymos");
                username = resultSet.getString("username");
            }
            UserProfile user = new UserProfile(about, email, id, isAnonymos, name, username);

            final String updateUser = user.update();
           // System.out.println(updateUser);
            PreparedStatement preparedStatementDeleteFollow = connection.prepareStatement(updateUser);
            preparedStatementDeleteFollow.executeUpdate();

            StringBuilder builder = new StringBuilder();
            builder.append("[");
            final String getFollowers = "Select follower_id from followers where following_id= '" + user.getId() + "';";
            resultSet = connection.createStatement().executeQuery(getFollowers);
            while(resultSet.next()) {
                final String getFollowerEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowerEmails);
                while(innerResultSet.next()){
                    builder.append(innerResultSet.getString(1));
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            user.setListOfFollower(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getFollowing = "Select following_id from followers where follower_id= "
                    + user.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getFollowing);
            while(resultSet.next()) {
                final String getFollowingEmails = "Select email from users where id = " + resultSet.getInt(1) + ";";
                ResultSet innerResultSet = connection.createStatement().executeQuery(getFollowingEmails);
                while(innerResultSet.next()){
                    builder.append(innerResultSet.getString(1));
                }
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            user.setListOfFollowing(builder.toString());
            builder.setLength(0);

            builder.append("[");
            final String getSubscription = "Select thread_id from subscribe where user_id= " + user.getId() + ";";
            resultSet = connection.createStatement().executeQuery(getSubscription);
            while(resultSet.next()) {
                builder.append(resultSet.getInt(1));
                if(resultSet.next()){
                    builder.append(", ");
                    resultSet.previous();
                }
            }
            builder.append("]");
            user.setListOfSubscriptions(builder.toString());
            builder.setLength(0);


            connection.close();

            SuccessResponse response = new SuccessResponse(0,user.toJSONDetails());
            return  ResponseEntity.ok(response.createJSONResponce());
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return  ResponseEntity.ok("OK");
    }

    @RequestMapping(path = "/db/api/user/listPosts/", method = RequestMethod.GET)
    public ResponseEntity userPostList(@RequestParam String user,
                                   @RequestParam(required = false) String since,
                                   @RequestParam(required = false) Integer limit,
                                   @RequestParam(required = false) String order) {
        final String email = user;
        Connection connection;
        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD );
            StringBuilder builder = new StringBuilder();

            final String getUserId = "Select id from users where email = '" + email + "';";
            ResultSet resultSet = connection.createStatement().executeQuery(getUserId);
            Integer userId = -1;
            while(resultSet.next()) {
                userId = resultSet.getInt(1);
            } //получили айди юзера

            builder.append("Select * from posts where user_id = ");
            builder.append(userId);

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
            resultSet = connection.createStatement().executeQuery(getPostsList);
            builder.setLength(0);
            builder.append("[ ");
            while(resultSet.next()) {
                Integer postId = resultSet.getInt("id");
                Integer threadId = resultSet.getInt("thread_id");
                String message = resultSet.getString("message");
                //костыль. Такое чувство, что скуль хранит дату с точностью до второго знака после запятой
                String date = resultSet.getString("date").substring(0,resultSet.getString("date").length()-2);
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

                if(parentId==0) {
                    parentId = null;
                }
                Post tempPost = new Post(postId, threadId, message, date, userId, forumId, parentId, isSpam,
                        isApproved, isHighlighted, isEdited, isDeleted, likes, dislikes, points);

                tempPost.setUserEmail(email);
                String forumShortName = "";
                final String getForumShortName = "Select short_name from forums " +
                        " WHERE id = " + forumId + ";";
                ResultSet forumResultSet = connection.createStatement().executeQuery(getForumShortName);
                while(forumResultSet.next()) {
                    forumShortName = forumResultSet.getString(1);
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




}
