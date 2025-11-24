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
            Student student1 = createStudentWithEvaluations(
                    new Evaluation("E1", 12.0, 0.5),
                    new Evaluation("E2", 14.0, 0.5));

            Student student2 = createStudentWithEvaluations(
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

        @Test
        @DisplayName("shouldCalculateFinalGradeWithoutAcademicYear")
        void shouldCalculateFinalGradeWithoutAcademicYear() {
            Student student = new Student("STU020", true);
            student.addEvaluation(new Evaluation("Parcial", 16.0, 0.5));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.5));

            GradeResult result = calculator.calculateFinalGrade(student);

            assertEquals(17.0, result.getFinalGrade(), 0.001);
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

        @Test
        @DisplayName("shouldReturnCorrectDetailsWhenNoAttendance")
        void shouldReturnCorrectDetailsWhenNoAttendance() {
            Student student = new Student("STU021", false);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 1.0));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertFalse(result.isMeetsAttendance());
            assertTrue(result.isPenalizedByAttendance());
            assertNotNull(result.getAttendanceDetail());
            assertTrue(result.getAttendanceDetail().contains("No cumple"));
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

        @Test
        @DisplayName("shouldWorkWithNullAttendancePolicy")
        void shouldWorkWithNullAttendancePolicy() {
            GradeCalculator calcWithNullAttendance = new GradeCalculator(null, extraPointsPolicy);
            Student student = new Student("STU022", true);
            student.addEvaluation(new Evaluation("Parcial", 14.0, 1.0));

            GradeResult result = calcWithNullAttendance.calculateFinalGrade(student, 2025);

            assertEquals(16.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldWorkWithExtraPointsPolicyOnlyConstructor")
        void shouldWorkWithExtraPointsPolicyOnlyConstructor() {
            GradeCalculator calcWithExtraOnly = new GradeCalculator(extraPointsPolicy);
            Student student = new Student("STU023", true);
            student.addEvaluation(new Evaluation("Final", 15.0, 1.0));

            GradeResult result = calcWithExtraOnly.calculateFinalGrade(student, 2024);

            assertEquals(17.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldReturnCorrectExtraPointsDetail")
        void shouldReturnCorrectExtraPointsDetail() {
            Student student = new Student("STU024", true);
            student.addEvaluation(new Evaluation("Final", 15.0, 1.0));

            GradeResult result = calculator.calculateFinalGrade(student, 2025);

            assertNotNull(result.getExtraPointsDetail());
            assertTrue(result.getExtraPointsDetail().contains("2025") ||
                    result.getExtraPointsDetail().contains("extra"));
        }

        @Test
        @DisplayName("shouldReturnNoExtraPointsDetailWhenYearNotConfigured")
        void shouldReturnNoExtraPointsDetailWhenYearNotConfigured() {
            Student student = new Student("STU025", true);
            student.addEvaluation(new Evaluation("Final", 15.0, 1.0));

            GradeResult result = calculator.calculateFinalGrade(student, 2020);

            assertNotNull(result.getExtraPointsDetail());
        }
    }

    @Nested
    @DisplayName("Constructores")
    class ConstructorTests {

        @Test
        @DisplayName("shouldCreateCalculatorWithDefaultConstructor")
        void shouldCreateCalculatorWithDefaultConstructor() {
            GradeCalculator calc = new GradeCalculator();
            Student student = new Student("STU026", true);
            student.addEvaluation(new Evaluation("Final", 18.0, 1.0));

            GradeResult result = calc.calculateFinalGrade(student);

            assertEquals(18.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCreateCalculatorWithExtraPointsPolicyOnly")
        void shouldCreateCalculatorWithExtraPointsPolicyOnly() {
            ExtraPointsPolicy policy = new ExtraPointsPolicy(List.of(2025), 3.0);
            GradeCalculator calc = new GradeCalculator(policy);
            Student student = new Student("STU027", true);
            student.addEvaluation(new Evaluation("Final", 15.0, 1.0));

            GradeResult result = calc.calculateFinalGrade(student, 2025);

            assertEquals(18.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldCreateCalculatorWithBothPolicies")
        void shouldCreateCalculatorWithBothPolicies() {
            AttendancePolicy attendancePolicy = new AttendancePolicy();
            ExtraPointsPolicy policy = new ExtraPointsPolicy(List.of(2025));
            GradeCalculator calc = new GradeCalculator(attendancePolicy, policy);
            Student student = new Student("STU028", true);
            student.addEvaluation(new Evaluation("Final", 16.0, 1.0));

            GradeResult result = calc.calculateFinalGrade(student, 2025);

            assertEquals(18.0, result.getFinalGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldHandleNullExtraPointsPolicy")
        void shouldHandleNullExtraPointsPolicy() {
            GradeCalculator calc = new GradeCalculator(new AttendancePolicy(), null);
            Student student = new Student("STU029", true);
            student.addEvaluation(new Evaluation("Final", 17.0, 1.0));

            GradeResult result = calc.calculateFinalGrade(student, 2025);

            assertEquals(17.0, result.getFinalGrade(), 0.001);
            assertNotNull(result.getExtraPointsDetail());
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

        @Test
        @DisplayName("shouldThrowExceptionWithDescriptiveMessage")
        void shouldThrowExceptionWithDescriptiveMessage() {
            Student student = new Student("STU030", true);
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.5));

            GradeCalculationException exception = assertThrows(
                    GradeCalculationException.class,
                    () -> calculator.calculateFinalGrade(student, 2024)
            );

            assertTrue(exception.getMessage().contains("suma") ||
                    exception.getMessage().contains("peso") ||
                    exception.getMessage().contains("1.0"));
        }

        @Test
        @DisplayName("shouldHandleWeightsExactlyOne")
        void shouldHandleWeightsExactlyOne() {
            Student student = new Student("STU031", true);
            student.addEvaluation(new Evaluation("E1", 10.0, 0.25));
            student.addEvaluation(new Evaluation("E2", 12.0, 0.25));
            student.addEvaluation(new Evaluation("E3", 14.0, 0.25));
            student.addEvaluation(new Evaluation("E4", 16.0, 0.25));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(13.0, result.getFinalGrade(), 0.001);
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

        @Test
        @DisplayName("shouldIncludeWeightedAverageInResult")
        void shouldIncludeWeightedAverageInResult() {
            Student student = new Student("STU032", true);
            student.addEvaluation(new Evaluation("Parcial", 12.0, 0.4));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.6));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            double expectedAverage = (12.0 * 0.4) + (18.0 * 0.6);
            assertEquals(expectedAverage, result.getWeightedAverage(), 0.001);
        }

        @Test
        @DisplayName("shouldIncludeCorrectEvaluationsInResult")
        void shouldIncludeCorrectEvaluationsInResult() {
            Student student = new Student("STU033", true);
            student.addEvaluation(new Evaluation("PC1", 14.0, 0.3));
            student.addEvaluation(new Evaluation("PC2", 16.0, 0.3));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.4));

            GradeResult result = calculator.calculateFinalGrade(student, 2023);

            assertEquals(3, result.getEvaluations().size());
            assertEquals("PC1", result.getEvaluations().get(0).getName());
            assertEquals("PC2", result.getEvaluations().get(1).getName());
            assertEquals("Final", result.getEvaluations().get(2).getName());
        }
    }

    private Student createStudentWithEvaluations(Evaluation... evaluations) {
        Student student = new Student("STU003", true);
        for (Evaluation eval : evaluations) {
            student.addEvaluation(eval);
        }
        return student;
    }
}