import com.revature.dtos.RbDTO;
import com.revature.models.*;
import com.revature.repositories.ReimbursementsRepository;
import com.revature.repositories.UserRepository;

import java.util.List;

public class ReimbursementsRepoTest {

    public static void main(String[] args) {
        ReimbursementsRepository repo = new ReimbursementsRepository();
        UserRepository userRepo = new UserRepository();

        User user = new User();
        user.setEmail("asdefnewEmail@test.com");
        user.setFirstname("test");
        user.setLastname("user");
        user.setUsername("asdnew");
        user.setPassword("password");
        user.setUserRole(Role.ADMIN.ordinal());

        System.out.println("Added a new user: "+userRepo.addUser(user));

        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAuthor(user);
        reimbursement.setReimbursementType(ReimbursementType.LODGING);
        reimbursement.setReimbursementStatus(ReimbursementStatus.PENDING);
        reimbursement.setAmount(100.0);
        reimbursement.setReceipt(new byte[]{1,0});

        System.out.println("New reimbursement added: "+repo.addReimbursement(reimbursement));

        List<RbDTO> list =  repo.getAllReimbursements();
        System.out.println("The first Reimbursement author is: "+list.get(0).getAuthorName());

        List<RbDTO> list2 = repo.getAllReimbSetByStatus(1);
        System.out.println("The number of Reimbursement authors with id 1 is: "+list2.size());

        List<RbDTO> list3 = repo.getAllReimbSetByStatus(0);
        System.out.println("The first Reimbursement author with id 0 is: "+list3.size());

    }
}
