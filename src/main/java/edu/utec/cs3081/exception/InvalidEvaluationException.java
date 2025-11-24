package edu.utec.cs3081.exception;

/**
 * Excepción lanzada cuando los datos de una evaluación son inválidos.
 */
public class InvalidEvaluationException extends RuntimeException {
    
    public InvalidEvaluationException(String message) {
        super(message);
    }
}
