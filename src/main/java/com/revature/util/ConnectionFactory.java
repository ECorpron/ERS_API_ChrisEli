package com.revature.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * A Class to get a connection from the connection factory
 */
public class ConnectionFactory {
    private Properties props = new Properties();
    //private static ConnectionFactory connFactory = new ConnectionFactory();

    private ConnectionFactory(){
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream propsInput = loader.getResourceAsStream("application.properties");
            if (propsInput == null) {
                props.setProperty("url", System.getProperty("url"));
                props.setProperty("username", System.getProperty("username"));
                props.setProperty("password", System.getProperty("password"));
            } else {
                props.load(propsInput);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current instance of the Connection to the DB
     * @return returns and instance of thte connection
     */
    public static ConnectionFactory getInstance(){
        return null;
    }

    /**
     * A method to get a connection from the connection factory
     * @return returns a connection to the DB
     */
    public Connection getConnection(){
        Connection conn = null;
//        try {
//            // Force the JVM to load the PostGreSQL JDBC driver
//            Class.forName("org.postgresql.Driver");
//            conn = DriverManager.getConnection(props.getProperty("url"),
//                    props.getProperty("username"),
//                    props.getProperty("password"));
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//        }
//        if (conn == null) {
//            throw new RuntimeException("Failed to establish connection.");
//        }
        return conn;
    }

}
