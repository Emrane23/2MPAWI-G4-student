package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;
import tn.esprit.studentmanagement.repositories.StudentRepository;
import tn.esprit.studentmanagement.repositories.CourseRepository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class EnrollmentServiceIntegrationTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void testSaveAndRetrieveEnrollment() {
        // Student
        Student student = new Student();
        student.setFirstName("Integration");
        student.setLastName("Test");
        student.setEmail("test@esprit.tn");
        student = studentRepository.save(student);

        // Course
        Course course = new Course();
        course.setName("Spring Boot");
        course = courseRepository.save(course);

        // Enrollment
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(18.0);
        enrollment.setStatus(Status.ACTIVE);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        Enrollment saved = enrollmentService.saveEnrollment(enrollment);

        assertNotNull(saved.getIdEnrollment());

        Enrollment retrieved = enrollmentService.getEnrollmentById(saved.getIdEnrollment());

        assertEquals(18.0, retrieved.getGrade());
        assertEquals(Status.ACTIVE, retrieved.getStatus());
    }

    @Test
    void testGetAllEnrollments_Integration() {
        Enrollment e1 = new Enrollment(null, LocalDate.now(), 12.0, Status.ACTIVE, null, null);
        Enrollment e2 = new Enrollment(null, LocalDate.now(), 14.0, Status.ACTIVE, null, null);

        Enrollment saved1 = enrollmentRepository.save(e1);
        Enrollment saved2 = enrollmentRepository.save(e2);

        List<Enrollment> list = enrollmentService.getAllEnrollments();

        assertTrue(list.size() >= 2);

        enrollmentRepository.deleteById(saved1.getIdEnrollment());
        enrollmentRepository.deleteById(saved2.getIdEnrollment());
    }
}
