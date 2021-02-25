import com.revature.repositories.UserRepository;

public class UserRepositoryTest {

    public static void main(String[] args) {
        UserRepository repo = new UserRepository();

        System.out.println("The name of the user with this account is: "+
                repo.getAUserByUsernameAndPassword("test", "password").get().getFirstname());
    }
}
