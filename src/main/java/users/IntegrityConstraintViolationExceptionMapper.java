package users;

import java.util.Map;

import org.jooq.exception.IntegrityConstraintViolationException;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IntegrityConstraintViolationExceptionMapper implements ExceptionMapper<IntegrityConstraintViolationException> {

    private static final String EMAIL_CONSTRAINT = "users_email_key";

    @Override
    public Response toResponse(IntegrityConstraintViolationException exception) {
        boolean emailAlreadyExists = containsConstraint(exception, EMAIL_CONSTRAINT);
        String code = emailAlreadyExists ? "USER_EMAIL_EXISTS" : "INTEGRITY_CONSTRAINT_VIOLATION";
        String message = emailAlreadyExists
                ? "A user with this email already exists."
                : "The request violates a database constraint.";

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of("code", code, "message", message))
                .build();
    }

    private boolean containsConstraint(Throwable exception, String constraint) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null && cause.getMessage().contains(constraint)) {
                return true;
            }
        }
        return false;
    }
}
