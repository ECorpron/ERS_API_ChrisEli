package com.revature.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.ApproveDeny;
import com.revature.dtos.Credentials;
import com.revature.dtos.RbDTO;
import com.revature.models.*;
import com.revature.services.ReimbursementService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class ReimbursementServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        String userIdParam = req.getParameter("userId");
        if (rqst != null && rqst.getUserRole() == Role.FINANCE_MANAGER.ordinal()) {
            financeManageDoGet(req, resp, mapper, writer);
            return;
        }
        if(rqst != null && rqst.getUserId() == Role.EMPLOYEE.ordinal()) {
            employeeDoGet(req, resp, rqst, mapper, writer);
            return;
        }
        resp.setStatus(401);
    }

    private void employeeDoGet(HttpServletRequest req, HttpServletResponse resp, User rsqt, ObjectMapper mapper, PrintWriter writer) {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByUserId(rsqt.getUserId());
        try {
            String usersJSON = mapper.writeValueAsString(reimbursements);
            writer.write(usersJSON);
            resp.setStatus(200);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        if (rqst != null && rqst.getUserRole() == Role.FINANCE_MANAGER.ordinal()) {
            managerPut(req,resp,rqst,mapper);
            return;
        }
        if(rqst != null && rqst.getUserId() == Role.EMPLOYEE.ordinal()) {
           employeePut(req,resp,mapper);
           return;
        }
        resp.setStatus(404);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        if(rqst != null && rqst.getUserId() == Role.EMPLOYEE.ordinal()) {
            employeePost(req,resp,rqst,mapper);
            return;
        }
        resp.setStatus(404);

    }



    private void managerPut(HttpServletRequest req, HttpServletResponse resp, User rqst,ObjectMapper mapper) {
        try {
            ApproveDeny approvedeny = mapper.readValue(req.getInputStream(), ApproveDeny.class);
            if (approvedeny.getStatus() == ReimbursementStatus.APPROVED.ordinal()) {
                ReimbursementService.getInstance().approve(rqst, approvedeny.getId());
                resp.setStatus(200);
                return;
            }
            ReimbursementService.getInstance().deny(rqst,approvedeny.getId());
            resp.setStatus(200);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }

    private void employeePut(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper) {
        try {
            RbDTO reimbursement = mapper.readValue(req.getInputStream(), RbDTO.class);
            ReimbursementService.getInstance().updateReimbursemntByRbDTO(reimbursement);
            resp.setStatus(200);
            return;
        }catch(Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }


    private void employeePost(HttpServletRequest req,HttpServletResponse resp,User rqst, ObjectMapper mapper) {
        try {
            RbDTO reimbursement = mapper.readValue(req.getInputStream(), RbDTO.class);
            ReimbursementService.getInstance().saveRbDTO(rqst,reimbursement);
            resp.setStatus(200);
            return;
        }catch(Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }

    }

    private void getReimbursementByTpe(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, String type) {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByType(ReimbursementType.valueOf(type).ordinal());
        try {
            String usersJson = mapper.writeValueAsString(reimbursements);
            writer.write(usersJson);
            resp.setStatus(200);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }

    private void getReimbursementByStatus(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer, String status) {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getReimbByStatus(ReimbursementStatus.valueOf(status).ordinal());
        try {
            String usersJson = mapper.writeValueAsString(reimbursements);
            writer.write(usersJson);
            resp.setStatus(200);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }

    private void getAllReimbursements(HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer) {
        List<RbDTO> reimbursements = ReimbursementService.getInstance().getAllReimb();
        try {
            String usersJSON = mapper.writeValueAsString(reimbursements);
            writer.write(usersJSON);
            resp.setStatus(200);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(404);
            return;
        }
    }

    private void financeManageDoGet(HttpServletRequest req, HttpServletResponse resp, ObjectMapper mapper, PrintWriter writer) {
        String reimbursement = req.getParameter("reimburseAccount");
        if (reimbursement == null || !"".equals(reimbursement.trim())) {
            getAllReimbursements(resp,mapper, writer);
            return;
        }
        String type = req.getParameter("type");
        if (type != null && !"".equals(type.trim())) {
            getReimbursementByTpe(resp, mapper, writer, type);
            return;
        }
        String status = req.getParameter("status");
        if (status != null && !"".equals(status.trim())) {
            getReimbursementByStatus(resp,mapper,writer,status);
            return;
        }

    }
}