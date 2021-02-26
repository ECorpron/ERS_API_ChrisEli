import com.revature.dtos.RbDTO;
import com.revature.models.Reimbursement;
import com.revature.models.ReimbursementStatus;
import com.revature.models.ReimbursementType;
import com.revature.models.User;
import com.revature.services.ReimbursementService;
import com.revature.services.UserService;

public class ReimburseServiceTest {

    public static void main(String[] args) {
        ReimbursementService reimbursementService = ReimbursementService.getInstance();
        User employee = UserService.getInstance().getAUserById(15);

        RbDTO store = new RbDTO();
        store.setStatus("PENDING");
        store.setAmount(10.00);
        store.setDescription("testing this out");
        store.setType("LODGING");

        reimbursementService.saveRbDTO(employee, store);

        //System.out.println("Number of reimbursements for account 15: "+reimbursementService.getReimbByUserId(15).size());
    }
}
