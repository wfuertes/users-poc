package users;

import java.time.Instant;

public record User(
        String id,
        String email,
        String password,
        Instant createdAt,
        Instant updatedAt) {

}
