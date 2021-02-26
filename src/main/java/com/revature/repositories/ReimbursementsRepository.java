package com.revature.repositories;

import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.util.ConnectionFactory;
import com.revature.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * A class to interact with the database to CRUD reimbursement objects
 */
public class ReimbursementsRepository {
    //base query that combines the name and resolver names from one query
    private String baseQuery = "SELECT er.id, er.amount, er.description, er.reimbursement_status_id, \n" +
            "er.reimbursement_type_id, er.resolved, er.submitted,  er.author_id , er.resolver_id,\n" +
            "author.first_name as author_first_name , author.last_name as author_last_name , \n" +
            "resolver.first_name as resolver_first_name, resolver.last_name as resolver_last_name\n" +
            "FROM project_1.ers_reimbursements er\n" +
            "left join project_1.ers_users author \n" +
            "on er.author_id = author.id\n" +
            "left join project_1.ers_users resolver \n" +
            "on er.resolver_id = resolver.id ";

    private String queryString = "FROM ERS_REIMBURSEMENTS er\n" +
            "left join ERS_USERS author \n" +
            "on er.author_id = author.id\n" +
            "AND er.resolver_id = resolver.id ";

    private String baseInsert = "INSERT INTO project_1.ers_reimbursements ";
    private String baseUpdate = "UPDATE project_1.ers_reimbursements er ";

    public ReimbursementsRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //
    /**
     * Adds a reimbursement to the database, Does not handle Images!
     * @param reimbursement the reimbursement to be added to the DB
     * @throws SQLException e
     * @throws IOException e
     */
    // TODO add support to persist receipt images to data source
    public boolean addReimbursement(Reimbursement reimbursement) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        try {
            session.save(reimbursement);
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

    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbursements() {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Reimbursement> list = session.createQuery("FROM Reimbursement").list();
        List<RbDTO> reimbursements = mapResultListToDTO(list);

        session.getTransaction().commit();
        session.close();

        return reimbursements;
    }

    public List<RbDTO> getAllReimbSetByStatus(Integer statusId) {
        //List<RbDTO> reimbursements = new ArrayList<>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where reimbursement_status_id = :id";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("id",statusId);
        List<Reimbursement> list = query.list();
        List<RbDTO> reimbursements = mapResultListToDTO(list);
        session.getTransaction().commit();
        session.close();

        return reimbursements;
    }

    /**
     * A method to get Reimbursements by the id of the reimbursement itself
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @return returns an Option Reimbursement object
     * @throws SQLException e
     */
    public Optional<Reimbursement> getAReimbByReimbId(Integer reimbId) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where id = :reimbId";
        Query query = session.createQuery(hql);
        query.setParameter("reimbId",reimbId);
        List<Reimbursement> list = query.list();
        session.getTransaction().commit();
        session.close();
        return Optional.of(list.get(0));
    }

    public Optional<Reimbursement> getAReimbByReimbIdAndUserId(Integer reimbId, Integer userId) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where id = :id AND author_id = :author";
        Query query = session.createQuery(hql);

        query.setParameter("id", reimbId);
        query.setParameter("author", userId);

