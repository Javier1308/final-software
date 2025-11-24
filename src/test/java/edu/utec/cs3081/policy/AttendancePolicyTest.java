package edu.utec.cs3081.policy;

import edu.utec.cs3081.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AttendancePolicy Tests")
class AttendancePolicyTest {

    private AttendancePolicy policy;

    @BeforeEach
    void setUp() {
        policy = new AttendancePolicy();
    }

    @Nested
    @DisplayName("Verificación de Asistencia Mínima")
    class AttendanceVerification {

        @Test
        @DisplayName("shouldReturnTrueWhenStudentMeetsAttendance")
        void shouldReturnTrueWhenStudentMeetsAttendance() {
            Student student = new Student("STU001", true);
            assertTrue(policy.meetsMinimumAttendance(student));
        }

        @Test
        @DisplayName("shouldReturnFalseWhenStudentDoesNotMeetAttendance")
        void shouldReturnFalseWhenStudentDoesNotMeetAttendance() {
            Student student = new Student("STU002", false);
            assertFalse(policy.meetsMinimumAttendance(student));
        }

        @Test
        @DisplayName("shouldReturnFalseWhenStudentIsNull")
        void shouldReturnFalseWhenStudentIsNull() {
            assertFalse(policy.meetsMinimumAttendance(null));
        }
    }

    @Nested
    @DisplayName("Aplicación de Política")
    class PolicyApplication {

        @Test
        @DisplayName("shouldReturnOriginalGradeWhenAttendanceMet")
        void shouldReturnOriginalGradeWhenAttendanceMet() {
            Student student = new Student("STU003", true);
            double result = policy.applyPolicy(student, 15.0);
            assertEquals(15.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldReturnZeroWhenAttendanceNotMet")
        void shouldReturnZeroWhenAttendanceNotMet() {
            Student student = new Student("STU004", false);
            double result = policy.applyPolicy(student, 20.0);
            assertEquals(0.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldReturnZeroWhenStudentIsNull")
        void shouldReturnZeroWhenStudentIsNull() {
            double result = policy.applyPolicy(null, 15.0);
            assertEquals(0.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Descripción de Política")
    class PolicyDescription {

        @Test
        @DisplayName("shouldReturnPositiveDescriptionWhenAttendanceMet")
        void shouldReturnPositiveDescriptionWhenAttendanceMet() {
            Student student = new Student("STU005", true);
            String description = policy.getPolicyDescription(student);
            assertTrue(description.contains("Cumple"));
        }

        @Test
        @DisplayName("shouldReturnPenaltyDescriptionWhenAttendanceNotMet")
        void shouldReturnPenaltyDescriptionWhenAttendanceNotMet() {
            Student student = new Student("STU006", false);
            String description = policy.getPolicyDescription(student);
            assertTrue(description.contains("No cumple"));
            assertTrue(description.contains("0.00") || description.contains("0,00"));
        }
    }
}
