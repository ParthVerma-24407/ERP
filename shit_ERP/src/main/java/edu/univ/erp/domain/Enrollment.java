package edu.univ.erp.domain;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;

    public Enrollment(int enrollmentId, int studentId, int sectionId) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
    }

    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
}
