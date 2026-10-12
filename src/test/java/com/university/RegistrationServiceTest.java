
package com.university;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationServiceTest {

    @Test
    void shouldRegisterStudentForValidCourse() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS301"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
        assertEquals(1, service.getAuditLog().size());
    }

    @Test
    void shouldRejectInactiveCourse() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, false, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, service.getAuditLog().size());
    }

    @Test
    void shouldRejectMissingPrerequisite() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301"
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS401"));
        assertEquals(0, service.getAuditLog().size());
    }

    @Test
    void shouldAllowRegistrationWhenPrerequisiteIsCompleted() {
        Student student = new Student("S101", "Anita");
        student.addCompletedPrerequisite("CS301");

        Course course = new Course(
                "CS401", "Advanced Algorithms", 4, 30, true, "CS301"
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS401"));
        assertEquals(1, service.getAuditLog().size());
    }

    @Test
    void shouldRejectDuplicateRegistration() {
        Student student = new Student("S101", "Anita");

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();

        assertTrue(service.register(student, course));
        assertEquals(1, service.getAuditLog().size());

        boolean secondAttempt = service.register(student, course);

        assertFalse(secondAttempt);
        assertEquals(4, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
        assertEquals(1, service.getAuditLog().size());
    }

    @Test
    void shouldRejectRegistrationWhenCreditLimitIsExceeded() {
        Student student = new Student("S101", "Anita");

        // Simulate 20 existing credits
        student.addRegistration("CS101", 4);
        student.addRegistration("CS102", 4);
        student.addRegistration("CS201", 4);
        student.addRegistration("CS202", 4);
        student.addRegistration("CS203", 4);

        Course course = new Course(
                "CS301", "Data Structures", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS301"));
        assertEquals(0, service.getAuditLog().size());
    }

    @Test
    void shouldRejectRegistrationWhenCourseIsFull() {
        Student student = new Student("S102", "Rahul");

        Course course = new Course(
                "CS302", "Operating Systems", 4, 1, true, null
        );

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertFalse(result);
        assertFalse(student.isRegistered("CS302"));
        assertEquals(0, student.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
        assertEquals(0, service.getAuditLog().size());
    }

    @Test
    void shouldAllowRegistrationWhenOneSeatRemains() {
        Student student = new Student("S103", "Priya");

        Course course = new Course(
                "CS303", "Computer Networks", 4, 2, true, null
        );

        course.incrementEnrollment();

        RegistrationService service = new RegistrationService();
        boolean result = service.register(student, course);

        assertTrue(result);
        assertTrue(student.isRegistered("CS303"));
        assertEquals(4, student.getTotalCredits());
        assertEquals(2, course.getCurrentEnrollment());
        assertEquals(1, service.getAuditLog().size());
    }

    @Test
    void shouldCreateAuditEntryAfterSuccessfulRegistration() {
        Student student = new Student("S104", "Ananya");

        Course course = new Course(
                "CS304", "Database Systems", 4, 30, true, null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertTrue(result);
        assertEquals(1, service.getAuditLog().size());

        String entry = service.getAuditLog().get(0);

        assertTrue(entry.contains("S104"));
        assertTrue(entry.contains("Ananya"));
        assertTrue(entry.contains("CS304"));
        assertTrue(entry.contains("Database Systems"));
    }

    @Test
    void shouldNotCreateAuditEntryForRejectedRegistration() {
        Student student = new Student("S105", "Rahul");

        Course course = new Course(
                "CS305", "Operating Systems", 4, 1, false, null
        );

        RegistrationService service = new RegistrationService();

        boolean result = service.register(student, course);

        assertFalse(result);
        assertTrue(service.getAuditLog().isEmpty());
    }

    @Test
    void shouldPreserveAuditEntryAfterFailedRegistration() {
        Student firstStudent = new Student("S106", "Priya");

        Course course = new Course(
                "CS306", "Computer Networks", 4, 1, true, null
        );

        RegistrationService service = new RegistrationService();

        // First registration succeeds
        assertTrue(service.register(firstStudent, course));
        assertEquals(1, service.getAuditLog().size());

        String originalEntry = service.getAuditLog().get(0);

        // Second student is rejected because the course is full
        Student secondStudent = new Student("S107", "Rahul");

        assertFalse(service.register(secondStudent, course));

        // The original entry must remain unchanged
        assertEquals(1, service.getAuditLog().size());
        assertEquals(originalEntry, service.getAuditLog().get(0));
        assertFalse(secondStudent.isRegistered("CS306"));
        assertEquals(0, secondStudent.getTotalCredits());
        assertEquals(1, course.getCurrentEnrollment());
    }
}
