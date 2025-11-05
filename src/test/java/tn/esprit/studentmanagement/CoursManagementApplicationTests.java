package tn.esprit.studentmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;
import tn.esprit.studentmanagement.StudentManagementApplication; // 👈 import main app

@SpringBootTest
@Transactional // rollback after each test
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CoursManagementApplicationTests {

    @Test
    void contextLoads() {
        // ✅ If context loads successfully, this passes.
    }
}
