package org.event.service.user;

import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter {

    public UserDto toDto(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
