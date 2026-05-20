package com.movetogether.modules.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EnrollmentFactory {

    private final EnrollmentRepository enrollmentRepository;

    public Enrollment createEnrollment(boolean accepted) {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrolledAt(LocalDateTime.now());
        enrollment.setAccepted(accepted);
        enrollment.setAttended(false);
        return enrollment;
    }
}
