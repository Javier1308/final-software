package edu.utec.cs3081.calculator;

import edu.utec.cs3081.exception.GradeCalculationException;
import edu.utec.cs3081.model.Evaluation;
import edu.utec.cs3081.model.Student;
import edu.utec.cs3081.policy.AttendancePolicy;
import edu.utec.cs3081.policy.ExtraPointsPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GradeCalculator Tests")
class GradeCalculatorTest {

    private GradeCalculator calculator;
    private ExtraPointsPolicy extraPointsPolicy;

    @BeforeEach
    void setUp() {
        extraPointsPolicy = new ExtraPointsPolicy(Arrays.asList(2024, 2025));
        calculator = new GradeCalculator(new AttendancePolicy(), extraPointsPolicy);
    }

    @Nested
    @DisplayName("Cálculo Normal")
    class NormalCalculation {

        @Test
        @DisplayName("shouldReturnWeightedAverageWhenStudentHasValidEvaluations")
        void shouldReturnWeightedAverageWhenStudentHasValidEvaluations() {
            Student student = new Student("STU001", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.4));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.6));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            double expectedAverage = (15.0 * 0.4) + (18.0 * 0.6);
            assertEquals(expectedAverage, result.getWeightedAverage(), 0.001);
        }

        @Test
        @DisplayName("shouldReturnCorrectFinalGradeWhenNoExtraPoints")
        void shouldReturnCorrectFinalGradeWhenNoExtraPoints() {
            Student student = new Student("STU002", true);
            student.addEvaluation(new Evaluation("Parcial", 14.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 16.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(15.0, result.getFinalGrade(), 0.001);
            assertEquals(0.0, result.getExtraPointsApplied(), 0.001);
        }

        @Test
        @DisplayName("shouldReturnDeterministicResultWhenSameInputs")
        void shouldReturnDeterministicResultWhenSameInputs() {
            Student student1 = createStudentWithEvaluations("STU003", true, 
                new Evaluation("E1", 12.0, 0.5), 
                new Evaluation("E2", 14.0, 0.5));
            
            Student student2 = createStudentWithEvaluations("STU003", true, 
                new Evaluation("E1", 12.0, 0.5), 
                new Evaluation("E2", 14.0, 0.5));

            GradeResult result1 = calculator.calculateFinalGrade(student1, 2024);
            GradeResult result2 = calculator.calculateFinalGrade(student2, 2024);

            assertEquals(result1.getFinalGrade(), result2.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCalculateCorrectlyWithMultipleEvaluations")
        void shouldCalculateCorrectlyWithMultipleEvaluations() {
            Student student = new Student("STU004", true);
            student.addEvaluation(new Evaluation("PC1", 15.0, 0.2));
            student.addEvaluation(new Evaluation("PC2", 16.0, 0.2));
            student.addEvaluation(new Evaluation("Parcial", 14.0, 0.3));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.3));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            double expected = (15.0 * 0.2) + (16.0 * 0.2) + (14.0 * 0.3) + (18.0 * 0.3);
            assertEquals(expected, result.getWeightedAverage(), 0.001);
        }
    }

    @Nested
    @DisplayName("Caso Sin Asistencia Mínima")
    class NoMinimumAttendance {

        @Test
        @DisplayName("shouldReturnZeroWhenStudentDidNotMeetAttendance")
        void shouldReturnZeroWhenStudentDidNotMeetAttendance() {
            Student student = new Student("STU005", false);
            student.addEvaluation(new Evaluation("Parcial", 20.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 20.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2024);

            assertEquals(0.0, result.getFinalGrade(), 0.001);
            assertTrue(result.isPenalizedByAttendance());
            assertFalse(result.isMeetsAttendance());
        }

        @Test
        @DisplayName("shouldNotApplyExtraPointsWhenNoAttendance")
        void shouldNotApplyExtraPointsWhenNoAttendance() {
            Student student = new Student("STU006", false);
            student.addEvaluation(new Evaluation("Parcial", 18.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 19.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertEquals(0.0, result.getFinalGrade(), 0.001);
            assertEquals(0.0, result.getExtraPointsApplied(), 0.001);
        }
    }

    @Nested
    @DisplayName("Caso Con y Sin Puntos Extra")
    class ExtraPointsTests {

        @Test
        @DisplayName("shouldApplyExtraPointsWhenYearIsConfigured")
        void shouldApplyExtraPointsWhenYearIsConfigured() {
            Student student = new Student("STU007", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 15.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertEquals(17.0, result.getFinalGrade(), 0.001);
            assertEquals(2.0, result.getExtraPointsApplied(), 0.001);
        }

        @Test
        @DisplayName("shouldNotApplyExtraPointsWhenYearIsNotConfigured")
        void shouldNotApplyExtraPointsWhenYearIsNotConfigured() {
            Student student = new Student("STU008", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 15.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(15.0, result.getFinalGrade(), 0.001);
            assertEquals(0.0, result.getExtraPointsApplied(), 0.001);
        }

        @Test
        @DisplayName("shouldCapGradeAtTwentyWhenExtraPointsExceed")
        void shouldCapGradeAtTwentyWhenExtraPointsExceed() {
            Student student = new Student("STU009", true);
            student.addEvaluation(new Evaluation("Parcial", 19.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 20.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertEquals(20.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldWorkWithoutExtraPointsPolicy")
        void shouldWorkWithoutExtraPointsPolicy() {
            GradeCalculator calcWithoutExtra = new GradeCalculator();
            Student student = new Student("STU010", true);
            student.addEvaluation(new Evaluation("Parcial", 16.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.5));

            GradeResult result = calcWithoutExtra.calculateFinalGrade(student, 2025);

            assertEquals(17.0, result.getFinalGrade(), 0.001);
        }
    }

    @Nested
    @DisplayName("Casos Borde")
    class EdgeCases {

        @Test
        @DisplayName("shouldThrowExceptionWhenStudentIsNull")
        void shouldThrowExceptionWhenStudentIsNull() {
            assertThrows(GradeCalculationException.class, 
                () -> calculator.calculateFinalGrade(null, 2024));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenNoEvaluations")
        void shouldThrowExceptionWhenNoEvaluations() {
            Student student = new Student("STU011", true);

            assertThrows(GradeCalculationException.class, 
                () -> calculator.calculateFinalGrade(student, 2024));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenWeightsDoNotSumToOne")
        void shouldThrowExceptionWhenWeightsDoNotSumToOne() {
            Student student = new Student("STU012", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.3));
            student.addEvaluation(new Evaluation("Final", 15.0, 0.3));

            assertThrows(GradeCalculationException.class, 
                () -> calculator.calculateFinalGrade(student, 2024));
        }

        @Test
        @DisplayName("shouldAcceptWeightsWithSmallTolerance")
        void shouldAcceptWeightsWithSmallTolerance() {
            Student student = new Student("STU013", true);
            student.addEvaluation(new Evaluation("E1", 15.0, 0.333));
            student.addEvaluation(new Evaluation("E2", 15.0, 0.333));
            student.addEvaluation(new Evaluation("E3", 15.0, 0.334));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertNotNull(result);
        }

        @Test
        @DisplayName("shouldHandleZeroGrades")
        void shouldHandleZeroGrades() {
            Student student = new Student("STU014", true);
            student.addEvaluation(new Evaluation("Parcial", 0.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 10.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(5.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldHandlePerfectGrades")
        void shouldHandlePerfectGrades() {
            Student student = new Student("STU015", true);
            student.addEvaluation(new Evaluation("Parcial", 20.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 20.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(20.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldHandleSingleEvaluation")
        void shouldHandleSingleEvaluation() {
            Student student = new Student("STU016", true);
            student.addEvaluation(new Evaluation("Única", 17.5, 1.0));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(17.5, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldHandleMaximumEvaluations")
        void shouldHandleMaximumEvaluations() {
            Student student = new Student("STU017", true);
            double weight = 0.1;
            for (int i = 1; i <= 10; i++) {
                student.addEvaluation(new Evaluation("E" + i, 15.0, weight));
            }

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(15.0, result.getFinalGrade(), 0.001);
        }
    }

    @Nested
    @DisplayName("Detalle del Cálculo")
    class CalculationDetails {

        @Test
        @DisplayName("shouldIncludeAllDetailsInResult")
        void shouldIncludeAllDetailsInResult() {
            Student student = new Student("STU018", true);
            student.addEvaluation(new Evaluation("Parcial", 14.0, 0.4));
            student.addEvaluation(new Evaluation("Final", 16.0, 0.6));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertEquals("STU018", result.getStudentCode());
            assertEquals(2, result.getEvaluations().size());
            assertNotNull(result.getAttendanceDetail());
            assertNotNull(result.getExtraPointsDetail());
        }

        @Test
        @DisplayName("shouldGenerateDetailedReport")
        void shouldGenerateDetailedReport() {
            Student student = new Student("STU019", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 17.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);
            String report = result.getDetailedReport();

            assertTrue(report.contains("STU019"));
            assertTrue(report.contains("Parcial"));
            assertTrue(report.contains("Final"));
            assertTrue(report.contains("NOTA FINAL"));
        }
    }

    private Student createStudentWithEvaluations(String code, boolean hasAttendance, Evaluation... evaluations) {
        Student student = new Student(code, hasAttendance);
        for (Evaluation eval : evaluations) {
            student.addEvaluation(eval);
        }
        return student;
    }
}
