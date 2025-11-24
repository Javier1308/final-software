package edu.utec.cs3081;

import edu.utec.cs3081.calculator.GradeCalculator;
import edu.utec.cs3081.calculator.GradeResult;
import edu.utec.cs3081.model.Evaluation;
import edu.utec.cs3081.model.Student;
import edu.utec.cs3081.policy.AttendancePolicy;
import edu.utec.cs3081.policy.ExtraPointsPolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del sistema CS-GradeCalculator.
 * Interfaz de línea de comandos para docentes de UTEC.
 */
public class Main {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        printWelcome();
        
        try {
            Student student = inputStudentData();
            int academicYear = inputAcademicYear();
            List<Integer> yearsWithExtra = inputYearsWithExtraPoints();
            
            ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(yearsWithExtra);
            AttendancePolicy attendancePolicy = new AttendancePolicy();
            GradeCalculator calculator = new GradeCalculator(attendancePolicy, extraPointsPolicy);
            
            long startTime = System.currentTimeMillis();
            GradeResult result = calculator.calculateFinalGrade(student, academicYear);
            long endTime = System.currentTimeMillis();
            
            System.out.println("\n" + result.getDetailedReport());
            System.out.printf("Tiempo de cálculo: %d ms%n", (endTime - startTime));
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void printWelcome() {
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     CS-GradeCalculator - UTEC 2025-2      ║");
        System.out.println("║   Sistema de Cálculo de Nota Final        ║");
        System.out.println("╚═══════════════════════════════════════════╝");
        System.out.println();
    }

    private static Student inputStudentData() {
        System.out.print("Ingrese código del estudiante: ");
        String code = scanner.nextLine().trim();
        
        System.out.print("¿El estudiante cumplió asistencia mínima? (s/n): ");
        boolean hasAttendance = scanner.nextLine().trim().equalsIgnoreCase("s");
        
        Student student = new Student(code, hasAttendance);
        
        System.out.print("¿Cuántas evaluaciones tiene el estudiante? (máx 10): ");
        int numEvaluations = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.println("\nIngrese las evaluaciones:");
        for (int i = 1; i <= numEvaluations; i++) {
            System.out.printf("--- Evaluación %d ---%n", i);
            
            System.out.print("  Nombre: ");
            String name = scanner.nextLine().trim();
            
            System.out.print("  Nota (0-20): ");
            double grade = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("  Peso (ej: 0.30 para 30%%): ");
            double weight = Double.parseDouble(scanner.nextLine().trim());
            
            student.addEvaluation(new Evaluation(name, grade, weight));
        }
        
        return student;
    }

    private static int inputAcademicYear() {
        System.out.print("\nIngrese el año académico del estudiante: ");
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private static List<Integer> inputYearsWithExtraPoints() {
        System.out.print("¿Se otorgan puntos extra este año? (s/n): ");
        String hasExtra = scanner.nextLine().trim();
        
        if (!hasExtra.equalsIgnoreCase("s")) {
            return new ArrayList<>();
        }
        
        System.out.print("Ingrese los años con puntos extra (separados por coma, ej: 2024,2025): ");
        String yearsInput = scanner.nextLine().trim();
        
        if (yearsInput.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(yearsInput.split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .toList();
    }

    /**
     * Método para ejecución programática (útil para tests de integración).
     * 
     * @param student estudiante a evaluar
     * @param academicYear año académico
     * @param yearsWithExtra años con puntos extra
     * @return resultado del cálculo
     */
    public static GradeResult calculateGrade(Student student, int academicYear, List<Integer> yearsWithExtra) {
        ExtraPointsPolicy extraPointsPolicy = new ExtraPointsPolicy(yearsWithExtra);
        GradeCalculator calculator = new GradeCalculator(extraPointsPolicy);
        return calculator.calculateFinalGrade(student, academicYear);
    }
}
