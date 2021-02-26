package UserServiceTest;
import com.revature.exceptions.*;
import com.revature.models.Role;
import com.revature.models.User;
import com.revature.services.UserService;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


public class UserServiceTest {
    UserService mockservice = mock(UserService.class);
    static User admin;
    static User manager;
    static User employee;
    static int adminid;

    @BeforeClass
    public static void makeUser() {
        admin = new User();
        admin.setUserRole(Role.ADMIN.ordinal());
        admin.setFirstname("chris");
        admin.setLastname("nichols");
        admin.setPassword("password");
        admin.setEmail("myemail@emailservice.com");
        admin.setUsername("canicho");

        manager = new User();
        manager.setUserRole(Role.FINANCE_MANAGER.ordinal());
        manager.setFirstname("jim");
        manager.setLastname("wales");
        manager.setPassword("password");
        manager.setEmail("jwales@emailservice.com");
        manager.setUsername("jwales");

        employee = new User();
        employee.setUserRole(Role.EMPLOYEE.ordinal());
        employee.setFirstname("bob");
        employee.setLastname("smith");
        employee.setPassword("password");
        employee.setEmail("bobsmithl@emailservice.com");
        employee.setUsername("bsmith");

        adminid = admin.getUserId();
    }

    @Test
    public void testAdd() {
        mockservice.register(admin);
        mockservice.register(manager);
        mockservice.register(employee);
    }

    @Test(expected = FieldNotUniqueException.class)
    public void testAddNonUniqueUser() {
        mockservice.register(admin);
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testInvalidUserRegister() {
        User user = new User();
        user.setFirstname("");
        mockservice.register(user);
    }

    @Test
    public void testIsUserValid() {
        assertTrue(mockservice.isUserValid(admin));
        User user = new User();
        user.setUsername("");
        assertFalse(mockservice.isUserValid(user));
    }

    @Test
    public void testSameUsername() {
        assertFalse(mockservice.isUsernameAvailable(admin.getUsername()));
    }

    @Test
    public void testSameEmail() {
        assertFalse(mockservice.isEmailAvailable(admin.getEmail()));
    }

    @Test
    public void testGetUserById() {
        assertNotNull(mockservice.getAUserById(admin.getUserId()));
    }

    @Test(expected = UserNotPresentException.class)
    public void testGetUserByIdFail() {
        mockservice.getAUserById(1234567890);
    }

    @Test
    public void testAuthenticate() {
        assertNotNull(mockservice.authenticate("canicho","password"));
    }

    @Test(expected = InvalidCredentialsException.class)
    public void testAuthenticateFail() {
        mockservice.authenticate("","");
    }

    @Test
    public void testUpdateAdminUserName() {
        admin.setUsername("canicho2");
        mockservice.update(admin);
    }

    @Test(expected = InvalidUserFieldsException.class)
    public void testUpdateUserFail() {
        User user = new User();
        user.setFirstname("");
        mockservice.update(user);
    }

    @Test
    public void testDeleteUsers() {
        mockservice.deleteUserById(admin.getUserId());
        mockservice.deleteUserById(manager.getUserId());
        mockservice.deleteUserById(employee.getUserId());
    }

    @Test(expected = InvalidIdException.class)
    public void testDelectIdZero() {
        mockservice.deleteUserById(0);
    }

    @Test(expected = Exception.class)
    public void testDeleteNonExistantUser() {
        mockservice.deleteUserById(adminid);
    }

    @Test
    public void testGetAlUsers() {
        assertTrue(mockservice.getAllUsers().size() > 0);
    }

}
