package edu.utec.cs3081.policy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExtraPointsPolicy Tests")
class ExtraPointsPolicyTest {

    private ExtraPointsPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new ExtraPointsPolicy(Arrays.asList(2024, 2025));
    }

    @Nested
    @DisplayName("Verificación de Años con Puntos Extra")
    class YearVerification {

        @Test
        @DisplayName("shouldReturnTrueWhenYearHasExtraPoints")
        void shouldReturnTrueWhenYearHasExtraPoints() {
            assertTrue(policy.hasExtraPoints(2024));
            assertTrue(policy.hasExtraPoints(2025));
        }

        @Test
        @DisplayName("shouldReturnFalseWhenYearDoesNotHaveExtraPoints")
        void shouldReturnFalseWhenYearDoesNotHaveExtraPoints() {
            assertFalse(policy.hasExtraPoints(2023));
            assertFalse(policy.hasExtraPoints(2026));
        }
    }

    @Nested
    @DisplayName("Aplicación de Puntos Extra")
    class ExtraPointsApplication {

        @Test
        @DisplayName("shouldAddTwoPointsWhenYearIsConfigured")
        void shouldAddTwoPointsWhenYearIsConfigured() {
            double result = policy.applyExtraPoints(15.0, 2024);
            assertEquals(17.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldNotAddPointsWhenYearIsNotConfigured")
        void shouldNotAddPointsWhenYearIsNotConfigured() {
            double result = policy.applyExtraPoints(15.0, 2023);
            assertEquals(15.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldCapGradeAtTwenty")
        void shouldCapGradeAtTwenty() {
            double result = policy.applyExtraPoints(19.5, 2024);
            assertEquals(20.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldReturnTwentyWhenGradeIsTwenty")
        void shouldReturnTwentyWhenGradeIsTwenty() {
            double result = policy.applyExtraPoints(20.0, 2024);
            assertEquals(20.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Configuración Personalizada")
    class CustomConfiguration {

        @Test
        @DisplayName("shouldApplyCustomExtraPoints")
        void shouldApplyCustomExtraPoints() {
            ExtraPointsPolicy customPolicy = new ExtraPointsPolicy(
                Arrays.asList(2024), 3.0);
            
            double result = customPolicy.applyExtraPoints(15.0, 2024);
            assertEquals(18.0, result, 0.001);
        }

        @Test
        @DisplayName("shouldHandleNullYearsList")
        void shouldHandleNullYearsList() {
            ExtraPointsPolicy nullPolicy = new ExtraPointsPolicy(null);
            
            assertFalse(nullPolicy.hasExtraPoints(2024));
            assertEquals(15.0, nullPolicy.applyExtraPoints(15.0, 2024), 0.001);
        }

        @Test
        @DisplayName("shouldHandleEmptyYearsList")
        void shouldHandleEmptyYearsList() {
            ExtraPointsPolicy emptyPolicy = new ExtraPointsPolicy(Collections.emptyList());
            
            assertFalse(emptyPolicy.hasExtraPoints(2024));
        }

        @Test
        @DisplayName("shouldHandleNegativeExtraPoints")
        void shouldHandleNegativeExtraPoints() {
            ExtraPointsPolicy negativePolicy = new ExtraPointsPolicy(
                Arrays.asList(2024), -5.0);
            
            assertEquals(0.0, negativePolicy.getExtraPoints(), 0.001);
        }
    }

    @Nested
    @DisplayName("Obtención de Detalles")
    class PolicyDetails {

        @Test
        @DisplayName("shouldReturnExtraPointsForConfiguredYear")
        void shouldReturnExtraPointsForConfiguredYear() {
            assertEquals(2.0, policy.getExtraPointsForYear(2024), 0.001);
        }

        @Test
        @DisplayName("shouldReturnZeroForNonConfiguredYear")
        void shouldReturnZeroForNonConfiguredYear() {
            assertEquals(0.0, policy.getExtraPointsForYear(2023), 0.001);
        }

        @Test
        @DisplayName("shouldReturnCorrectDescription")
        void shouldReturnCorrectDescription() {
            String description = policy.getPolicyDescription(2024);
            assertTrue(description.contains("2024"));
            assertTrue(description.contains("2.00") || description.contains("2,00"));
        }

        @Test
        @DisplayName("shouldReturnNoExtraPointsDescription")
        void shouldReturnNoExtraPointsDescription() {
            String description = policy.getPolicyDescription(2023);
            assertTrue(description.contains("Sin puntos extra"));
        }

        @Test
        @DisplayName("shouldReturnImmutableYearsSet")
        void shouldReturnImmutableYearsSet() {
            assertThrows(UnsupportedOperationException.class, 
                () -> policy.getYearsWithExtraPoints().add(2030));
        }
    }
}
