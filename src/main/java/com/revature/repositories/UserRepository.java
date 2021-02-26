package com.revature.repositories;

import com.revature.models.User;
import com.revature.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.*;

public class UserRepository {

    public UserRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //

    /**
     * A method tho add a new user to the database, hashes passwords before inserting
     * @param newUser the user to be added
     * @return returns true if one and only one row was inserted
     * @throws SQLException e
     */
    public boolean addUser(User newUser)  {
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

    public List<User> getAllusers() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "From User";
        Query<User> query = session.createQuery(hql);
        return query.list();
    }
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
     * @throws SQLException e
     */
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
     * @throws SQLException e
     */
    @SuppressWarnings("unchecked")
    public Optional<User> getAUserByUsernameAndPassword(String userName, String password) {
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
            //session.getTransaction().rollback();
            session.close();
            e.printStackTrace();
            return Optional.empty();
        }
    }

    //---------------------------------- UPDATE -------------------------------------------- //

    public boolean updateAUser(User newUser) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            //session.evict(newUser)?
            session.merge(newUser);
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

    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single User from the database
     * @param userId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     * @throws SQLException
     */
    public boolean deleteAUserById(Integer userId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            //session.evict(newUser)?
            User delete = session.find(User.class, userId);
            session.remove(delete);
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

}
