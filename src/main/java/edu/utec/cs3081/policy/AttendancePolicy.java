package edu.utec.cs3081.policy;

import edu.utec.cs3081.model.Student;

/**
 * Política de asistencia mínima según el reglamento de UTEC.
 * Determina si un estudiante cumple con la asistencia requerida.
 */
public class AttendancePolicy {
    
    private static final double PENALTY_FOR_NO_ATTENDANCE = 0.0;
    
    /**
     * Verifica si el estudiante cumple con la asistencia mínima.
     * 
     * @param student el estudiante a verificar
     * @return true si cumple con la asistencia mínima
     */
    public boolean meetsMinimumAttendance(Student student) {
        if (student == null) {
            return false;
        }
        return student.hasReachedMinimumClasses();
    }

    /**
     * Aplica la penalización por falta de asistencia.
     * Si el estudiante no cumple con la asistencia mínima, su nota es 0.
     * 
     * @param student el estudiante
     * @param calculatedGrade la nota calculada antes de aplicar la política
     * @return la nota después de aplicar la política de asistencia
     */
    public double applyPolicy(Student student, double calculatedGrade) {
        if (!meetsMinimumAttendance(student)) {
            return PENALTY_FOR_NO_ATTENDANCE;
        }
        return calculatedGrade;
    }

    /**
     * Obtiene la descripción de la política aplicada.
     * 
     * @param student el estudiante
     * @return descripción de la política aplicada
     */
    public String getPolicyDescription(Student student) {
        if (!meetsMinimumAttendance(student)) {
            return "No cumple asistencia mínima - Nota final: 0.00";
        }
        return "Cumple con asistencia mínima";
    }
}
