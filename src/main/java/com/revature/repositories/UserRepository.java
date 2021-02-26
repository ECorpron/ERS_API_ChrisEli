package com.revature.repositories;

import com.revature.models.User;
import com.revature.util.HibernateUtil;
import com.revature.util.PasswordHash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.*;

/**
 * Handles all CRUD methods accessing the User table in the database. Uses Hibernate
 */
public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);
    /**
     * Empty constructor
     */
    public UserRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //

    /**
     * A method tho add a new user to the database, hashes passwords before inserting
     * @param newUser the user to be added
     * @return returns true if one and only one row was inserted
     */
    public boolean addUser(User newUser)  {
        newUser.setPassword(PasswordHash.getInstance().hashing(newUser.getPassword()));

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            session.save(newUser);
        } catch (Exception e){
            session.getTransaction().rollback();
            session.close();
            e.printStackTrace();
            return false;
        }

        session.getTransaction().commit();
        session.close();
        return true;
    }

    //---------------------------------- READ -------------------------------------------- //

    /**
     * Returns a list of all Users
     * @return returns a list of all Users
     */
    @SuppressWarnings("unchecked")
    public List<User> getAllusers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "From User";
        Query<User> query = session.createQuery(hql);
        return query.list();
    }

    /**
     * Gets a spcific user with a specified id
     * @param id the id of the user being searched for
     * @return returns an optional of the user. Contains null if the user is not found
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUserId(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM User WHERE id = :id";
        Query<User> query = session.createQuery(hql);
        query.setParameter("id", id);
        List<User> list = query.list();
        session.getTransaction().commit();
        session.close();
        if (list.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    /**
     * A method to get a single User by email
     * @param email the email address to search the DB for
     * @return returns an Optional user
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM User WHERE email = :email";
        Query<User> query = session.createQuery(hql);
        query.setParameter("email", email);
        List<User> list = query.list();
        session.getTransaction().commit();
        session.close();
        if (list.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    /**
     * Gets a specific user by their username
     * @param userName the username of the searched for user
     * @return returns an optional of the user. Contains null if the user isn't found
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUsername(String userName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM User where username = :userName";
        Query<User> query = session.createQuery(hql);
        query.setParameter("userName",userName);
        List<User> list = query.list();
        session.getTransaction().commit();
        session.close();
        if (list.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    /**
     * A method to get a single user by a given username and password
     * @param userName the users username
     * @param password the users password
     * @return returns an optional user
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUsernameAndPassword(String userName, String password) {
        password = PasswordHash.getInstance().hashing(password);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            String hql = "FROM User WHERE username = :name AND password = :pass";
            Query<User> query = session.createQuery(hql);
            query.setParameter("name",userName);
            query.setParameter("pass", password);

            List<User> results = query.list();
            session.getTransaction().commit();
            session.close();
            return Optional.of(results.get(0));
        } catch (Exception e){
            session.close();
            logger.error(e.getStackTrace());
            return Optional.empty();
        }
    }

    //---------------------------------- UPDATE -------------------------------------------- //

    /**
     * Updates a user in the database with information from the inputted user
     * @param newUser the user to update
     * @return returns true if an entry was updated, else returns false
     */
    public boolean updateAUser(User newUser) {
        newUser.setPassword(PasswordHash.getInstance().hashing(newUser.getPassword()));

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            session.merge(newUser);
        } catch (Exception e){
            session.getTransaction().rollback();
            session.close();
            logger.error(e.getStackTrace());
            return false;
        }

        session.getTransaction().commit();
        session.close();
        return true;
    }

    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single User from the database
     * @param userId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     */
    public boolean deleteAUserById(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            User delete = session.find(User.class, userId);
            session.remove(delete);
        } catch (Exception e){
            session.getTransaction().rollback();
            session.close();
            logger.error(e.getStackTrace());
            return false;
        }

        session.getTransaction().commit();
        session.close();
        return true;
    }

}
