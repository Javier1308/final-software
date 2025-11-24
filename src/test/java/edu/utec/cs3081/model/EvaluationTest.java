package edu.utec.cs3081.model;

import edu.utec.cs3081.exception.InvalidEvaluationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Evaluation Tests")
class EvaluationTest {

    @Nested
    @DisplayName("Creación Válida")
    class ValidCreation {

        @Test
        @DisplayName("shouldCreateEvaluationWithValidData")
        void shouldCreateEvaluationWithValidData() {
            Evaluation eval = new Evaluation("Parcial", 15.0, 0.5);

            assertEquals("Parcial", eval.getName());
            assertEquals(15.0, eval.getGrade(), 0.001);
            assertEquals(0.5, eval.getWeight(), 0.001);
        }

        @Test
        @DisplayName("shouldCalculateWeightedGradeCorrectly")
        void shouldCalculateWeightedGradeCorrectly() {
            Evaluation eval = new Evaluation("Final", 18.0, 0.6);
            assertEquals(10.8, eval.getWeightedGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldAcceptZeroGrade")
        void shouldAcceptZeroGrade() {
            Evaluation eval = new Evaluation("PC1", 0.0, 0.3);
            assertEquals(0.0, eval.getGrade(), 0.001);
        }

        @Test
        @DisplayName("shouldAcceptMaxGrade")
        void shouldAcceptMaxGrade() {
            Evaluation eval = new Evaluation("PC2", 20.0, 0.3);
            assertEquals(20.0, eval.getGrade(), 0.001);
        }
    }

    @Nested
    @DisplayName("Validación de Datos")
    class DataValidation {

        @Test
        @DisplayName("shouldThrowExceptionWhenNameIsNull")
        void shouldThrowExceptionWhenNameIsNull() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation(null, 15.0, 0.5));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenNameIsBlank")
        void shouldThrowExceptionWhenNameIsBlank() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("   ", 15.0, 0.5));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenGradeIsNegative")
        void shouldThrowExceptionWhenGradeIsNegative() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("Parcial", -1.0, 0.5));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenGradeExceedsTwenty")
        void shouldThrowExceptionWhenGradeExceedsTwenty() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("Parcial", 21.0, 0.5));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenWeightIsZero")
        void shouldThrowExceptionWhenWeightIsZero() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("Parcial", 15.0, 0.0));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenWeightIsNegative")
        void shouldThrowExceptionWhenWeightIsNegative() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("Parcial", 15.0, -0.1));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenWeightExceedsOne")
        void shouldThrowExceptionWhenWeightExceedsOne() {
            assertThrows(InvalidEvaluationException.class,
                () -> new Evaluation("Parcial", 15.0, 1.5));
        }
    }

    @Nested
    @DisplayName("Representación de Texto")
    class TextRepresentation {

        @Test
        @DisplayName("shouldReturnFormattedString")
        void shouldReturnFormattedString() {
            Evaluation eval = new Evaluation("Parcial", 15.5, 0.4);
            String str = eval.toString();

            assertTrue(str.contains("Parcial"));
            assertTrue(str.contains("15.50") || str.contains("15,50"));
            assertTrue(str.contains("40%"));
        }
    }
}
