package edu.utec.cs3081;

import edu.utec.cs3081.calculator.GradeResult;
import edu.utec.cs3081.model.Evaluation;
import edu.utec.cs3081.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Main Tests")
class MainTest {

    @Nested
    @DisplayName("calculateGrade Method Tests")
    class CalculateGradeTests {

        @Test
        @DisplayName("shouldCalculateGradeWithExtraPoints")
        void shouldCalculateGradeWithExtraPoints() {
            Student student = new Student("STU001", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.4));
            student.addEvaluation(new Evaluation("Final", 17.0, 0.6));

            List<Integer> yearsWithExtra = Arrays.asList(2024, 2025);
            GradeResult result = Main.calculateGrade(student, 2025, yearsWithExtra);

            assertNotNull(result);
            assertEquals("STU001", result.getStudentCode());
            double expectedBase = (15.0 * 0.4) + (17.0 * 0.6);
            assertEquals(expectedBase + 2.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCalculateGradeWithoutExtraPoints")
        void shouldCalculateGradeWithoutExtraPoints() {
            Student student = new Student("STU002", true);
            student.addEvaluation(new Evaluation("Parcial", 14.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 16.0, 0.5));

            List<Integer> yearsWithExtra = Arrays.asList(2024, 2025);
            GradeResult result = Main.calculateGrade(student, 2023, yearsWithExtra);

            assertEquals(15.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldReturnZeroWhenNoAttendance")
        void shouldReturnZeroWhenNoAttendance() {
            Student student = new Student("STU003", false);
            student.addEvaluation(new Evaluation("Parcial", 20.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 20.0, 0.5));

            List<Integer> yearsWithExtra = Arrays.asList(2025);
            GradeResult result = Main.calculateGrade(student, 2025, yearsWithExtra);

            assertEquals(0.0, result.getFinalGrade(), 0.001);
            assertTrue(result.isPenalizedByAttendance());
        }

        @Test
        @DisplayName("shouldCalculateWithEmptyExtraPointsList")
        void shouldCalculateWithEmptyExtraPointsList() {
            Student student = new Student("STU004", true);
            student.addEvaluation(new Evaluation("Unica", 18.0, 1.0));

            GradeResult result = Main.calculateGrade(student, 2025, Collections.emptyList());

            assertEquals(18.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCalculateWithNullExtraPointsList")
        void shouldCalculateWithNullExtraPointsList() {
            Student student = new Student("STU005", true);
            student.addEvaluation(new Evaluation("Unica", 16.5, 1.0));

            GradeResult result = Main.calculateGrade(student, 2025, null);

            assertEquals(16.5, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCapGradeAtTwentyWithExtraPoints")
        void shouldCapGradeAtTwentyWithExtraPoints() {
            Student student = new Student("STU006", true);
            student.addEvaluation(new Evaluation("Parcial", 19.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 20.0, 0.5));

            List<Integer> yearsWithExtra = Arrays.asList(2025);
            GradeResult result = Main.calculateGrade(student, 2025, yearsWithExtra);

            assertEquals(20.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldHandleMultipleEvaluations")
        void shouldHandleMultipleEvaluations() {
            Student student = new Student("STU007", true);
            student.addEvaluation(new Evaluation("PC1", 12.0, 0.1));
            student.addEvaluation(new Evaluation("PC2", 14.0, 0.1));
            student.addEvaluation(new Evaluation("PC3", 16.0, 0.1));
            student.addEvaluation(new Evaluation("PC4", 18.0, 0.1));
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.3));
            student.addEvaluation(new Evaluation("Final", 17.0, 0.3));

            GradeResult result = Main.calculateGrade(student, 2023, Collections.emptyList());

            double expected = (12*0.1) + (14*0.1) + (16*0.1) + (18*0.1) + (15*0.3) + (17*0.3);
            assertEquals(expected, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldReturnCorrectDetailsInResult")
        void shouldReturnCorrectDetailsInResult() {
            Student student = new Student("STU008", true);
            student.addEvaluation(new Evaluation("Parcial", 14.0, 0.4));
            student.addEvaluation(new Evaluation("Final", 16.0, 0.6));

            GradeResult result = Main.calculateGrade(student, 2025, Arrays.asList(2025));

            assertNotNull(result.getDetailedReport());
            assertTrue(result.isMeetsAttendance());
            assertFalse(result.isPenalizedByAttendance());
            assertEquals(2, result.getEvaluations().size());
        }
    }
}