package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // this will load application-test.properties
class StudentManagementApplicationTests {

    @Test
    void contextLoads() {
        // if this passes, your Spring context and DB connection are working
    }
}
