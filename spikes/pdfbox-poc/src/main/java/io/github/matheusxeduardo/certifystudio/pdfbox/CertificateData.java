package io.github.matheusxeduardo.certifystudio.pdfbox;

import java.time.LocalDate;
import java.util.Objects;

public record CertificateData(
        String participantName,
        String courseName,
        int workloadHours,
        LocalDate completionDate,
        String instructorName,
        String validationCode
) {

    public CertificateData {
        participantName = requireText(participantName, "participantName");
        courseName = requireText(courseName, "courseName");
        instructorName = requireText(instructorName, "instructorName");
        validationCode = requireText(validationCode, "validationCode");
        completionDate = Objects.requireNonNull(completionDate, "completionDate is required");

        if (workloadHours <= 0) {
            throw new IllegalArgumentException("workloadHours must be greater than zero");
        }
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value.trim();
    }
}