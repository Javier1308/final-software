package edu.utec.cs3081.model;

import edu.utec.cs3081.exception.InvalidStudentDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Tests")
class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student("STU001", true);
    }

    @Nested
    @DisplayName("Creación Válida")
    class ValidCreation {

        @Test
        @DisplayName("shouldCreateStudentWithValidData")
        void shouldCreateStudentWithValidData() {
            Student s = new Student("STU002", true);

            assertEquals("STU002", s.getCode());
            assertTrue(s.hasReachedMinimumClasses());
            assertEquals(0, s.getEvaluationCount());
        }

        @Test
        @DisplayName("shouldCreateStudentWithoutAttendance")
        void shouldCreateStudentWithoutAttendance() {
            Student s = new Student("STU003", false);

            assertFalse(s.hasReachedMinimumClasses());
        }
    }

    @Nested
    @DisplayName("Validación de Datos")
    class DataValidation {

        @Test
        @DisplayName("shouldThrowExceptionWhenCodeIsNull")
        void shouldThrowExceptionWhenCodeIsNull() {
            assertThrows(InvalidStudentDataException.class,
                () -> new Student(null, true));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenCodeIsBlank")
        void shouldThrowExceptionWhenCodeIsBlank() {
            assertThrows(InvalidStudentDataException.class,
                () -> new Student("   ", true));
        }
    }

    @Nested
    @DisplayName("Gestión de Evaluaciones")
    class EvaluationManagement {

        @Test
        @DisplayName("shouldAddEvaluationSuccessfully")
        void shouldAddEvaluationSuccessfully() {
            Evaluation eval = new Evaluation("Parcial", 15.0, 0.5);
            student.addEvaluation(eval);

            assertEquals(1, student.getEvaluationCount());
            assertEquals(eval, student.getEvaluations().get(0));
        }

        @Test
        @DisplayName("shouldAddMultipleEvaluations")
        void shouldAddMultipleEvaluations() {
            student.addEvaluation(new Evaluation("PC1", 14.0, 0.2));
            student.addEvaluation(new Evaluation("PC2", 16.0, 0.3));
            student.addEvaluation(new Evaluation("Final", 18.0, 0.5));

            assertEquals(3, student.getEvaluationCount());
        }

        @Test
        @DisplayName("shouldAddEvaluationsFromList")
        void shouldAddEvaluationsFromList() {
            student.addEvaluations(Arrays.asList(
                new Evaluation("E1", 15.0, 0.5),
                new Evaluation("E2", 16.0, 0.5)
            ));

            assertEquals(2, student.getEvaluationCount());
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenNullEvaluation")
        void shouldThrowExceptionWhenNullEvaluation() {
            assertThrows(InvalidStudentDataException.class,
                () -> student.addEvaluation(null));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenNullEvaluationsList")
        void shouldThrowExceptionWhenNullEvaluationsList() {
            assertThrows(InvalidStudentDataException.class,
                () -> student.addEvaluations(null));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenExceedingMaxEvaluations")
        void shouldThrowExceptionWhenExceedingMaxEvaluations() {
            for (int i = 1; i <= 10; i++) {
                student.addEvaluation(new Evaluation("E" + i, 15.0, 0.1));
            }

            assertThrows(InvalidStudentDataException.class,
                () -> student.addEvaluation(new Evaluation("E11", 15.0, 0.1)));
        }

        @Test
        @DisplayName("shouldReturnImmutableEvaluationsList")
        void shouldReturnImmutableEvaluationsList() {
            student.addEvaluation(new Evaluation("Parcial", 15.0, 0.5));

            assertThrows(UnsupportedOperationException.class,
                () -> student.getEvaluations().add(new Evaluation("Extra", 10.0, 0.5)));
        }
    }

    @Nested
    @DisplayName("Información del Estudiante")
    class StudentInfo {

        @Test
        @DisplayName("shouldReturnMaxEvaluationsConstant")
        void shouldReturnMaxEvaluationsConstant() {
            assertEquals(10, Student.getMaxEvaluations());
        }

        @Test
        @DisplayName("shouldReturnFormattedString")
        void shouldReturnFormattedString() {
            student.addEvaluation(new Evaluation("Parcial", 15.0, 1.0));
            String str = student.toString();

            assertTrue(str.contains("STU001"));
            assertTrue(str.contains("1"));
        }
    }
}
