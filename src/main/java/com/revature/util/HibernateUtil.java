package com.revature.util;

import com.revature.models.Reimbursement;
import com.revature.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * Handles session creation to the database using Hibernate. Uses programmatic configuration.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();

                File temp = new File("src/main/resources/properties.properties");

                if (temp.exists()) {
                    try {
                        Properties props = new Properties();

                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        InputStream input = loader.getResourceAsStream("properties.properties");

                        props.load(input);

                        settings.put(Environment.DRIVER, "org.postgresql.Driver");
                        settings.put(Environment.URL, props.getProperty("url"));
                        settings.put(Environment.USER, props.getProperty("username"));
                        settings.put(Environment.PASS, props.getProperty("password"));
                        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    settings.put(Environment.DRIVER, "org.postgresql.Driver");
                    settings.put(Environment.URL, System.getProperty("url"));
                    settings.put(Environment.USER, System.getProperty("username"));
                    settings.put(Environment.PASS, System.getProperty("password"));
                    settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");
                }


                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(Reimbursement.class);
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
