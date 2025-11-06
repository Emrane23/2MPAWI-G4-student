package tn.esprit.studentmanagement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.studentmanagement.entities.Student;
import tn.esprit.studentmanagement.repositories.StudentRepository;
import tn.esprit.studentmanagement.services.StudentService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository; // ✅ MOCK du Repository

    @InjectMocks
    private StudentService studentService; // ✅ Service réel injecté avec le mock

    // Helper method pour créer un student de test
    private Student createTestStudent() {
        return new Student(1L, "John", "Doe", "john.doe@email.com", 
                          "12345678", LocalDate.of(2000, 1, 1), "123 Main St", null, null);
    }

    @Test
    void testGetAllStudents() {
        // Given - Arrange
        Student student1 = createTestStudent();
        Student student2 = new Student(2L, "Jane", "Smith", "jane.smith@email.com",
                                     "87654321", LocalDate.of(1999, 5, 15), "456 Oak St", null, null);
        
        List<Student> expectedStudents = Arrays.asList(student1, student2);
        when(studentRepository.findAll()).thenReturn(expectedStudents);

        // When - Act
        List<Student> result = studentService.getAllStudents();

        // Then - Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(studentRepository, times(1)).findAll(); // Vérifie que findAll() a été appelé 1 fois
    }

    @Test
    void testGetStudentById_Found() {
        // Given
        Long studentId = 1L;
        Student expectedStudent = createTestStudent();
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(expectedStudent));

        // When
        Student result = studentService.getStudentById(studentId);

        // Then
        assertNotNull(result);
        assertEquals(studentId, result.getIdStudent());
        assertEquals("John", result.getFirstName());
        verify(studentRepository).findById(studentId);
    }

    @Test
    void testGetStudentById_NotFound() {
        // Given
        Long studentId = 999L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When
        Student result = studentService.getStudentById(studentId);

        // Then
        assertNull(result);
        verify(studentRepository).findById(studentId);
    }

    @Test
    void testSaveStudent() {
        // Given
        Student studentToSave = createTestStudent();
        when(studentRepository.save(studentToSave)).thenReturn(studentToSave);

        // When
        Student result = studentService.saveStudent(studentToSave);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(studentRepository).save(studentToSave);
    }

    @Test
    void testDeleteStudent() {
        // Given
        Long studentId = 1L;
        doNothing().when(studentRepository).deleteById(studentId);

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository, times(1)).deleteById(studentId);
    }
}