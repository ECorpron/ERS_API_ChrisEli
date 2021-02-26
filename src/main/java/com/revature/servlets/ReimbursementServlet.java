package com.revature.servlets;

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


@WebServlet("/reimburse")
public class ReimbursementServlet extends HttpServlet {


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
            }
            resp.setStatus(200);
        }catch(InvalidIdException | NoReimbursementsException ie) {
            final ErrorResponse err = new ErrorResponse(404,ie.getMessage());
            resp.setStatus(404);
            resp.getWriter().write(mapper.writeValueAsString(err));

        }
    }

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



    private void managerPut(HttpServletRequest req, HttpServletResponse resp, User rqst,ObjectMapper mapper) throws IOException{
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


    private void employeePost(HttpServletRequest req,HttpServletResponse resp,User rqst, ObjectMapper mapper) throws IOException {
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

    private void getReimbursementByTpe(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, String type) throws IOException  {
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

    private void getAllReimbursements(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer) throws IOException {
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

    private void financeManageDoGet(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer)throws IOException {
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