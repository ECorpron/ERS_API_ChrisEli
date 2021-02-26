package com.revature.services;

import com.revature.exceptions.*;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Constitutes the SERVICE LAYER for users. concerned with validating all user
 * input before being sent to the database. Uses a Singleton model
 */
public class UserService {
    private UserRepository userRepo = new UserRepository();
    private static UserService userService = new UserService();

    /**
     * A getter for the user service instance
     * @return returns the instance of the user service
     */
    public static UserService getInstance() {
        return userService;
    }

    /**
     * Gets all users from the DataBase
     * @return A list of Users
     */
    public List<User> getAllUsers() {
        List<User> users = userRepo.getAllusers();
        if (users.isEmpty()){
            throw new NoUsersException("sorry, but no users found.");
        }
        return users;
    }

    /**
     * Gets a user by the specified id. Throws an error if no user is found.
     * @param userId the User id to search a user for
     * @return returns a user if one is found
     * @throws UserNotPresentException throws an error if no corresponding user is found
     */
    public User getAUserById(int userId) throws UserNotPresentException{
        Optional<User> user = userRepo.getAUserByUserId(userId);
        if(!user.isPresent()) {
            throw new UserNotPresentException("Sorry, but no user exists with the Id " + userId);
        }
        return user.get();
    }

    /**
     * Authentication method used by the authentication servlet
     * @param username username of the user
     * @param password password of the user
     * @return the object of the requested user
     */
    public User authenticate(String username, String password) {
        if (username == null || username.trim().equals("") || password == null || password.trim().equals("")){
            throw new InvalidCredentialsException("username: " + username +" password: " + password + " is invalid.");
        }
        return userRepo.getAUserByUsernameAndPassword(username,password)
                .orElseThrow(RuntimeException::new);
    }

    /**
     * Register a new user in the DB. validates all fields first
     * @param newUser completed user object
     */
    public void register(User newUser) {
        if (!isUserValid(newUser)) {
            throw new InvalidCredentialsException("Invalid user field values provided during registration!");
        }
        Optional<User> existingUser = userRepo.getAUserByUsername(newUser.getUsername());
        if (existingUser.isPresent()) {
            throw new FieldNotUniqueException("Username is already in use");
        }
        Optional<User> existingUserEmail = userRepo.getAUserByEmail(newUser.getEmail());
        if (existingUserEmail.isPresent()) {
            throw new FieldNotUniqueException("Email is already in use");
        }
        newUser.setUserRole(Role.EMPLOYEE.ordinal());
        userRepo.addUser(newUser);
    }

    /**
     * Update a user in the DB.
     * @param newUser user to update
     */
    public void update(User newUser) {
        if (!isUserValid(newUser)) {
            throw new InvalidUserFieldsException("Invalid user field values provided during registration!");
        }
        if (!userRepo.updateAUser(newUser)){
            throw new UpdateObjectException("There was a problem trying to update the user");
        }
    }

    /**
     * Deletes a user by changing their role to 4
     * @param id id of user to delete
     * @return true if role was updated in db
     */
    public boolean deleteUserById(int id) {
        if (id <= 0){
            throw new InvalidIdException("THE PROVIDED ID CANNOT BE LESS THAN OR EQUAL TO ZERO");
        }
        return userRepo.deleteAUserById(id);
    }

    /**
     * Method for simple checking of availability of username
     * @param username username to chek
     * @return true if available
     */
    public boolean isUsernameAvailable(String username) {
        User user = userRepo.getAUserByUsername(username).orElse(null);
        return user == null;
    }

    /**
     * Method for simple checking of availability of email
     * @param email the email to be searched for
     * @return true if available
     */
    public boolean isEmailAvailable(String email) {
        User user = userRepo.getAUserByEmail(email).orElse(null);
        return user == null;
    }

    /**
     * Validates that the given user and its fields are valid (not null or empty strings). Does
     * not perform validation on id or role fields.
     *
     * @param user the user to verify has valid fields
     * @return true or false depending on if the user was valid or not
     */
    public boolean isUserValid(User user) {
        if (user == null) return false;
        if (user.getFirstname() == null || user.getFirstname().trim().equals("")) return false;
        if (user.getLastname() == null || user.getLastname().trim().equals("")) return false;
        if (user.getUsername() == null || user.getUsername().trim().equals("")) return false;
        if (user.getPassword() == null || user.getPassword().trim().equals("")) return false;
        return true;
    }
}
