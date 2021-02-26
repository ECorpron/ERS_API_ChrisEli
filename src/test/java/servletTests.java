import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.Role;
import com.revature.models.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class servletTests {
    public static void main(String[] args) {
        String id = "";
        int test = Integer.parseInt(id);
        System.out.println(test);

//        ObjectMapper mapper = new ObjectMapper();
//
//        User user = new User();
//        user.setEmail("dc@test.com");
//        user.setFirstname("Douglas");
//        user.setLastname("Corpron");
//        user.setUsername("douglas");
//        user.setPassword("corpron");
//        user.setUserRole(Role.EMPLOYEE.ordinal());
//
//        String jsonString  = "{\"userId\":4,\"username\":\"test\",\"password\":\"password\",\"firstname\":\"test\"," +
//                "\"lastname\":\"user\",\"email\":\"testEmail@test.com\",\"userRole\":1}";
//
//        InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
//
//        try {
//            User newUser = mapper.readValue(stream,User.class);
//            System.out.println(newUser.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
