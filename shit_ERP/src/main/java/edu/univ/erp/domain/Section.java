package edu.univ.erp.domain;

public class Section {
    private int sectionId;
    private String courseCode;
    private int instructorId;
    private int capacity;
    private int enrolledCount;
    private String timeSlot;
    public Section(int sectionId, String courseCode, int instructorId,
                   int capacity, int enrolledCount) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.instructorId = instructorId;
        this.capacity = capacity;
        this.enrolledCount = enrolledCount;
    }




    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public int getInstructorId() { return instructorId; }
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolledCount; }

    public boolean hasSeat() {
        return enrolledCount < capacity;
    }
}
