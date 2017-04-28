package cn.ushang.myfire;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ushang000 on 2017/2/20.
 */

// [START blog_user_class]
@IgnoreExtraProperties
public class User {

    public String username;
    public String password;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
// [END blog_user_class]
