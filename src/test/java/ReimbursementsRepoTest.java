import com.revature.dtos.RbDTO;
import com.revature.repositories.ReimbursementsRepository;

import java.util.List;

public class ReimbursementsRepoTest {

    public static void main(String[] args) {
        ReimbursementsRepository repo = new ReimbursementsRepository();

        List<RbDTO> list =  repo.getAllReimbursements();
    }
}
