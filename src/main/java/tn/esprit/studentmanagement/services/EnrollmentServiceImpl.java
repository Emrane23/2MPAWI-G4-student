package tn.esprit.studentmanagement.services;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.studentmanagement.entities.Enrollment;
import tn.esprit.studentmanagement.repositories.EnrollmentRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class EnrollmentServiceImpl extends EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public Enrollment addEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment retrieveEnrollment(Long id) {
        Optional<Enrollment> enrollment = enrollmentRepository.findById(id);
        return enrollment.orElse(null);
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
