import com.revature.services.ReimbursementService;

public class ReimburseServiceTest {

    public static void main(String[] args) {
        ReimbursementService reimbursementService = ReimbursementService.getInstance();

        reimbursementService.getReimbByUserId(15);
    }
}
