package com.revature.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.ApproveDeny;
import com.revature.dtos.ErrorResponse;
import com.revature.dtos.RbDTO;
import com.revature.exceptions.InvalidIdException;
import com.revature.exceptions.NoReimbursementsException;
import com.revature.models.*;
import com.revature.services.ReimbursementService;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet that handles all reimbursement type requests. If sent a get request, the client is attempting to retrieve
 * reimbursement info. If sent a post request, the client is attempting to submit a new reimbursement. If sent a put
 * request, the client is attempting to update a reimbursement.
 */
@WebServlet("/reimburse")
public class ReimbursementServlet extends HttpServlet {

    /**
     * Sent a get request to reimbursement if a client is trying to retrieve reimbursement info.
     * If the client is logged in as an employee, they are trying to get information about their reimbursements.
     * If they are logged in as a Finance manager they are trying to get information about all reimbursements.
     * Clarifying paramaters of reimbursement Id can be added by both finance managers and employees.
     * Finance managers can add type or status parameters to sort reimbursements by type or status.
     * @param req The client request. May hold additional parameters
     * @param resp the server response
     * @throws IOException thrown if there is a problem with the input/output
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        if (rqst != null && rqst.getUserRole() == Role.FINANCE_MANAGER.ordinal()) {
            financeManageDoGet(req, resp, mapper, writer);
            return;
        }
        if(rqst != null && rqst.getUserRole() == Role.EMPLOYEE.ordinal()) {
            employeeDoGet(req, resp, rqst, mapper, writer);
            return;
        }
        final int code = (rqst == null)? 401 : 403;
        final ErrorResponse err = new ErrorResponse(code,"Not authorized.");
        resp.setStatus(code);
        writer.write(mapper.writeValueAsString(err));
    }

    /**
     * A helper method that handles an employees get response. If an employee sends a get, then they are looking for
     * information about their own reimbursements. If there is no id parameter, return all reimbursements the employee
     * has. If there is an id field, return that specific reimbursement
     * @param req the servlet request
     * @param resp the servlet response
     * @param rsqt the user who is requesting information
     * @param mapper the object mapper
     * @param writer the writer who writes information
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void employeeDoGet(HttpServletRequest req, HttpServletResponse resp, User rsqt, ObjectMapper mapper, PrintWriter writer) throws IOException {
        String id = req.getParameter("id");
        try {
            if(id == null) {
                List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByUserId(rsqt.getUserId());
                String usersJSON = mapper.writeValueAsString(reimbursements);
                writer.write(usersJSON);
        } else {
            int reimbursementId;
                reimbursementId = Integer.parseInt(id);
                RbDTO reimb;
                reimb = ReimbursementService.getInstance().getReimbByUserAndReimbId(rsqt.getUserId(), reimbursementId);
                String usersJSON = mapper.writeValueAsString(reimb);
                writer.write(usersJSON);
                resp.setStatus(200);
            }
            resp.setStatus(200);
        }catch(InvalidIdException | NoReimbursementsException  ie) {
            final ErrorResponse err = new ErrorResponse(404,ie.getMessage());
            resp.setStatus(404);
            resp.getWriter().write(mapper.writeValueAsString(err));

        }
    }

    /**
     * Sending a put request means attempting to update a request. If an employee sends it, they are updating a request
     * that they have. If a financial manager sends it, they are attempting to update a specific reimbursement.
     * @param req The client request
     * @param resp the servlet response
     * @throws IOException thrown if there is a problem with the input/output
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        if (rqst != null && rqst.getUserRole() == Role.FINANCE_MANAGER.ordinal()) {
            managerPut(req,resp,rqst,mapper);
            return;
        }
        if(rqst != null && rqst.getUserRole() == Role.EMPLOYEE.ordinal()) {
           employeePut(req,resp,mapper, rqst);
           return;
        }
        final int code = (rqst == null)? 401 : 403;
        final ErrorResponse err = new ErrorResponse(code,"Not authorized to put.");
        resp.setStatus(code);
        writer.write(mapper.writeValueAsString(err));

    }

    /**
     * Sending a post request means attempting to add a new reimbursement. The only people who would do so are
     * employees.
     * @param req The client request
     * @param resp the server response
     * @throws IOException thrown if there is a problem with the input/output
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        if(rqst != null && rqst.getUserRole() == Role.EMPLOYEE.ordinal()) {
            employeePost(req,resp,rqst,mapper);
            return;
        }
        final int code = (rqst == null)? 401 : 403;
        final ErrorResponse err = new ErrorResponse(code,"Not authorized to post.");
        resp.setStatus(code);
        writer.write(mapper.writeValueAsString(err));
    }

    /**
     * A helper method that handles the manager put. A manager uses put to approve or deny a specific reimbursement
     * request. This should be done by sending an ApproveDeny object
     * @param req The client request
     * @param resp the client response
     * @param rqst The user making the request
     * @param mapper the Object mapper
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void managerPut(HttpServletRequest req, HttpServletResponse resp, User rqst,ObjectMapper mapper) throws IOException {
        try {
            ApproveDeny approvedeny = mapper.readValue(req.getInputStream(), ApproveDeny.class);
            if (approvedeny.getStatus() == ReimbursementStatus.APPROVED.ordinal()) {
                ReimbursementService.getInstance().approve(rqst, approvedeny.getId());
                resp.setStatus(200);
                return;
            }
            ReimbursementService.getInstance().deny(rqst,approvedeny.getId());
            resp.setStatus(200);
        } catch (Exception e) {
            final ErrorResponse err = new ErrorResponse(406,e.getMessage());
            resp.setStatus(406);
            resp.getWriter().write(mapper.writeValueAsString(err));
        }
    }


    /**
     * A helper method that handles an employee put. An employee sends a put to update a reimbursement request. This
     * should be done by sending an RbDTO.
     * @param req The client request
     * @param resp the server response
     * @param mapper the object mapper, should contain an RbDTO
     * @param rqst The user making the request
     * @throws IOException thrown if there is a problem with the input/output
     */
private void employeePut(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, User rqst) throws IOException {
        try {
            RbDTO reimbursement = mapper.readValue(req.getInputStream(), RbDTO.class);
            ReimbursementService.getInstance().updateReimbursemntByRbDTO(reimbursement, rqst);
            resp.setStatus(200);
        }catch(Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            resp.getWriter().write(mapper.writeValueAsString(err));
        }
    }


