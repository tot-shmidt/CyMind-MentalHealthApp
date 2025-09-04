package coms309;

import java.util.ArrayList;

public class UserManager {
    private static final ArrayList<User> users = new ArrayList<User>();

    public static void addUser(User user) {
        users.add(user);
    }

    public static User getUser(String name) {
        for (User user : users) {
            if (user.name().equals(name)) {
                return user;
            }
        }

        return null;
    }
}
