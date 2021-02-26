package com.revature.repositories;

import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.services.UserService;
import com.revature.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.sql.*;
import java.util.*;

/**
 * A class to interact with the database to CRUD reimbursement objects
 */
public class ReimbursementsRepository {
    private static final Logger logger = LogManager.getLogger(ReimbursementsRepository.class);

    public ReimbursementsRepository(){
        super();
    }

    //---------------------------------- CREATE -------------------------------------------- //
    /**
     * Adds a reimbursement to the database, Does not handle Images!
     * @param reimbursement the reimbursement to be added to the DB
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
            logger.error(e.getStackTrace());
            return false;
        }

        session.getTransaction().commit();
        session.close();

        return true;
    }

    //---------------------------------- READ -------------------------------------------- //

    /**
     * Returns a list of RbDTOs that represent all reimbursements stored in the database
     * @return a list of RbDTOs that represent all of the the reimbursements stored in the database
     */
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

    /**
     * Gets a list of RbDTOs of all reimbursements that have a specific status Id
     * @param statusId the status to sort reimbursements by when grabbed
     * @return returns a list of RbDTOs of all reimbursements that have the inputted status Id
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByStatus(Integer statusId){
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
     * @throws SQLException Throws an SQLException if there was a problem executing the given statement
     */
    @SuppressWarnings("unchecked")
    public Optional<Reimbursement> getAReimbByReimbId(Integer reimbId) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where id = :reimbId";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("reimbId",reimbId);
        List<Reimbursement> list = query.list();
        session.getTransaction().commit();
        session.close();
        return Optional.of(list.get(0));
    }

    /**
     * Gets a specific reimbursement that has the given author Id and reimbursement Id.
     * @param userId The userId to be searched for
     * @param reimbId the reimbursement Id to search for
     * @return returns an Optional reimbursement
     * @throws SQLException Throws an SQLException if there was a problem executing the given statement
     */
    @SuppressWarnings("unchecked")
    public Optional<Reimbursement> getAReimbByReimbIdAndUserId(int userId, int reimbId) throws SQLException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where id = :id AND author_id = :author";
        Query<Reimbursement> query = session.createQuery(hql);

        query.setParameter("id", reimbId);
        query.setParameter("author", userId);

        List<Reimbursement> list = query.list();
        session.getTransaction().commit();
        session.close();
        if(list.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    /**
     * A method to get all of the records for an author given their id
     * @param authorId the ID of the author of the reimbursement
     * @return a set of reimbursements mapped by the MapResultSet method
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByAuthorId(Integer authorId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where author_id = :authorId";
        Query<Reimbursement> query = session.createQuery(hql);
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
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByAuthorIdAndStatus(Integer authorId, ReimbursementStatus reStat){
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
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByAuthorIdAndType(Integer authorId, ReimbursementType reType){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where author_id = :authorId AND reimbursement_type_id = :retype";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("authorId",authorId);
        query.setParameter("retype",reType.ordinal());
        return mapResultListToDTO(query.list());
    }

    /**
     * Gets a list of RbDTOs that are all reimbursements that have a specific type
     * @param typeId the type id to filter reimbursements by
     * @return returns a list of RbDTO that are all reimbursements with the specified type
     */
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
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByResolverId(Integer resolverId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where resolver_id = :id";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("resolver_id",resolverId);
        return mapResultListToDTO(query.list());
    }

    /**
     * A method to get all of the records for a resolver given their id and filter by status
     * @param resolverId  ID of the Resolver User
     * @param reStat the status to update the record to
     * @return a set of reimbursements mapped by the MapResultSet method
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByResolverIdAndStatus(Integer resolverId, ReimbursementStatus reStat){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where resolver_id = :id AND reimbursement_status_id = :restat";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("resolver_id",resolverId);
        query.setParameter("restat",reStat);
        return mapResultListToDTO(query.list());
    }

    /**
     * A  method to get all of the records for a resolver given their id and filter by type
     * @param resolverId ID of the Resolver User
     * @param reType type of Reimbursements to select by
     * @return a set of reimbursements mapped by the MapResultSet method
     */
    @SuppressWarnings("unchecked")
    public List<RbDTO> getAllReimbSetByResolverIdAndType(Integer resolverId, ReimbursementType reType){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        String hql = "FROM Reimbursement where resolver_id = :id AND reimbursement_type_id = :retype";
        Query<Reimbursement> query = session.createQuery(hql);
        query.setParameter("resolver_id",resolverId);
        query.setParameter("retype",reType.ordinal());
        return mapResultListToDTO(query.list());
    }

    //---------------------------------- UPDATE -------------------------------------------- //

    /**
     * Takes in a reimbursement and updates a stored reimbursement
     * @param reimb the reimbursement instance to save
     * @return returns true if it was stored, false if it was not
     */
    public boolean updateEMP(Reimbursement reimb) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            session.merge(reimb);
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

    /**
     * Updates a spcified reimbursement to either be approved or denied
     * @param user The user who is approving or denying the reimbursement
     * @param statusId the status id to update to
     * @param reimbId the id of the reimbursement being updating
     * @return returns true if it was updated, false if the reimbursement was not updated.
     */
    public boolean updateFIN(User user, Integer statusId, Integer reimbId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if (reimbursement.isPresent()) {
                reimbursement.get().setReimbursementStatus(ReimbursementStatus.getByNumber(statusId));
                reimbursement.get().setResolver(user);
                session.update(reimbursement.get());
            } else {
                throw new RuntimeException();
            }
            session.getTransaction().commit();
            session.close();
            return true;
        }catch(Exception e) {
            logger.error(e.getStackTrace());
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
     */
    public boolean updateResolvedTimeStampByReimbId(Integer reimbId, Timestamp timestamp){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if(reimbursement.isPresent()) {
                reimbursement.get().setResolved(timestamp);
                session.merge(reimbursement);
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }catch(Exception e) {
            logger.error(e.getStackTrace());
            session.getTransaction().rollback();
        }
        session.close();
        return false;
    }

    /**
     * A method to update only the resolver ID by the id of the reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param resolverId the ID of the user that resolves the record to update the record to
     * @return returns true if one and only one record was updated
     */
    public boolean updateResolverIdByReimbId(Integer reimbId, Integer resolverId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if(reimbursement.isPresent()) {
                User resolver = UserService.getInstance().getAUserById(resolverId);
                reimbursement.get().setResolver(resolver);
                session.merge(reimbursement);
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }catch(Exception e) {
            logger.error(e.getStackTrace());
            session.getTransaction().rollback();
        }
        session.close();
        return false;
    }

    /**
     * A method to update only the Reimb. TYPE by the id of the Reimbursement
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param reimbursementType the type to update the record to
     * @return returns true if one and only one record was updated
     */
    public boolean updateReimbursementTypeByReimbId(Integer reimbId, ReimbursementType reimbursementType){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if(reimbursement.isPresent()) {
                reimbursement.get().setReimbursementType(reimbursementType);
                session.merge(reimbursement);
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }catch(Exception e) {
            logger.error(e.getStackTrace());
            session.getTransaction().rollback();
        }
        session.close();
        return false;
    }

    /**
     * A method to update the status of a reimbursement in the database
     * @param reimbId The ID of the reimbursement in the database that is requested
     * @param newReimbStatus the status to update the record to
     * @return returns true if one and only one record was updated
     */
    public boolean updateReimbursementStatusByReimbId(Integer reimbId, ReimbursementStatus newReimbStatus){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if(reimbursement.isPresent()) {
                reimbursement.get().setReimbursementStatus(newReimbStatus);
                session.merge(reimbursement);
                session.getTransaction().commit();
                session.close();
                return true;
            }
        }catch(Exception e) {
            logger.error(e.getStackTrace());
            session.getTransaction().rollback();
        }
        session.close();
        return false;
    }


    //---------------------------------- DELETE -------------------------------------------- //

    /**
     * A method to delete a single Reimbursement from the database
     * @param reimbId the ID of the record to be deleted
     * @return returns true if one and only one record is updated
     */
    public boolean delete(Integer reimbId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.getTransaction().commit();
        try {
            Optional<Reimbursement> reimbursement = getAReimbByReimbId(reimbId);
            if(reimbursement.isPresent()) {
                session.remove(reimbursement);
                session.getTransaction().commit();
                return true;
            }
        }catch(Exception e) {
            logger.error(e.getStackTrace());
            session.getTransaction().rollback();
        }
        session.close();
        return false;
    }

    //---------------------------------- UTIL -------------------------------------------- //

    /**
     * A helper method that converts a list of reimbursements into a list of RbDTOs.
     * @param reimbursements the list of reimbursements to be converted
     * @return returns a list of RbDTOs that represent the reimbursements.
     */
    private List<RbDTO> mapResultListToDTO(List<Reimbursement> reimbursements) {
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
}
