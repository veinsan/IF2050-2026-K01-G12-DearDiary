DearDiary (DeDi) - Local development

This project is a JavaFX application using Java 21 and PostgreSQL. The repository includes a helper to bootstrap the database schema automatically when the application connects and does not find the core `pengguna` table.

Requirements

- Java 21 (OpenJDK 21)
- Maven 3.8+
- PostgreSQL (local) or Docker (recommended for easy setup)

Auto-bootstrap behavior

When the app establishes its first JDBC connection it will check for the existence of the `pengguna` table. If the table is missing and a `sql/schema.sql` file exists at the project root, the app will attempt to execute the statements in that file to create the schema and seed data. The bootstrap is best-effort and errors are printed to stderr; if bootstrap fails you should run the SQL manually (instructions below).

Running locally (quick)

1. Using Docker (recommended):

```powershell
# start a local Postgres with the password found in DatabaseConnection
docker run --name dedidb -e POSTGRES_PASSWORD=irghisatya8 -p 5432:5432 -d postgres:15
# create the database
docker exec -i dedidb psql -U postgres -c "CREATE DATABASE deardiary;"
# copy and run the schema inside the container
docker cp .\sql\schema.sql dedidb:/tmp/schema.sql
docker exec -i dedidb psql -U postgres -d deardiary -f /tmp/schema.sql
```

2. Or using psql locally (assumes Postgres already installed):

```powershell
psql -h localhost -p 5432 -U postgres -c "CREATE DATABASE deardiary;"
psql -h localhost -p 5432 -U postgres -d deardiary -f .\sql\schema.sql
```

Run the app

```powershell
mvn -DskipTests compile
mvn javafx:run
```

Seeded credentials

- researcher1 / password123  (role Researcher)
- guest1      / password123  (role Tim R&D)

Notes

- The project requires Java 21. The `pom.xml` is set to compile for Java 21.
- If you want the application to create the database itself (CREATE DATABASE), run the Docker commands or create the database manually first. The bootstrap only executes the statements in `sql/schema.sql` against the configured `deardiary` database.

If you want, I can also add a small command-line tool to run the schema from Maven or automatically create the database using the Postgres superuser credentials; tell me which you prefer.