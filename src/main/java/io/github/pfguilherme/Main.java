package io.github.pfguilherme;

import io.github.pfguilherme.persistence.config.ConnectionConfig;
import io.github.pfguilherme.persistence.migration.MigrationStrategy;
import io.github.pfguilherme.ui.MainMenu;

import java.sql.SQLException;

public class Main
{
    public static void main(String[] args) throws SQLException
    {
        try (var connection = ConnectionConfig.getConnection())
        {
            new MigrationStrategy(connection).executeMigration();
        }
        new MainMenu().execute();
    }
}
