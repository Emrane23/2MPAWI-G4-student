package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceImplTestMock {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @Test
    public void testAddEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setGrade(15.5);

        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        Enrollment savedEnrollment = enrollmentService.addEnrollment(enrollment);

        assertNotNull(savedEnrollment);
        assertEquals(15.5, savedEnrollment.getGrade());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    public void testRetrieveEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setIdEnrollment(1L);
        enrollment.setGrade(12.0);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment found = enrollmentService.retrieveEnrollment(1L);

        assertNotNull(found);
        assertEquals(12.0, found.getGrade());
        verify(enrollmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeleteEnrollment() {
        Long id = 2L;
        doNothing().when(enrollmentRepository).deleteById(id);

        enrollmentService.deleteEnrollment(id);

        verify(enrollmentRepository, times(1)).deleteById(id);
    }
}
