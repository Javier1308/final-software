package edu.utec.cs3081.exception;

/**
 * Excepción lanzada cuando ocurre un error en el cálculo de la nota.
 */
public class GradeCalculationException extends RuntimeException {
    
    public GradeCalculationException(String message) {
        super(message);
    }
}
