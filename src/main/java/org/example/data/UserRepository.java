package org.example.data;

import org.example.model.User;

public interface UserRepository {

    boolean save(User user);
}
