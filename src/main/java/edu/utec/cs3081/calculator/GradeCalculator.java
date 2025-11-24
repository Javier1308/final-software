package edu.utec.cs3081.calculator;

import edu.utec.cs3081.exception.GradeCalculationException;
import edu.utec.cs3081.model.Evaluation;
import edu.utec.cs3081.model.Student;
import edu.utec.cs3081.policy.AttendancePolicy;
import edu.utec.cs3081.policy.ExtraPointsPolicy;

import java.util.List;

/**
 * Calculadora de nota final para estudiantes de UTEC.
 * Implementa RF04: cálculo de nota final considerando evaluaciones,
 * asistencia mínima y políticas de puntos extra.
 */
public class GradeCalculator {
    
    private static final double WEIGHT_TOLERANCE = 0.001;
    private static final double EXPECTED_TOTAL_WEIGHT = 1.0;
    
    private final AttendancePolicy attendancePolicy;
    private final ExtraPointsPolicy extraPointsPolicy;

    /**
     * Constructor con políticas personalizadas.
     * 
     * @param attendancePolicy política de asistencia
     * @param extraPointsPolicy política de puntos extra
     */
    public GradeCalculator(AttendancePolicy attendancePolicy, ExtraPointsPolicy extraPointsPolicy) {
        this.attendancePolicy = attendancePolicy != null ? attendancePolicy : new AttendancePolicy();
        this.extraPointsPolicy = extraPointsPolicy;
    }

    /**
     * Constructor solo con política de puntos extra.
     * 
     * @param extraPointsPolicy política de puntos extra
     */
    public GradeCalculator(ExtraPointsPolicy extraPointsPolicy) {
        this(new AttendancePolicy(), extraPointsPolicy);
    }

    /**
     * Constructor por defecto sin puntos extra.
     */
    public GradeCalculator() {
        this(new AttendancePolicy(), null);
    }

    /**
     * Calcula la nota final de un estudiante para un año académico.
     * El cálculo es determinista: mismos datos producen misma nota (RNF03).
     * 
     * @param student el estudiante con sus evaluaciones
     * @param academicYear el año académico para aplicar política de puntos extra
     * @return GradeResult con la nota final y detalles del cálculo
     * @throws GradeCalculationException si hay error en el cálculo
     */
    public GradeResult calculateFinalGrade(Student student, int academicYear) {
        validateStudent(student);
        validateWeights(student.getEvaluations());

        double weightedAverage = calculateWeightedAverage(student.getEvaluations());
        boolean meetsAttendance = attendancePolicy.meetsMinimumAttendance(student);
        String attendanceDetail = attendancePolicy.getPolicyDescription(student);

        if (!meetsAttendance) {
            return buildResultForNoAttendance(student, weightedAverage, attendanceDetail, academicYear);
        }

        double gradeWithExtra = applyExtraPoints(weightedAverage, academicYear);
        double extraPointsApplied = gradeWithExtra - weightedAverage;
        String extraPointsDetail = getExtraPointsDetail(academicYear);

        return new GradeResult.Builder()
            .studentCode(student.getCode())
            .evaluations(student.getEvaluations())
            .weightedAverage(weightedAverage)
            .meetsAttendance(true)
            .penalizedByAttendance(false)
            .attendanceDetail(attendanceDetail)
            .extraPointsApplied(extraPointsApplied)
            .extraPointsDetail(extraPointsDetail)
            .finalGrade(gradeWithExtra)
            .build();
    }

    /**
     * Calcula la nota final sin considerar año académico (sin puntos extra).
     * 
     * @param student el estudiante con sus evaluaciones
     * @return GradeResult con la nota final y detalles
     */
    public GradeResult calculateFinalGrade(Student student) {
        return calculateFinalGrade(student, 0);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new GradeCalculationException("El estudiante no puede ser nulo");
        }
        if (student.getEvaluations().isEmpty()) {
            throw new GradeCalculationException("El estudiante debe tener al menos una evaluación");
        }
    }

    private void validateWeights(List<Evaluation> evaluations) {
        double totalWeight = evaluations.stream()
            .mapToDouble(Evaluation::getWeight)
            .sum();
        
        if (Math.abs(totalWeight - EXPECTED_TOTAL_WEIGHT) > WEIGHT_TOLERANCE) {
            throw new GradeCalculationException(
                String.format("La suma de pesos debe ser 1.0 (100%%). Suma actual: %.2f", totalWeight));
        }
    }

    private double calculateWeightedAverage(List<Evaluation> evaluations) {
        return evaluations.stream()
            .mapToDouble(Evaluation::getWeightedGrade)
            .sum();
    }

    private double applyExtraPoints(double grade, int academicYear) {
        if (extraPointsPolicy == null) {
            return grade;
        }
        return extraPointsPolicy.applyExtraPoints(grade, academicYear);
    }

    private String getExtraPointsDetail(int academicYear) {
        if (extraPointsPolicy == null) {
            return "No configurada política de puntos extra";
        }
        return extraPointsPolicy.getPolicyDescription(academicYear);
    }

    private GradeResult buildResultForNoAttendance(Student student, double weightedAverage, 
                                                    String attendanceDetail, int academicYear) {
        return new GradeResult.Builder()
            .studentCode(student.getCode())
            .evaluations(student.getEvaluations())
            .weightedAverage(weightedAverage)
            .meetsAttendance(false)
            .penalizedByAttendance(true)
            .attendanceDetail(attendanceDetail)
            .extraPointsApplied(0)
            .extraPointsDetail("No aplica (penalizado por asistencia)")
            .finalGrade(0.0)
            .build();
    }
}
