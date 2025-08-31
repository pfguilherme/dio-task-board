package io.github.pfguilherme.persistence.migration;

import io.github.pfguilherme.persistence.config.ConnectionConfig;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.AllArgsConstructor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class MigrationStrategy
{
    private Connection connection;

    public void executeMigration()
    {
        var out = System.out;
        var err = System.err;

        try (var fileOutputStream = new FileOutputStream("liquibase.log"))
        {
            System.setOut(new PrintStream(fileOutputStream));
            System.setErr(new PrintStream(fileOutputStream));

            try (var connection = ConnectionConfig.getConnection();
                 var jdbcConnection = new JdbcConnection(connection))
            {
                var changeLogFile = "/db/changelog/db.changelog-master.yml";
                var liquibase = new Liquibase(
                    changeLogFile,
                    new ClassLoaderResourceAccessor(),
                    jdbcConnection
                );

                liquibase.update();
            }
            catch (SQLException | LiquibaseException e)
            {
                e.printStackTrace();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.setOut(out);
            System.setErr(err);
        }
    }
}
