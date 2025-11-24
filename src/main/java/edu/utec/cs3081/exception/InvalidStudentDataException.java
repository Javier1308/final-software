package edu.utec.cs3081.exception;

/**
 * Excepción lanzada cuando los datos de un estudiante son inválidos.
 */
public class InvalidStudentDataException extends RuntimeException {
    
    public InvalidStudentDataException(String message) {
        super(message);
    }
}
