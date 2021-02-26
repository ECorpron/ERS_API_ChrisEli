import com.revature.models.Role;
import com.revature.models.User;
import com.revature.services.UserService;

public class UserServiceTest {
    public static void main(String[] args) {
        UserService service = UserService.getInstance();

//        User user = new User();
//        user.setEmail("updated");
//        user.setFirstname("Me");
//        user.setLastname("Mine");
//        user.setUsername("updatedUsername3");
//        user.setPassword("always");
//        user.setUserRole(Role.EMPLOYEE.ordinal());
//        user.setUserId(9);

        User user = service.getAUserById(17);
        user.setUsername("Admin");
        user.setUserRole(1d);

        //service.register(user);
        service.update(user);
    }
}
