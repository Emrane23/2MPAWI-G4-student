package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;
import tn.esprit.studentmanagement.services.StudentService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService; // ✅ Service RÉEL

    @Autowired
    private StudentRepository studentRepository; // ✅ Repository RÉEL

    @Test
    void testSaveAndRetrieveStudent() {
        // Given - Création d'un student
        Student student = new Student();
        student.setFirstName("Integration");
        student.setLastName("Test");
        student.setEmail("integration.test@email.com");
        student.setPhone("11111111");
        student.setDateOfBirth(LocalDate.of(1998, 3, 10));
        student.setAddress("789 Test St");

        // When - Sauvegarde RÉELLE en base
        Student savedStudent = studentService.saveStudent(student);
        
        // Then - Vérification RÉELLE depuis la base
        assertNotNull(savedStudent.getIdStudent());
        
        Student retrievedStudent = studentService.getStudentById(savedStudent.getIdStudent());
        assertNotNull(retrievedStudent);
        assertEquals("Integration", retrievedStudent.getFirstName());
        assertEquals("Test", retrievedStudent.getLastName());

        // Cleanup - Nettoyage de la base
        studentService.deleteStudent(savedStudent.getIdStudent());
    }

    @Test
    void testGetAllStudents_Integration() {
        // Given - Sauvegarde de plusieurs étudiants RÉELS
        Student student1 = new Student(null, "Alice", "Johnson", "alice@email.com", 
                                     "22222222", LocalDate.of(1997, 7, 20), "101 Alice St", null, null);
        Student student2 = new Student(null, "Bob", "Brown", "bob@email.com", 
                                     "33333333", LocalDate.of(1996, 12, 5), "202 Bob St", null, null);
        
        Student saved1 = studentService.saveStudent(student1);
        Student saved2 = studentService.saveStudent(student2);

        // When - Récupération RÉELLE
        List<Student> students = studentService.getAllStudents();

        // Then - Vérification
        assertTrue(students.size() >= 2);
        assertTrue(students.stream().anyMatch(s -> s.getFirstName().equals("Alice")));
        assertTrue(students.stream().anyMatch(s -> s.getFirstName().equals("Bob")));

        // Cleanup
        studentService.deleteStudent(saved1.getIdStudent());
        studentService.deleteStudent(saved2.getIdStudent());
    }
}