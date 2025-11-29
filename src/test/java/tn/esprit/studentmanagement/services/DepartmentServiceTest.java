package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.studentmanagement.entities.Department;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepartmentServiceTest {

    @Autowired
    private DepartmentService departmentService;

    private static Long savedDepartmentId;

    @Test
    @Order(1)
    public void testCreateDepartment() {
        System.out.println("=== Testing Department Creation ===");
        
        // Create new department
        Department department = new Department();
        department.setName("Computer Science");
        department.setLocation("Building A");
        department.setPhone("123-456-7890");
        department.setHead("Dr. Smith");
        
        // Save department 
        Department savedDepartment = departmentService.saveDepartment(department);
        
        // Verify it was saved
        assertNotNull(savedDepartment.getIdDepartment(), "Department ID should not be null");
        assertEquals("Computer Science", savedDepartment.getName());
        assertEquals("Building A", savedDepartment.getLocation());
        
        // Save ID for later tests
        savedDepartmentId = savedDepartment.getIdDepartment();
        
        System.out.println("✅ Department created with ID: " + savedDepartmentId);
    }

    @Test
    @Order(2)
    public void testGetDepartmentById() {
        System.out.println("=== Testing Get Department By ID ===");
        
        // Get department by ID
        Department foundDepartment = departmentService.getDepartmentById(savedDepartmentId);
        
        // Verify we found it
        assertNotNull(foundDepartment, "Department should be found");
        assertEquals("Computer Science", foundDepartment.getName());
        assertEquals("Dr. Smith", foundDepartment.getHead());
        
        System.out.println("✅ Department retrieved: " + foundDepartment.getName());
    }

    @Test
    @Order(3)
    public void testGetAllDepartments() {
        System.out.println("=== Testing Get All Departments ===");
        
        // Get all departments.
        List<Department> departments = departmentService.getAllDepartments();
        
        // Verify we have departments
        assertNotNull(departments, "Departments list should not be null");
        assertTrue(departments.size() > 0, "Should have at least one department");
        
        System.out.println("✅ Total departments found: " + departments.size());
        departments.forEach(dept -> System.out.println("   - " + dept.getName() + " (ID: " + dept.getIdDepartment() + ")"));
    }

    @Test
    @Order(4)
    public void testUpdateDepartment() {
        System.out.println("=== Testing Department Update ===");
        
        // Get the department
        Department department = departmentService.getDepartmentById(savedDepartmentId);
        assertNotNull(department, "Department should exist for update");
        
        // Update it
        department.setName("Updated Computer Science");
        department.setLocation("Building B");
        department.setHead("Dr. Johnson");
        
        Department updatedDepartment = departmentService.saveDepartment(department);
        
        // Verify update
        assertEquals("Updated Computer Science", updatedDepartment.getName());
        assertEquals("Building B", updatedDepartment.getLocation());
        assertEquals("Dr. Johnson", updatedDepartment.getHead());
        
        System.out.println("✅ Department updated to: " + updatedDepartment.getName());
    }

    @Test
    @Order(5)
    public void testDeleteDepartment() {
        System.out.println("=== Testing Department Deletion ===");
        
        // Delete the department 
        departmentService.deleteDepartment(savedDepartmentId);
        
        System.out.println("✅ Department deletion method called successfully");
        
        
    }
}