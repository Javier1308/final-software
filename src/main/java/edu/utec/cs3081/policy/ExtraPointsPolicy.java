package edu.utec.cs3081.policy;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Política de puntos extra aplicada según el año académico.
 * Los docentes definen colectivamente en qué años se otorgan puntos extra.
 */
public class ExtraPointsPolicy {
    
    private static final double DEFAULT_EXTRA_POINTS = 2.0;
    private static final double MAX_GRADE = 20.0;
    
    private final Set<Integer> yearsWithExtraPoints;
    private final double extraPoints;

    /**
     * Constructor con años específicos y puntos extra por defecto.
     * 
     * @param allYearsTeachers lista de años donde se otorgan puntos extra
     */
    public ExtraPointsPolicy(List<Integer> allYearsTeachers) {
        this(allYearsTeachers, DEFAULT_EXTRA_POINTS);
    }

    /**
     * Constructor con años específicos y puntos extra personalizados.
     * 
     * @param allYearsTeachers lista de años donde se otorgan puntos extra
     * @param extraPoints cantidad de puntos extra a otorgar
     */
    public ExtraPointsPolicy(List<Integer> allYearsTeachers, double extraPoints) {
        this.yearsWithExtraPoints = allYearsTeachers != null 
            ? new HashSet<>(allYearsTeachers) 
            : new HashSet<>();
        this.extraPoints = Math.max(0, extraPoints);
    }

    /**
     * Verifica si se otorgan puntos extra para un año académico dado.
     * 
     * @param academicYear el año académico a verificar
     * @return true si se otorgan puntos extra para ese año
     */
    public boolean hasExtraPoints(int academicYear) {
        return yearsWithExtraPoints.contains(academicYear);
    }

    /**
     * Aplica los puntos extra a la nota si corresponde al año académico.
     * La nota máxima es 20.
     * 
     * @param grade la nota base
     * @param academicYear el año académico del estudiante
     * @return la nota con puntos extra aplicados (máximo 20)
     */
    public double applyExtraPoints(double grade, int academicYear) {
        if (!hasExtraPoints(academicYear)) {
            return grade;
        }
        return Math.min(grade + extraPoints, MAX_GRADE);
    }

    /**
     * Obtiene la cantidad de puntos extra otorgados para un año.
     * 
     * @param academicYear el año académico
     * @return cantidad de puntos extra (0 si no aplica)
     */
    public double getExtraPointsForYear(int academicYear) {
        return hasExtraPoints(academicYear) ? extraPoints : 0.0;
    }

    /**
     * Obtiene la descripción de la política aplicada.
     * 
     * @param academicYear el año académico
     * @return descripción de la política
     */
    public String getPolicyDescription(int academicYear) {
        if (hasExtraPoints(academicYear)) {
            return String.format("Puntos extra aplicados: +%.2f (año %d)", extraPoints, academicYear);
        }
        return String.format("Sin puntos extra para el año %d", academicYear);
    }

    /**
     * Obtiene los años configurados con puntos extra.
     * 
     * @return conjunto inmutable de años con puntos extra
     */
    public Set<Integer> getYearsWithExtraPoints() {
        return Collections.unmodifiableSet(yearsWithExtraPoints);
    }

    /**
     * Obtiene la cantidad de puntos extra configurada.
     * 
     * @return puntos extra
     */
    public double getExtraPoints() {
        return extraPoints;
    }
}