    /**
     * A helper method that handles an employee post request. This is called when an employee wants to submit a new
     * reimbursement request. Should be done by sending an RbDTO
     * @param req The client request
     * @param resp the server response
     * @param mapper The user making the request
     * @param rqst the object mapper, should contain an RbDTO
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void employeePost(HttpServletRequest req,HttpServletResponse resp,User rqst, ObjectMapper mapper) throws IOException{
        try {
            RbDTO reimbursement = mapper.readValue(req.getInputStream(), RbDTO.class);
            ReimbursementService.getInstance().saveRbDTO(rqst,reimbursement);
            resp.setStatus(200);
        }catch(Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            resp.getWriter().write(mapper.writeValueAsString(err));
        }

    }

    /**
     * A helper method that handles a get method that sorts by type
     * @param resp The servlet response
     * @param mapper the object mapper
     * @param writer writes text responses
     * @param type the type of reimbursement to be searched for
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void getReimbursementByTpe(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, String type) throws IOException{
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByType(ReimbursementType.valueOf(type).ordinal());
        try {
            String usersJson = mapper.writeValueAsString(reimbursements);
            writer.write(usersJson);
            resp.setStatus(200);
        } catch (Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            writer.write(mapper.writeValueAsString(err));
        }
    }

    /**
     * A helper method that handles searching for reimbursement by status
     * @param resp The server response
     * @param mapper the object mapper
     * @param writer writes text responses
     * @param status the status to be searched for
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void getReimbursementByStatus(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, String status) throws IOException {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByStatus(ReimbursementStatus.valueOf(status).ordinal());
        try {
            String usersJson = mapper.writeValueAsString(reimbursements);
            writer.write(usersJson);
            resp.setStatus(200);
        } catch (Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            writer.write(mapper.writeValueAsString(err));
        }
    }


    /**
     * A helper method that handles grabbing all reimbursements.
     * @param resp the server responce
     * @param mapper the object mapper
     * @param writer writes text responses
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void getAllReimbursements(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer)throws IOException  {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getAllReimb();
        try {
            String usersJSON = mapper.writeValueAsString(reimbursements);
            writer.write(usersJSON);
            resp.setStatus(200);
        } catch (Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            writer.write(mapper.writeValueAsString(err));
        }
    }

    /**
     * A helper method that grabs a specific reimbursement by its id
     * @param resp the server response
     * @param mapper the object mapper
     * @param writer the object writer
     * @param id the id of the reimbursement
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void getSpecificReimbursement(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, Integer id) throws IOException{
        RbDTO reimbursements = ReimbursementService.getInstance().getReimbByReimbId(id);
        try {
            String usersJSON = mapper.writeValueAsString(reimbursements);
            writer.write(usersJSON);
            resp.setStatus(200);
        } catch (Exception e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            writer.write(mapper.writeValueAsString(err));
        }
    }

    /**
     * A helper method that handles a finance manager get. If they get, they could be looking for all reimbursements,
     * a specific reimbursement, or a reimbursement by status or type.
     * @param req the server request
     * @param resp the server resposne
     * @param mapper the object mapper
     * @param writer writes text responses
     * @throws IOException thrown if there is a problem with the input/output
     */
    private void financeManageDoGet(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer) throws IOException{
        String id = req.getParameter("id");
        String type = req.getParameter("type");
        String status = req.getParameter("status");
        try {
            if (type != null && !"".equals(type.trim())) {
                getReimbursementByTpe(resp, mapper, writer, type);
                return;
            }
            if (status != null && !"".equals(status.trim())) {
                getReimbursementByStatus(resp, mapper, writer, status);
                resp.setStatus(200);
                return;
            }
            if (id == null) {
                getAllReimbursements(resp, mapper, writer);
            } else {
                int reimbursementId;
                try {
                    reimbursementId = Integer.parseInt(id);
                } catch (NumberFormatException n) {
                    final ErrorResponse err = new ErrorResponse(401,n.getMessage());
                    resp.setStatus(401);
                    resp.getWriter().write(mapper.writeValueAsString(err));
                    return;
                }
                getSpecificReimbursement(resp, mapper, writer, reimbursementId);
            }
        }catch(InvalidIdException | NoReimbursementsException re) {
            final ErrorResponse err = new ErrorResponse(406,re.getMessage());
            resp.setStatus(406);
            resp.getWriter().write(mapper.writeValueAsString(err));
        } catch(Exception e) {
            resp.setStatus(418);
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            writer.write(mapper.writeValueAsString(err));
        }
    }
}