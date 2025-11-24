package edu.utec.cs3081.calculator;

import edu.utec.cs3081.model.Evaluation;

import java.util.List;

/**
 * Resultado del cálculo de nota final con todos los detalles.
 * Cumple con RF05: mostrar detalle del cálculo.
 */
public class GradeResult {
    
    private final String studentCode;
    private final double weightedAverage;
    private final double extraPointsApplied;
    private final double finalGrade;
    private final boolean meetsAttendance;
    private final boolean penalizedByAttendance;
    private final List<Evaluation> evaluations;
    private final String attendanceDetail;
    private final String extraPointsDetail;

    private GradeResult(Builder builder) {
        this.studentCode = builder.studentCode;
        this.weightedAverage = builder.weightedAverage;
        this.extraPointsApplied = builder.extraPointsApplied;
        this.finalGrade = builder.finalGrade;
        this.meetsAttendance = builder.meetsAttendance;
        this.penalizedByAttendance = builder.penalizedByAttendance;
        this.evaluations = builder.evaluations;
        this.attendanceDetail = builder.attendanceDetail;
        this.extraPointsDetail = builder.extraPointsDetail;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public double getWeightedAverage() {
        return weightedAverage;
    }

    public double getExtraPointsApplied() {
        return extraPointsApplied;
    }

    public double getFinalGrade() {
        return finalGrade;
    }

    public boolean isMeetsAttendance() {
        return meetsAttendance;
    }

    public boolean isPenalizedByAttendance() {
        return penalizedByAttendance;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public String getAttendanceDetail() {
        return attendanceDetail;
    }

    public String getExtraPointsDetail() {
        return extraPointsDetail;
    }

    /**
     * Genera el reporte detallado del cálculo.
     * 
     * @return String con el detalle completo del cálculo
     */
    public String getDetailedReport() {
        StringBuilder report = new StringBuilder();
        report.append("═══════════════════════════════════════════\n");
        report.append("       DETALLE DE CÁLCULO DE NOTA FINAL    \n");
        report.append("═══════════════════════════════════════════\n");
        report.append(String.format("Estudiante: %s%n", studentCode));
        report.append("───────────────────────────────────────────\n");
        
        report.append("EVALUACIONES:\n");
        for (Evaluation eval : evaluations) {
            report.append(String.format("  • %s: %.2f × %.0f%% = %.2f%n", 
                eval.getName(), eval.getGrade(), eval.getWeight() * 100, eval.getWeightedGrade()));
        }
        
        report.append("───────────────────────────────────────────\n");
        report.append(String.format("Promedio ponderado: %.2f%n", weightedAverage));
        report.append(String.format("Asistencia: %s%n", attendanceDetail));
        report.append(String.format("Puntos extra: %s%n", extraPointsDetail));
        
        report.append("═══════════════════════════════════════════\n");
        report.append(String.format("NOTA FINAL: %.2f%n", finalGrade));
        report.append("═══════════════════════════════════════════\n");
        
        return report.toString();
    }

    @Override
    public String toString() {
        return String.format("GradeResult[student=%s, final=%.2f]", studentCode, finalGrade);
    }

    /**
     * Builder para construir GradeResult de forma fluida.
     */
    public static class Builder {
        private String studentCode;
        private double weightedAverage;
        private double extraPointsApplied;
        private double finalGrade;
        private boolean meetsAttendance;
        private boolean penalizedByAttendance;
        private List<Evaluation> evaluations;
        private String attendanceDetail;
        private String extraPointsDetail;

        public Builder studentCode(String studentCode) {
            this.studentCode = studentCode;
            return this;
        }

        public Builder weightedAverage(double weightedAverage) {
            this.weightedAverage = weightedAverage;
            return this;
        }

        public Builder extraPointsApplied(double extraPointsApplied) {
            this.extraPointsApplied = extraPointsApplied;
            return this;
        }

        public Builder finalGrade(double finalGrade) {
            this.finalGrade = finalGrade;
            return this;
        }

        public Builder meetsAttendance(boolean meetsAttendance) {
            this.meetsAttendance = meetsAttendance;
            return this;
        }

        public Builder penalizedByAttendance(boolean penalizedByAttendance) {
            this.penalizedByAttendance = penalizedByAttendance;
            return this;
        }

        public Builder evaluations(List<Evaluation> evaluations) {
            this.evaluations = evaluations;
            return this;
        }

        public Builder attendanceDetail(String attendanceDetail) {
            this.attendanceDetail = attendanceDetail;
            return this;
        }

        public Builder extraPointsDetail(String extraPointsDetail) {
            this.extraPointsDetail = extraPointsDetail;
            return this;
        }

        public GradeResult build() {
            return new GradeResult(this);
        }
    }
}
