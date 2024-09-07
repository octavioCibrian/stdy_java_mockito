package org.example.data;

import org.example.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    Map<String, User> users = new HashMap<>();
    @Override
    public boolean save(User user) {
        if(users.containsKey(user.getId())) {
            return false;
        }

        users.put(user.getId(), user);

        users.forEach((str, str2) -> System.out.println(str+" : "+str2));
        return true;
    }
}
