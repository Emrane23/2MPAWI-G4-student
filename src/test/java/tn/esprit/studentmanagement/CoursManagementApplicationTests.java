package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import tn.esprit.studentmanagement.StudentManagementApplication; // 👈 import main app

@SpringBootTest
@Transactional // rollback after each test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test") // this will load application-test.properties
class CoursManagementApplicationTests {

    @Test
    void contextLoads() {
        // ✅ If context loads successfully, this passes.
    }
}
