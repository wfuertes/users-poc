package users;

import java.time.Instant;

public record UserDto(
                String id,
                String email,
                Instant createdAt,
                Instant updatedAt) {

}
