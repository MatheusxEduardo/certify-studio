package io.github.matheusxeduardo.certifystudio.pdfbox;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class PdfCertificateValidator {

    private static final int EXPECTED_PAGE_COUNT = 1;
    private static final float DIMENSION_TOLERANCE = 0.5f;

    public PdfValidationResult validate(
            Path pdfPath,
            CertificateData expectedData
    ) throws IOException {
        Objects.requireNonNull(pdfPath, "pdfPath is required");
        Objects.requireNonNull(
                expectedData,
                "expectedData is required"
        );

        Path normalizedPath =
                pdfPath.toAbsolutePath().normalize();

        validateFile(normalizedPath);

        try (PDDocument document =
                     Loader.loadPDF(normalizedPath.toFile())) {

            validatePageCount(document);

            PDPage page = document.getPage(0);
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            validateLandscapeA4(pageWidth, pageHeight);
            validateExpectedText(document, expectedData);

            return new PdfValidationResult(
                    normalizedPath,
                    document.getNumberOfPages(),
                    pageWidth,
                    pageHeight,
                    Files.size(normalizedPath)
            );
        } catch (IOException exception) {
            throw new IOException(
                    "Could not reopen and validate PDF: "
                            + normalizedPath,
                    exception
            );
        }
    }

    private static void validateFile(Path pdfPath)
            throws IOException {
        if (!Files.isRegularFile(pdfPath)) {
            throw new IOException(
                    "Generated PDF was not found: " + pdfPath
            );
        }

        if (Files.size(pdfPath) == 0) {
            throw new IOException(
                    "Generated PDF is empty: " + pdfPath
            );
        }
    }

    private static void validatePageCount(
            PDDocument document
    ) {
        int actualPageCount = document.getNumberOfPages();

        if (actualPageCount != EXPECTED_PAGE_COUNT) {
            throw new IllegalStateException(
                    "Expected one page, but PDF contains: "
                            + actualPageCount
            );
        }
    }

    private static void validateLandscapeA4(
            float pageWidth,
            float pageHeight
    ) {
        float expectedWidth = PDRectangle.A4.getHeight();
        float expectedHeight = PDRectangle.A4.getWidth();

        boolean validWidth =
                Math.abs(pageWidth - expectedWidth)
                        <= DIMENSION_TOLERANCE;

        boolean validHeight =
                Math.abs(pageHeight - expectedHeight)
                        <= DIMENSION_TOLERANCE;

        if (!validWidth || !validHeight) {
            throw new IllegalStateException(
                    "Expected A4 landscape dimensions, but found: "
                            + pageWidth
                            + " x "
                            + pageHeight
            );
        }
    }

    private static void validateExpectedText(
            PDDocument document,
            CertificateData expectedData
    ) throws IOException {
        PDFTextStripper textStripper = new PDFTextStripper();
        String extractedText = textStripper.getText(document);

        requireText(
                extractedText,
                expectedData.participantName(),
                "participant name"
        );

        requireText(
                extractedText,
                expectedData.courseName(),
                "course name"
        );

        requireText(
                extractedText,
                expectedData.validationCode(),
                "validation code"
        );
    }

    private static void requireText(
            String extractedText,
            String expectedText,
            String description
    ) {
        if (!extractedText.contains(expectedText)) {
            throw new IllegalStateException(
                    "PDF does not contain the expected "
                            + description
                            + ": "
                            + expectedText
            );
        }
    }
}