        List<Reimbursement> list = query.list();
        session.getTransaction().commit();
        session.close();
        return Optional.of(list.get(0));
    }

    /**
     * A method to get all of the records for an author given their id
     * @param authorId the ID of the author of the reimbursement
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByAuthorId(Integer authorId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where author_id = :authorId";
        Query query = session.createQuery(hql);
        query.setParameter("authorId",authorId);
        List<Reimbursement> list = query.list();
        List<RbDTO> r_list = mapResultListToDTO(list);
        session.getTransaction().commit();
        session.close();
        return r_list;
    }

    /**
     * A method to get all of the records for an author given their id and filter by status
     * @param authorId the ID of the author of the reimbursement
     * @param reStat the status that the reimbursement is to be set to
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByAuthorIdAndStatus(Integer authorId, ReimbursementStatus reStat) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where author_id = :authorId AND reimbursement_status_id = :restat";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("authorId",authorId);
        query.setParameter("restat",reStat.ordinal());
        return mapResultListToDTO(query.list());
    }

    /**
     * A method to get all of the records for an author given their id and filter by type
     * @param authorId ID of the Author User
     * @param reType the Type to update the record to
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByAuthorIdAndType(Integer authorId, ReimbursementType reType) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where author_id = :authorId AND reimbursement_type_id = :retype";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("authorId",authorId);
        query.setParameter("retype",reType.ordinal());
        return mapResultListToDTO(query.list());
    }

    public List<RbDTO> getAllReimbSetByType(Integer typeId)  {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where reimbursement_type_id = :type";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("type",typeId);
        return mapResultListToDTO(query.list());
    }

    /**
     * A method to get all of the records for a resolver given their id
     * @param resolverId ID of the Resolver User
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByResolverId(Integer resolverId) throws SQLException {
        List<RbDTO> reimbursements = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseQuery + "WHERE er.resolver_id=? order by er.id";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1,resolverId);

            ResultSet rs = ps.executeQuery();

            reimbursements = mapResultSetDTO(rs);
        }
        return reimbursements;
    }

    /**
     * A method to get all of the records for a resolver given their id and filter by status
     * @param resolverId  ID of the Resolver User
     * @param reStat the status to update the record to
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByResolverIdAndStatus(Integer resolverId, ReimbursementStatus reStat) throws SQLException {
        List<RbDTO> reimbursements = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseQuery + "WHERE er.resolver_id=? AND er.reimbursement_status_id=? order by er.id";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1,resolverId);
            ps.setInt(2,reStat.ordinal() + 1);
            ResultSet rs = ps.executeQuery();
            reimbursements = mapResultSetDTO(rs);
        }
        return reimbursements;
    }

    /**
     * A  method to get all of the records for a resolver given their id and filter by type
     * @param resolverId ID of the Resolver User
     * @param reType type of Reimbursements to select by
     * @return a set of reimbursements mapped by the MapResultSet method
     * @throws SQLException e
     */
    public List<RbDTO> getAllReimbSetByResolverIdAndType(Integer resolverId, ReimbursementType reType) throws SQLException {
        List<RbDTO> reimbursements = new ArrayList<>();
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseQuery + "WHERE er.resolver_id=? AND er.reimbursement_type_id=? order by er.id";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1,resolverId);
            ps.setInt(2,reType.ordinal() + 1);
            ResultSet rs = ps.executeQuery();
            reimbursements = mapResultSetDTO(rs);
        }
        return reimbursements;
    }

    //---------------------------------- UPDATE -------------------------------------------- //
    public boolean updateEMP(Reimbursement reimb) {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                    "SET amount=?, description=?, reimbursement_type_id=?\n" +
                    "WHERE id=?\n";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, reimb.getAmount());
            ps.setString(2, reimb.getDescription());
            ps.setInt(3,reimb.getReimbursementType().ordinal() + 1);
            ps.setInt(4,reimb.getId());
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateFIN(User user, Integer statusId, Integer reimbId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Reimbursement reimbursement = getAReimbByReimbId(reimbId).get();
            reimbursement.setReimbursementStatus(ReimbursementStatus.getByNumber(statusId));
            reimbursement.setResolver(user);
            session.update(reimbursement);
            session.getTransaction().commit();
            session.close();
            return true;
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return false;
        }
    }

    /**
     * A method to update only the resolved timestamp by the id of the reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param timestamp an SQL timestamp object to set the time resolved to
     * @return returns true if one and only one record was updated
     * @throws SQLException e
     */
    public boolean updateResolvedTimeStampByReimbId(Integer reimbId, Timestamp timestamp) throws SQLException {

        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                         "SET resolved=?\n" +
                         "WHERE id=?\n";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTimestamp(1,timestamp);
            ps.setInt(2,reimbId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        }
    }

    /**
     * A method to update only the resolver ID by the id of the reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param resolverId the ID of the user that resolves the record to update the record to
     * @return returns true if one and only one record was updated
     * @throws SQLException e
     */
    public boolean updateResolverIdByReimbId(Integer reimbId, Integer resolverId) throws SQLException {

        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                    "SET resolver_id=?\n" +
                    "WHERE id=?\n";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,resolverId);
            ps.setInt(2,reimbId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        }
    }

    /**
     * A method to update only the Reimb. TYPE by the id of the Reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param reimbursementType the type to update the record to
     * @return returns true if one and only one record was updated
     * @throws SQLException e
     */
    public boolean updateReimbursementTypeByReimbId(Integer reimbId, ReimbursementType reimbursementType) throws SQLException {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                    "SET reimbursement_type_id=? " +
                    "WHERE er.id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,reimbursementType.ordinal() + 1);
            ps.setInt(2,reimbId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        }
    }

    /**
     * A method to update the status of a reimbursement in the database
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param newReimbStatus the status to update the record to
     * @return returns true if one and only one record was updated
     * @throws SQLException e
     */
    public boolean updateReimbursementStatusByReimbId(Integer reimbId, ReimbursementStatus newReimbStatus) throws SQLException {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = baseUpdate +
                         "SET reimbursement_status_id=? " +
                         "WHERE er.id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,newReimbStatus.ordinal() + 1);
            ps.setInt(2,reimbId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        }
    }


    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single Reimbursement from the database
     * @param reimbId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     * @throws SQLException e
     */
    public boolean delete(Integer reimbId) throws SQLException {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()) {
            String sql = "DELETE FROM project_1.ers_reimbursements\n" +
                         "WHERE id=? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,reimbId);
            //get the number of affected rows
            int rowsInserted = ps.executeUpdate();
            return rowsInserted != 0;
        }
    }

    //---------------------------------- UTIL -------------------------------------------- //
    /**
     * A method to map the result sets from the reimbursement queries
     * @param rs a resultset
     * @return a set of reimbursements
     * @throws SQLException e
     */
    private Set<Reimbursement> mapResultSet(ResultSet rs) throws SQLException {
        Set<Reimbursement> reimbursements = new HashSet<>();
        while (rs.next()){
            Reimbursement temp = new Reimbursement();
            temp.setId(rs.getInt("id"));
            temp.setAmount(rs.getDouble("amount"));
            temp.setSubmitted(rs.getTimestamp("submitted"));
            temp.setResolved(rs.getTimestamp("resolved"));
            temp.setDescription(rs.getString("description"));
            //temp.setAuthor(rs.getObject("author"));
            //temp.setResolver(rs.getInt("resolver_id"));
            temp.setReimbursementStatus(ReimbursementStatus.getByNumber(rs.getInt("reimbursement_status_id")));
            temp.setReimbursementType(ReimbursementType.getByNumber(rs.getInt("reimbursement_type_id")));

            reimbursements.add(temp);
        }
        return reimbursements;
    }

    private List<RbDTO> mapResultListToDTO(List<Reimbursement> reimbursements) {
        // So I suspect that Object[0] = Reimbursement, Object[1] = Author, Object[2] = Resolver.
        List<RbDTO> reimbs = new ArrayList<>();
        for(Reimbursement objs: reimbursements) {
            RbDTO rbDTO = new RbDTO();
            rbDTO.setId(objs.getId());
            rbDTO.setAmount(objs.getAmount());
            rbDTO.setSubmitted(objs.getSubmitted().toString().substring(0,19));
            rbDTO.setDescription(objs.getDescription());
            rbDTO.setAuthorName(objs.getAuthor().getFirstname()+" "+objs.getAuthor().getLastname());
            rbDTO.setStatus(objs.getReimbursementStatus().toString());
            rbDTO.setType(objs.getReimbursementStatus().toString());

            reimbs.add(rbDTO);
        }

        return reimbs;
    }

    private List<RbDTO> mapResultSetDTO(ResultSet rs) throws SQLException {
        List<RbDTO> reimbs = new ArrayList<>();
        while (rs.next()){
            RbDTO temp = new RbDTO();
            temp.setId(rs.getInt("id"));
            temp.setAmount(rs.getDouble("amount"));
            temp.setSubmitted(rs.getTimestamp("submitted").toString().substring(0,19));
            temp.setDescription(rs.getString("description"));
            temp.setAuthorName(rs.getString("author_first_name") + " " + rs.getString("author_last_name"));
            temp.setStatus(ReimbursementStatus.getByNumber(rs.getInt("reimbursement_status_id")).toString());
            temp.setType(ReimbursementType.getByNumber(rs.getInt("reimbursement_type_id")).toString());
            try {
                temp.setResolved(rs.getTimestamp("resolved").toString().substring(0,19));
                temp.setResolverName(rs.getString("resolver_first_name") + " " + rs.getString("resolver_last_name"));
            } catch (NullPointerException e){
                //If Reimb. has not been resolved DB will return null for these values:
                temp.setResolved("");
                temp.setResolverName("");
            }

            reimbs.add(temp);
        }
        System.out.println(reimbs);
        return reimbs;
    }
}
