package tn.esprit.studentmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.studentmanagement.controllers.CourseController;
import tn.esprit.studentmanagement.entities.Course;
import tn.esprit.studentmanagement.services.CourseService;
import tn.esprit.studentmanagement.StudentManagementApplication; // 👈 import main app

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTestMosc {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllCourses() throws Exception {
        Course course = new Course(1L, "Java", "CS101", 3, "Intro to Java", null);
        Mockito.when(courseService.getAllCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Java"));
    }

    @Test
    void testAddCourse() throws Exception {
        Course course = new Course(1L, "Spring Boot", "CS202", 4, "Advanced Spring", null);
        Mockito.when(courseService.addCourse(Mockito.any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spring Boot"));
    }
}
