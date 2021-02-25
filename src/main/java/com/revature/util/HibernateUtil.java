package com.revature.util;

import com.revature.models.Reimbursement;
import com.revature.models.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.postgresql.Driver");
                settings.put(Environment.URL, System.getProperty("url"));
                settings.put(Environment.USER, System.getProperty("user"));
                settings.put(Environment.PASS, System.getProperty("password"));
                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL82Dialect");

//                System.out.println("+------------------------------------------------------");
//
//                System.out.println("The URL is: "+System.getenv("url"));
//                System.out.println("The Username is: "+System.getenv("user"));
//                System.out.println("The password is: "+System.getenv("password"));
//
//                System.out.println("+-------------------------------------------------------");

                //settings.put(Environment.SHOW_SQL, "true");

                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

                //settings.put(Environment.HBM2DDL_AUTO, "create-drop");

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
        System.out.println("Session factory is made: "+sessionFactory == null);
        return sessionFactory;
    }
}
