package tn.esprit.studentmanagement.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Status;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class EnrollmentServiceImplTest {

    @Autowired
    private EnrollmentServiceImpl enrollmentService;

    @Test
    @Order(1)
    public void testAddEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(16.0);
        enrollment.setStatus(Status.ACTIVE);

        Enrollment saved = enrollmentService.addEnrollment(enrollment);
        log.info("Added Enrollment: " + saved);

        assertNotNull(saved);
        assertNotNull(saved.getIdEnrollment());
        assertEquals(16.0, saved.getGrade());
    }

    @Test
    @Order(2)
    public void testRetrieveEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(13.5);
        enrollment.setStatus(Status.PENDING);

        Enrollment saved = enrollmentService.addEnrollment(enrollment);
        Enrollment retrieved = enrollmentService.retrieveEnrollment(saved.getIdEnrollment());

        log.info("Retrieved Enrollment: " + retrieved);
        assertNotNull(retrieved);
        assertEquals(saved.getGrade(), retrieved.getGrade());
    }

    @Test
    @Order(3)
    public void testDeleteEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(10.0);
        enrollment.setStatus(Status.CANCELLED);

        Enrollment saved = enrollmentService.addEnrollment(enrollment);
        Long id = saved.getIdEnrollment();

        enrollmentService.deleteEnrollment(id);
        Enrollment deleted = enrollmentService.retrieveEnrollment(id);

        log.info("Deleted Enrollment with ID: " + id);
        assertNull(deleted);
    }
}
