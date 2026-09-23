package users.codegen;

import java.nio.file.Path;
import org.flywaydb.core.Flyway;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.postgresql.PostgreSQLContainer;

public final class JooqCodegen {
    private JooqCodegen() {
    }

    public static void main(String[] args) throws Exception {
        try (PostgreSQLContainer database = new PostgreSQLContainer("postgres:18")) {
            database.start();

            Flyway.configure()
                .dataSource(database.getJdbcUrl(), database.getUsername(), database.getPassword())
                .locations("filesystem:src/main/resources/db/migration")
                .load()
                .migrate();

            GenerationTool.generate(new Configuration()
                .withJdbc(new Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUrl(database.getJdbcUrl())
                    .withUser(database.getUsername())
                    .withPassword(database.getPassword()))
                .withGenerator(new Generator()
                    .withName("org.jooq.codegen.JavaGenerator")
                    .withDatabase(new Database()
                        .withName("org.jooq.meta.postgres.PostgresDatabase")
                        .withInputSchema("public")
                        .withIncludes("users")
                        .withExcludes("flyway_schema_history"))
                    .withGenerate(new Generate()
                        .withRecords(true)
                        .withFluentSetters(true))
                    .withTarget(new Target()
                        .withPackageName("users.jooq")
                        .withDirectory(Path.of("target", "generated-sources", "jooq").toString()))));
        }
    }
}
