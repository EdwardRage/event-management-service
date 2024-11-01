package org.event.service.user;

import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                entity.getRole()
        );
    }
}
