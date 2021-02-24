package com.revature.repositories;

import com.revature.models.User;
import com.revature.util.ConnectionFactory;
import com.revature.util.HibernateUtil;
import org.hibernate.Session;

import java.sql.*;
import java.util.*;

public class UserRepository {
    private String baseQuery = "SELECT * FROM project_1.ers_users eu ";
    private String baseInsert = "INSERT INTO project_1.ers_users ";
    private String baseUpdate = "UPDATE project_1.ers_users eu ";

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
        List<User> users = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseQuery + " order by eu.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            users = mapResultSet(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * A method to get a single User by email
     * @param email the email address to search the DB for
     * @return returns an Optional user
     * @throws SQLException e
     */
    public Optional<User> getAUserByEmail(String email) {
        Optional<User> user = Optional.empty();
        try (Connection conn = ConnectionFactory.getInstance().getConnection()){
            String sql = baseQuery + "WHERE email =? ";
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1,email);
            ResultSet rs = psmt.executeQuery();
            user = mapResultSet(rs).stream().findFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public Optional<User> getAUserByUsername(String userName) {
        Optional<User> user = Optional.empty();
        try (Connection conn = ConnectionFactory.getInstance().getConnection()){
            String sql = baseQuery + "WHERE username = ?";
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1,userName);
            ResultSet rs = psmt.executeQuery();
            user = mapResultSet(rs).stream().findFirst();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        System.out.println(user);
        return user;
    }

    /**
     * A method to get a single user by a given username and password
     * @param userName the users username
     * @param password the users password
     * @return returns an optional user
     * @throws SQLException e
     */
    public Optional<User> getAUserByUsernameAndPassword(String userName, String password) {
        Optional<User> user = Optional.empty();
        try (Connection conn = ConnectionFactory.getInstance().getConnection()){
            String sql = baseQuery + "WHERE username = ? AND  password = project_1.crypt(?, password)";
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setString(1,userName);
            psmt.setString(2,password);
            ResultSet rs = psmt.executeQuery();
            user = mapResultSet(rs).stream().findFirst();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        System.out.println(user);
        return user;
    }

    //---------------------------------- UPDATE -------------------------------------------- //

    public boolean updateAUser(User newUser) {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                    "SET first_name=?, last_name=?, email=?, user_role_id=?, username=?, password= project_1.crypt(?, project_1.gen_salt('bf', 10))\n" +
                    "WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,newUser.getFirstname());
            ps.setString(2,newUser.getLastname());
            ps.setString(3,newUser.getEmail());
            ps.setInt(4,newUser.getUserRole());
            ps.setString(5,newUser.getUsername());
            ps.setString(6, newUser.getPassword());
            ps.setInt(7,newUser.getUserId());
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single User from the database
     * @param userId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     * @throws SQLException
     */
    public boolean deleteAUserById(Integer userId) {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                         "SET user_role_id=4\n" +
                         "WHERE id=? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    //---------------------------------- UTIL -------------------------------------------- //

    /**
     * A method to map the result sets from the users queries
     * @param rs a result set
     * @return a set of users
     * @throws SQLException e
     */
    private List<User> mapResultSet(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()){
            User temp = new User();
            temp.setUserId(rs.getInt("id"));
            temp.setUsername(rs.getString("username"));
            temp.setPassword(rs.getString("password"));
            temp.setEmail(rs.getString("email"));
            temp.setFirstname(rs.getString("first_name"));
            temp.setLastname(rs.getString("last_name"));
            temp.setUserRole(rs.getInt("user_role_id"));
            users.add(temp);
        }
        return users;
    }


}
