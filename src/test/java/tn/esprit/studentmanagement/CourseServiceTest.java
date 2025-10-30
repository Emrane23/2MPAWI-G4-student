package tn.esprit.studentmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.repositories.CourseRepository;
import tn.esprit.studentmanagement.services.CourseService;
import tn.esprit.studentmanagement.StudentManagementApplication; // 👈 import main app


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        course = new Course(1L, "Java Basics", "CS101", 3, "Intro to Java", null);
    }

    @Test
    void testGetAllCourses() {
        when(courseRepository.findAll()).thenReturn(Arrays.asList(course));

        List<Course> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getName());
    }

    @Test
    void testGetCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertNotNull(result);
        assertEquals("CS101", result.getCode());
    }

    @Test
    void testAddCourse() {
        when(courseRepository.save(course)).thenReturn(course);

        Course saved = courseService.addCourse(course);

        assertEquals("Java Basics", saved.getName());
        verify(courseRepository, times(1)).save(course);
    }
}
