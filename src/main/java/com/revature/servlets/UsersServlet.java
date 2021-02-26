package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.ErrorResponse;
import com.revature.exceptions.FieldNotUniqueException;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.services.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {

    private final UserService userService = UserService.getInstance();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        try {
            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                ErrorResponse err = new ErrorResponse(401,"Not authorized to post.")
                resp.setStatus(401);
                writer.write(mapper.writeValueAsString(err));
                return;
            }
            User newUser = mapper.readValue(req.getInputStream(),User.class);
            userService.register(newUser);
            resp.setStatus(200);
        } catch (FieldNotUniqueException fnu){
            ErrorResponse err = new ErrorResponse(409,fnu.getMessage());
            resp.setStatus(409); //conflict
            writer.write(mapper.writeValueAsString(err));
        } catch(InvalidCredentialsException ice) {
            resp.setStatus(409);
            ErrorResponse err = new ErrorResponse(409,ice.getMessage());
            writer.write(mapper.writeValueAsString(err));
        }catch(Exception e) {
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            resp.setStatus(418);
            writer.write(mapper.writeValueAsString(err));
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        try {

            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                resp.setStatus(401);
                return;
            }
            User updateUser = mapper.readValue(req.getInputStream(),User.class);
            userService.update(updateUser);
            resp.setStatus(200);
        } catch (RuntimeException e){
            e.printStackTrace();
            resp.setStatus(409); //conflict
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");

        try {

            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                resp.setStatus(401);
                return;
            }

            User toDelete = mapper.readValue(req.getInputStream(),User.class);
            if (userService.deleteUserById(toDelete.getUserId())) {
                resp.setStatus(200);
            } else {
                resp.setStatus(404);
            }


        } catch (RuntimeException e){
            e.printStackTrace();
            resp.setStatus(409); //conflict
        }
    }
}
