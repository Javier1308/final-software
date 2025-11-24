package edu.utec.cs3081.model;

import edu.utec.cs3081.exception.InvalidEvaluationException;

/**
 * Representa una evaluación individual con su nota y peso.
 */
public class Evaluation {
    
    private static final double MIN_GRADE = 0.0;
    private static final double MAX_GRADE = 20.0;
    private static final double MIN_WEIGHT = 0.0;
    private static final double MAX_WEIGHT = 1.0;
    
    private final String name;
    private final double grade;
    private final double weight;

    public Evaluation(String name, double grade, double weight) {
        validateName(name);
        validateGrade(grade);
        validateWeight(weight);
        
        this.name = name;
        this.grade = grade;
        this.weight = weight;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidEvaluationException("El nombre de la evaluación no puede ser nulo o vacío");
        }
    }

    private void validateGrade(double grade) {
        if (grade < MIN_GRADE || grade > MAX_GRADE) {
            throw new InvalidEvaluationException(
                String.format("La nota debe estar entre %.1f y %.1f. Valor recibido: %.2f", 
                    MIN_GRADE, MAX_GRADE, grade));
        }
    }

    private void validateWeight(double weight) {
        if (weight <= MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new InvalidEvaluationException(
                String.format("El peso debe estar entre %.1f (exclusivo) y %.1f. Valor recibido: %.2f", 
                    MIN_WEIGHT, MAX_WEIGHT, weight));
        }
    }

    public String getName() {
        return name;
    }

    public double getGrade() {
        return grade;
    }

    public double getWeight() {
        return weight;
    }

    public double getWeightedGrade() {
        return grade * weight;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f (peso: %.0f%%)", name, grade, weight * 100);
    }
}
