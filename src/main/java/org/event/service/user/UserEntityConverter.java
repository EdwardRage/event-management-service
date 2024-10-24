package org.event.service.user;

import org.springframework.stereotype.Component;

@Component
public class UserEntityConverter {

    /*public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user
        );
    }*/

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getAge(),
                UserRole.valueOf(entity.getRole())
        );
    }
}
