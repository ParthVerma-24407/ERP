package edu.univ.erp.domain;
import edu.univ.erp.domain.Student;
public class Grade {

    private int student_id;
    private String component;
    private double score;
    private String finalGrade;

    public Grade(int gradeId, int student_id, String component, double score, String finalGrade) {

        this.student_id = student_id;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }


    public int getStudent_id() {
        return student_id;
    }

    public String getComponent() {
        return component;
    }

    public double getScore() {
        return score;
    }

    public String getFinalGrade() {
        return finalGrade;
    }
}
