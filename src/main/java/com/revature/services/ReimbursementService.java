package com.revature.services;

import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for validating reimbursements before sending to or from the Database
 */
public class ReimbursementService {
    private final ReimbursementsRepository reimbRepo = new ReimbursementsRepository();
    private final static ReimbursementService reimbService = new ReimbursementService();



    private ReimbursementService() {
        super();
    }


    public static ReimbursementService getInstance() {
        return reimbService;
    }

    /**
     * Gets all Reimbursements from the DataBase
     * @return A list of RbDTO objects
     */
    public List<RbDTO> getAllReimb(){
        List<RbDTO> reimbursements = reimbRepo.getAllReimbursements();

        if (reimbursements.isEmpty()){
            throw new RuntimeException();
        }
        return reimbursements;
    }

    /**
     * Gets all reimbursements for a usre given their Id
     * @param userId user id requested
     * @return A list of RbDTO objects
     */
    public List<RbDTO> getReimbByUserId(Integer userId){
        if (userId <= 0){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<RbDTO> reimb = new ArrayList<>();
        reimb = reimbRepo.getAllReimbSetByAuthorId(userId);
        return reimb;
    }

    public RbDTO getReimbByReimbId(Integer userId){
        if (userId <= 0){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        RbDTO reimb = null;
        try {
            Optional<Reimbursement> temp = reimbRepo.getAReimbByReimbId(userId);
            if (temp.isPresent()) {
                reimb = reimbursementToRbDTO(temp.get());
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return reimb;
    }

    public RbDTO getReimbByUserAndReimbId(int userId, int reimbId) {
        try {
            Optional<Reimbursement> temp = reimbRepo.getAReimbByReimbIdAndUserId(userId, reimbId);

            if (temp.isPresent()) {
                return reimbursementToRbDTO(temp.get());
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Gets all reimbursements by a specified type
     * @param typeId ordinal number of the type requested, between 1-4
     * @return A list of RbDTO objects
     */
    public List<RbDTO> getReimbByType(Integer typeId){
        if (typeId <= 0 || typeId >=5){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<RbDTO> reimb = reimbRepo.getAllReimbSetByType(typeId);
        if (reimb.isEmpty()){
            throw new RuntimeException();
        }
        return reimb;
    }

    /**
     * Gets all reimbursements by a specified status
     * @param statusId ordinal number of the type requested, between 1-3
     * @return A list of RbDTO objects
     */
    public List<RbDTO> getReimbByStatus(Integer statusId){
        if (statusId <= 0 || statusId >= 4){
            throw new RuntimeException("THE PROVIDED USER ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        List<RbDTO> reimb = reimbRepo.getAllReimbSetByStatus(statusId);
        if (reimb.isEmpty()){
            throw new RuntimeException();
        }
        return reimb;
    }

    /**
     * Saves a reimbursement after validation
     * @param reimb the completed reimbursement object
     */
    public void save(Reimbursement reimb){
        if (!isReimbursementValid(reimb)){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.addReimbursement(reimb)){
            throw new RuntimeException("Something went wrong trying to save this reimbursement");
        }
    }

    public void saveRbDTO(User user, RbDTO rbdto) {
        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAuthor(user);
        reimbursement.setAmount(rbdto.getAmount());
        reimbursement.setDescription(rbdto.getDescription());
        reimbursement.setReceipt(rbdto.getImage());
        reimbursement.setReimbursementType(ReimbursementType.valueOf(rbdto.getType()));
        reimbursement.setReimbursementStatus(ReimbursementStatus.valueOf(rbdto.getStatus()));
        save(reimbursement);
    }

    /**
     * Update a reimbursement
     * @param reimb the completed reimbursement object
     */
    public void updateEMP(Reimbursement reimb) {
        if (!isReimbursementValid(reimb)){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateEMP(reimb)){
            throw new RuntimeException("Something went wrong trying to save this reimbursement");
        }
        System.out.println(reimb);
    }

    public void updateReimbursemntByRbDTO(RbDTO reimb) {
        try {
            Optional<Reimbursement> reimbursement = reimbRepo.getAReimbByReimbId(reimb.getId());
            if(reimbursement.isPresent()) {
                Reimbursement updated_reimb = reimbursement.get();
                updated_reimb.setAmount(reimb.getAmount());
                updated_reimb.setDescription(reimb.getDescription());
                updated_reimb.setReimbursementType(ReimbursementType.valueOf(reimb.getType()));
                updated_reimb.setReimbursementStatus(ReimbursementStatus.valueOf(reimb.getStatus()));
                reimbRepo.updateEMP(updated_reimb);
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Approve a Reimb.
     * @param user the user of the fin manager resolving the reimb.
     * @param reimbId id of the Reimb. to approve or disapprove.
     */
    public void approve(User user, Integer reimbId) {
        if (reimbId <= 0 || user.getUserId() <=0){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateFIN(user, 2, reimbId)){
            throw new RuntimeException("Something went wrong trying to approve this reimbursement");
        }
    }

    /**
     * Deny a reimb.
     * @param user the user of the fin manager resolving the reimb.
     * @param reimbId id of the Reimb. to approve or disapprove.
     */
    public void deny(User user, Integer reimbId) {
        if (reimbId <= 0){
            throw new RuntimeException("Invalid user field values provided!");
        }
        if(!reimbRepo.updateFIN(user, 3, reimbId)){
            throw new RuntimeException("Something went wrong trying to deny this reimbursement");
        }
    }

    private RbDTO reimbursementToRbDTO(Reimbursement reimb) {
        RbDTO rbDTO = new RbDTO();

        rbDTO.setStatus(reimb.getReimbursementStatus().name());
        rbDTO.setId(reimb.getId());
        rbDTO.setDescription(reimb.getDescription());
        rbDTO.setAmount(reimb.getAmount());
        rbDTO.setSubmitted(reimb.getSubmitted().toString());
        rbDTO.setType(reimb.getReimbursementType().name());
        rbDTO.setAuthorName(reimb.getAuthor().getFirstname()+" "+reimb.getAuthor().getLastname());
        rbDTO.setImage(reimb.getReceipt());

        if (reimb.getResolved() != null) {
            rbDTO.setResolved(reimb.getResolved().toString());
        }
        if (reimb.getResolver() != null) {
            rbDTO.setResolverName(reimb.getResolver().getFirstname() + " " + reimb.getResolver().getLastname());
        }

        return rbDTO;
    }

    /**
     * Validates feilds of a reimbursement
     * @param reimb reimb. to be validated
     * @return true or false based on fields
     */
    public boolean isReimbursementValid(Reimbursement reimb){
        if (reimb == null) return false;
        if (reimb.getAmount() == null || reimb.getAmount() <= 0 ) return false;
        if (reimb.getDescription() == null || reimb.getDescription().trim().equals("")) return false;
        if (reimb.getAuthor() == null ) return false;
        if (reimb.getReimbursementType() == null ) return false;
        return true;
    }


}
