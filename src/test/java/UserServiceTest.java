import com.revature.models.Role;
import com.revature.models.User;
import com.revature.services.UserService;

public class UserServiceTest {
    public static void main(String[] args) {
        UserService service = UserService.getInstance();

        User user = new User();
        user.setEmail("here@test.com");
        user.setFirstname("Me");
        user.setLastname("Mine");
        user.setUsername("myself");
        user.setPassword("always");
        user.setUserRole(Role.EMPLOYEE.ordinal());

        service.register(user);
    }
}
