package edu.iastate.cs3090.exp_1.blog;

import edu.iastate.cs3090.exp_1.model.User;
import lombok.Getter;

import java.security.KeyException;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    @Getter
    private static final HashMap<UUID, User> users = new HashMap<UUID, User>();

    public static void addUser(User user) {
        users.put(user.getId(), user);
    }

    public static User getUser(UUID id) throws KeyException {
        if (!users.containsKey(id)) {
            throw new KeyException("UUID does not exist");
        }
        return users.get(id);
    }

    public static void replaceUser(User user) throws KeyException {
        if (!users.containsKey(user.getId())) {
            throw new KeyException("UUID does not exist");
        }
        users.replace(user.getId(), user);
    }

    public static void deleteUser(UUID id) throws KeyException {
        if (!users.containsKey(id)) {
            throw new KeyException("UUID does not exist");
        }
        users.remove(id);
    }
}
