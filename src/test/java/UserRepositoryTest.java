import com.revature.models.Role;
import com.revature.models.User;
import com.revature.repositories.UserRepository;
import com.revature.services.UserService;

public class UserRepositoryTest {

    public static void main(String[] args) {
        UserRepository repo = new UserRepository();

        User user = new User();
        user.setEmail("dc@test.com");
        user.setFirstname("Douglas");
        user.setLastname("Corpron");
        user.setUsername("douglas");
        user.setPassword("corpron");
        user.setUserRole(Role.EMPLOYEE.ordinal());

        System.out.println("Saved a new user: "+repo.addUser(user));

        System.out.println("The name of the user with this account is: "+
                repo.getAUserByUsernameAndPassword("douglas", "corpron").get().getFirstname());
    }
}
