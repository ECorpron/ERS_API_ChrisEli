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
        //User employee = UserService.getInstance().getAUserById(15);
        User financialManager = UserService.getInstance().getAUserById(16);

        RbDTO store = new RbDTO();
        store.setId(4);
        //store.setStatus("PENDING");
        store.setAmount(1000.00);
        store.setDescription("that's a lot of money!");
        store.setType("LODGING");

        //reimbursementService.saveRbDTO(employee, store);
        //System.out.println(reimbursementService.getReimbByUserAndReimbId(15, 8).toString());
        //System.out.println("Number of reimbursements for account 15: "+reimbursementService.getReimbByUserId(15).size());
        //reimbursementService.updateReimbursemntByRbDTO(store, employee);
        reimbursementService.deny(financialManager, 1);

    }
}
