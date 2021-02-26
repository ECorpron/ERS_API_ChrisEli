import com.revature.services.ReimbursementService;

public class ReimburseServiceTest {

    public static void main(String[] args) {
        ReimbursementService reimbursementService = ReimbursementService.getInstance();

        System.out.println("Number of reimbursements for account 15: "+reimbursementService.getReimbByUserId(15).size());
    }
}
