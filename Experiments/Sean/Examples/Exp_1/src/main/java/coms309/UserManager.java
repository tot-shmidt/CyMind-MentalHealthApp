package coms309;

import java.util.HashMap;

public class UserManager {
    private static final HashMap<String, User> users = new HashMap<String, User>();

    public static void addUser(User user) {
        users.put(user.name(), user);
    }

    public static User getUser(String name) {
        return users.get(name);
    }
}
