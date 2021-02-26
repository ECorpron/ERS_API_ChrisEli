package com.revature.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.revature.dtos.Credentials;
import com.revature.dtos.ErrorResponse;
import com.revature.exceptions.InvalidCredentialsException;
import com.revature.models.User;
import com.revature.services.UserService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {

    public final UserService userService = UserService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        ObjectMapper mapper = new ObjectMapper();
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        try {
            Credentials creds = mapper.readValue(req.getInputStream(), Credentials.class);
            User authUser = userService.authenticate(creds.getUsername(), creds.getPassword());
            writer.write(mapper.writeValueAsString(authUser));
            req.getSession().setAttribute("this-user", authUser);
            resp.setStatus(200);
        } catch (MismatchedInputException | InvalidCredentialsException e) {
            final ErrorResponse err = new ErrorResponse(404,e.getMessage());
            resp.setStatus(404);
            writer.write(mapper.writeValueAsString(err));
        } catch(Exception e) {
            resp.setStatus(418);
            ErrorResponse err = new ErrorResponse(418,e.getMessage());
            writer.write(mapper.writeValueAsString(err));
        }

    }
}
