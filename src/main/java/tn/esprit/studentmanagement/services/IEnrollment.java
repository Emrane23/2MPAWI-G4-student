package tn.esprit.studentmanagement.services;

import tn.esprit.studentmanagement.entities.Enrollment;
import java.util.List;

public interface IEnrollment {
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long idEnrollment);
    Enrollment saveEnrollment(Enrollment enrollment);
    Enrollment addEnrollment(Enrollment enrollment);
    Enrollment retrieveEnrollment(Long idEnrollment);
    void deleteEnrollment(Long idEnrollment);
}
