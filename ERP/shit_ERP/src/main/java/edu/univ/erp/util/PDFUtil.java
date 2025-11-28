package edu.univ.erp.util;

import edu.univ.erp.data.dao.GradeDAO;
import edu.univ.erp.data.dao.SectionDAO;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.util.List;

public class PDFUtil {

    public static File writeTranscriptPDF(List<Enrollment> enrollments, String path) throws Exception {
        File file = new File(path);

        SectionDAO sectionDAO = new SectionDAO();
        GradeDAO gradeDAO = new GradeDAO();

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
            cs.beginText();
            cs.newLineAtOffset(50, 800);
            cs.showText("Transcript");
            cs.endText();

            float y = 770;

            cs.setFont(PDType1Font.HELVETICA, 12);

            for (Enrollment e : enrollments) {

                if (y < 50) break;

                Section sec = sectionDAO.findById(e.getSectionId());
                String courseCode = (sec != null ? sec.getCourseCode() : "");
                String finalGrade = gradeDAO.getFinalGrade(e.getEnrollmentId());
                if (finalGrade == null) finalGrade = "-";

                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText(
                        "Course: " + courseCode +
                                "   Section: " + e.getSectionId() +
                                "   Final Grade: " + finalGrade
                );
                cs.endText();

                y -= 20;
            }

            cs.close();
            doc.save(file);
        }
        return file;
    }
}
