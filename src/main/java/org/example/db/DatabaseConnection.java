//package org.example.db;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.example.utils.Properties;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseConnection {
//    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
//    private static final String URL = Properties.get("URL_P");
//    private static final String USER = Properties.get("USER_P");
//    private static final String PASSWORD = Properties.get("PASSWORD_P");
//
//    public static Connection getConnection() throws SQLException {
//        logger.info("Attempting to establish the DB");
//        try {
//            Connection connection = DriverManager.getConnection("postgresql://postgsql_db_user:LiVlOfh1WkjIKIw8kIZo62sk4wAQbx4z@dpg-ctt7u3l2ng1s73c6i9c0-a.oregon-postgres.render.com/postgsql_db", "postgsql_db_user", "LiVlOfh1WkjIKIw8kIZo62sk4wAQbx4z");
//            logger.info("Database connection established successfully.");
//            return connection;
//        } catch (SQLException e) {
//            logger.error("Failed to establish a database connection.", e);
//            throw e;
//        }
//    }
//}
package org.example.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.utils.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Logger logger = LogManager.getLogger(DatabaseConnection.class);
//    private static final String URL = "jdbc:postgresql://dpg-ctt7u3l2ng1s73c6i9c0-a.oregon-postgres.render.com:5432/postgsql_db";
//    private static final String USER = "postgsql_db_user";
//    private static final String PASSWORD = "LiVlOfh1WkjIKIw8kIZo62sk4wAQbx4z";

    private static final String URL = Properties.get("URL");
    private static final String USER = Properties.get("USER");
    private static final String PASSWORD = Properties.get("PASSWORD");
    public static Connection getConnection() throws SQLException {
        logger.info("Attempting to establish the DB");
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Database connection established successfully.");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish a database connection.", e);
            throw e;
        }
    }
}
