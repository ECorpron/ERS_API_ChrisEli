package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.dtos.ErrorResponse;
import com.revature.exceptions.*;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet that handles users requests. The only people accessing the user tables will be admins. post request means
 * creating a new user, a put request means updating a user, a delete request means deleting a user.
 */
@WebServlet("/users/*")
public class UsersServlet extends HttpServlet {

    private final UserService userService = UserService.getInstance();
    private static final Logger logger = LogManager.getLogger(UsersServlet.class);

    /**
     * Admin can create a new User account. It will be initiated as an Employee.
     * @param req the client request
     * @param resp the server response
     * @throws IOException thrown when a problem is encountered with the input/output
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        try {
            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                final int code = (rqst == null)? 401 : 403;
                ErrorResponse err = new ErrorResponse(code,"Not authorized to post.");
                resp.setStatus(code);
                logger.info(err.toString());
                writer.write(mapper.writeValueAsString(err));
                return;
            }
            User newUser = mapper.readValue(req.getInputStream(),User.class);
            userService.register(newUser);
            resp.setStatus(200);
        } catch (FieldNotUniqueException | InvalidCredentialsException fnu){
            ErrorResponse err = new ErrorResponse(409,fnu.getMessage());
            resp.setStatus(409);
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        } catch(Exception e) {
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            resp.setStatus(418);
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        }

    }

    /**
     * Admin can update an account
     * @param req the client request
     * @param resp the server response
     * @throws IOException thrown when a problem is encountered with the input/output
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        try {
            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                final int code = (rqst == null)? 401 : 403;
                ErrorResponse err = new ErrorResponse(code,"Not authorized to post.");
                resp.setStatus(code);
                logger.info(err.toString());
                writer.write(mapper.writeValueAsString(err));
                return;
            }
            User updateUser = mapper.readValue(req.getInputStream(),User.class);
            userService.update(updateUser);
            resp.setStatus(200);
        } catch (InvalidUserFieldsException | UpdateObjectException iufe){
            resp.setStatus(409);
            ErrorResponse err = new ErrorResponse(409,iufe.getMessage());
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        } catch(Exception e) {
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            resp.setStatus(418);
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        }
    }

    /**
     * Admin can delete an account
     * @param req the client request
     * @param resp the server response
     * @throws IOException thrown when a problem is encountered with the input/output
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper mapper = new ObjectMapper();
        HttpSession session = req.getSession(false);
        User rqst = (session == null) ? null : (User) req.getSession(false).getAttribute("this-user");
        resp.setContentType("application/json");
        try {
            if (rqst == null || rqst.getUserRole() != Role.ADMIN.ordinal()) {
                final int code = (rqst == null)? 401 : 403;
                ErrorResponse err = new ErrorResponse(code,"Not authorized to post.");
                resp.setStatus(code);
                logger.info(err.toString());
                writer.write(mapper.writeValueAsString(err));
                return;
            }
            User toDelete = mapper.readValue(req.getInputStream(),User.class);
            if (userService.deleteUserById(toDelete.getUserId())) {
                resp.setStatus(200);
            } else {
                ErrorResponse err = new ErrorResponse(404,"Not able to delete user");
                resp.setStatus(404);
                logger.info(err.toString());
                writer.write(mapper.writeValueAsString(err));
            }
        } catch (InvalidIdException ie){
            resp.setStatus(409);
            ErrorResponse err = new ErrorResponse(409,ie.getMessage());
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        } catch(Exception e) {
            resp.setStatus(418);
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            logger.info(err.toString());
            writer.write(mapper.writeValueAsString(err));
        }
    }
}
