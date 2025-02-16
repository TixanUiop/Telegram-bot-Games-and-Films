package org.evgeny.Util;


import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@UtilityClass
public class GetConnection {
    private static final String URL = "db.connection.url";
    private static final String USER = "db.connection.user";
    private static final String PASSWORD = "db.connection.password";

    static {
        loadDriver();
    }

    private static void loadDriver() {
        try  {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(
             GetProperties.get(URL),
             GetProperties.get(USER),
             GetProperties.get(PASSWORD)
        );
    }

}
