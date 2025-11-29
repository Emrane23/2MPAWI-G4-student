package tn.esprit.studentmanagement.services;

import tn.esprit.studentmanagement.entities.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void testCreateAndGetDepartment() {
        Department d = new Department();
        d.setName("Mathematics");
        d.setLocation("Building A");
        d.setHead("Dr. Smith");
        d.setPhone("11122233");

        Department saved = departmentService.saveDepartment(d);
        assertNotNull(saved.getIdDepartment());

        List<Department> departments = departmentService.getAllDepartments();
        assertTrue(departments.size() > 0);

        departmentService.deleteDepartment(saved.getIdDepartment());
    }
}
