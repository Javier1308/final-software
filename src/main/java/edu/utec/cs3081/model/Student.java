package edu.utec.cs3081.model;

import edu.utec.cs3081.exception.InvalidStudentDataException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un estudiante con sus evaluaciones y estado de asistencia.
 */
public class Student {
    
    private static final int MAX_EVALUATIONS = 10;
    
    private final String code;
    private final List<Evaluation> evaluations;
    private final boolean hasReachedMinimumClasses;

    public Student(String code, boolean hasReachedMinimumClasses) {
        validateCode(code);
        this.code = code;
        this.hasReachedMinimumClasses = hasReachedMinimumClasses;
        this.evaluations = new ArrayList<>();
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidStudentDataException("El código del estudiante no puede ser nulo o vacío");
        }
    }

    public void addEvaluation(Evaluation evaluation) {
        if (evaluation == null) {
            throw new InvalidStudentDataException("La evaluación no puede ser nula");
        }
        if (evaluations.size() >= MAX_EVALUATIONS) {
            throw new InvalidStudentDataException(
                String.format("No se pueden agregar más de %d evaluaciones", MAX_EVALUATIONS));
        }
        evaluations.add(evaluation);
    }

    public void addEvaluations(List<Evaluation> newEvaluations) {
        if (newEvaluations == null) {
            throw new InvalidStudentDataException("La lista de evaluaciones no puede ser nula");
        }
        for (Evaluation evaluation : newEvaluations) {
            addEvaluation(evaluation);
        }
    }

    public String getCode() {
        return code;
    }

    public List<Evaluation> getEvaluations() {
        return Collections.unmodifiableList(evaluations);
    }

    public boolean hasReachedMinimumClasses() {
        return hasReachedMinimumClasses;
    }

    public int getEvaluationCount() {
        return evaluations.size();
    }

    public static int getMaxEvaluations() {
        return MAX_EVALUATIONS;
    }

    @Override
    public String toString() {
        return String.format("Estudiante[código=%s, evaluaciones=%d, asistencia=%s]", 
            code, evaluations.size(), hasReachedMinimumClasses ? "Sí" : "No");
    }
}
