package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.entities.Status;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    // Helper
    private Enrollment createEnrollment() {
        Student student = new Student();
        student.setIdStudent(1L);

        Course course = new Course();
        course.setIdCourse(1L);

        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(1L);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(15.5);
        enrollment.setStatus(Status.ACTIVE);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return enrollment;
    }

    @Test
    void testGetAllEnrollments() {
        Enrollment e1 = createEnrollment();
        Enrollment e2 = createEnrollment();
        e2.setIdEnrollment(2L);

        when(enrollmentRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertEquals(2, result.size());
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    void testGetEnrollmentById_Found() {
        Enrollment enrollment = createEnrollment();
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment result = enrollmentService.getEnrollmentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdEnrollment());
    }

    @Test
    void testGetEnrollmentById_NotFound() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        Enrollment result = enrollmentService.getEnrollmentById(99L);

        assertNull(result);
    }

    @Test
    void testSaveEnrollment() {
        Enrollment enrollment = createEnrollment();

        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        Enrollment result = enrollmentService.saveEnrollment(enrollment);

        assertNotNull(result);
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void testDeleteEnrollment() {
        doNothing().when(enrollmentRepository).deleteById(1L);

        enrollmentService.deleteEnrollment(1L);

        verify(enrollmentRepository, times(1)).deleteById(1L);
    }
}
