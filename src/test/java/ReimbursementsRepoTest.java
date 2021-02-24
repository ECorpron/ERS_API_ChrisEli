import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.repositories.ReimbursementsRepository;

import java.util.List;

public class ReimbursementsRepoTest {

    public static void main(String[] args) {
        ReimbursementsRepository repo = new ReimbursementsRepository();

        User user = new User();
        user.setEmail("testEmail@test.com");
        user.setFirstname("test");
        user.setLastname("user");
        user.setUsername("test");
        user.setPassword("password");

        Reimbursement reimbursement = new Reimbursement();
        reimbursement.setAuthor(user);
        reimbursement.setReimbursementType(ReimbursementType.LODGING);
        reimbursement.setReimbursementStatus(ReimbursementStatus.PENDING);
        reimbursement.setAmount(100.0);

        System.out.println("New user added: "+repo.addReimbursement(reimbursement));

        List<RbDTO> list =  repo.getAllReimbursements();
        System.out.println("The first Reimbursement author is: "+list.get(0).getAuthorName());

        List<RbDTO> list2 = repo.getAllReimbSetByStatus(1);
        System.out.println("The number of Reimbursement authors with id 1 is: "+list2.size());

        List<RbDTO> list3 = repo.getAllReimbSetByStatus(0);
        System.out.println("The first Reimbursement author with id 0 is: "+list3.size());


    }
}
