package users;

import static users.jooq.tables.Users.USERS;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.tools.StringUtils;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import users.jooq.tables.records.UsersRecord;

@Path("/users")
public class UserResource {
    private DSLContext dsl;

    @Inject
    UserResource(DSLContext dsl) {
        this.dsl = dsl;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(CreateUserDto createUser) {
        UsersRecord record = dsl.newRecord(USERS)
                .setEmail(createUser.email())
                .setPassword(createUser.password());
        record.store();
        record.refresh();
        return Response.status(Response.Status.CREATED)
                .entity(deserialize(record))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers(
            @QueryParam("email") String email,
            @QueryParam("limit") @DefaultValue("10") int limit,
            @QueryParam("offset") @DefaultValue("0") int offset) {

        // 1. The inner subquery: It only selects the indexed ID column
        var deferred = dsl.select(USERS.ID)
                .from(USERS)
                .where(StringUtils.isBlank(email) ? DSL.noCondition() : USERS.EMAIL.containsIgnoreCase(email))
                .orderBy(USERS.ID.desc()) // Crucial: Orders the subquery so offset is predictable
                .limit(limit)
                .offset(offset)
                .asTable("deferred");

        // 2. The outer query: Joins the full table ONLY to the 10 fetched IDs
        List<UserDto> users = dsl.select(USERS.ID, USERS.EMAIL, USERS.PASSWORD, USERS.CREATED_AT, USERS.UPDATED_AT)
                .from(USERS)
                .join(deferred).on(USERS.ID.eq(deferred.field(USERS.ID)))
                .orderBy(deferred.field(USERS.ID).desc())
                .fetch()
                .map(record -> deserialize(record.into(UsersRecord.class)));

        return Response.ok(users).build();
    }

    private static UserDto deserialize(UsersRecord record) {
        return new UserDto(
                record.getId().toString(),
                record.getEmail(),
                record.getCreatedAt().toInstant(),
                record.getUpdatedAt().toInstant());
    }
}
