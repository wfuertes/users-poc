# users

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

## Local PostgreSQL

Start the PostgreSQL 18 database used by the application with Docker Compose:

```shell script
docker compose up -d
```

The database is available at `localhost:54321` with these credentials:

| Setting | Value |
| --- | --- |
| Database | `users` |
| Username | `postgres` |
| Password | `postgres` |
| JDBC URL | `jdbc:postgresql://localhost:54321/users` |

Flyway runs automatically when Quarkus starts. To reset the local database and
run all migrations from the beginning:

```shell script
docker compose down -v
docker compose up -d
```

## jOOQ code generation

jOOQ reads the live PostgreSQL schema and generates typed tables and records
during Maven's `generate-sources` phase:

```shell script
./mvnw generate-sources
```

Generated sources are written to `target/generated-sources/jooq` and use the
`users.jooq` package. For the current schema, the main generated classes are:

```java
users.jooq.tables.Users
users.jooq.tables.records.UsersRecord
```

Maven starts a disposable PostgreSQL 18 Testcontainers database, initializes it
with the current migration, and generates the jOOQ sources. Run generation
again after adding or changing a Flyway migration. Generated files under
`target/` are build output and should not be edited manually.

```shell script
./mvnw generate-sources
```

To start Quarkus with remote debugging enabled:

```shell script
./mvnw quarkus:dev -Ddebug=5005
```

The REST API is available at `http://localhost:8080/users`.

Create a user:

```shell script
curl --location 'http://localhost:8080/users' \
	--header 'Content-Type: application/json' \
	--data-raw '{
		"email": "wfuertes@gmail.com",
		"password": "super@pong"
	}'
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/users-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Flyway ([guide](https://quarkus.io/guides/flyway)): Handle your database schema migrations
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
