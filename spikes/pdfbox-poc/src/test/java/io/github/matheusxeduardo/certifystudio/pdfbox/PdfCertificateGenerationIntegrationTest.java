package io.github.matheusxeduardo.certifystudio.pdfbox;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfCertificateGenerationIntegrationTest {

    private static final float PAGE_DIMENSION_TOLERANCE = 0.5f;
    private static final int EXPECTED_BACKGROUND_WIDTH = 1_754;
    private static final int EXPECTED_BACKGROUND_HEIGHT = 1_240;

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldGenerateAndReopenLandscapeA4Certificate()
            throws IOException {
        // Arrange
        CertificateData data = createCertificateData();

        Path pdfPath =
                temporaryDirectory.resolve("certificate.pdf");

        Path backgroundPath =
                temporaryDirectory.resolve("background.png");

        PdfCertificateGenerator generator =
                new PdfCertificateGenerator();

        PdfCertificateValidator validator =
                new PdfCertificateValidator();

        // Act
        GenerationResult generation = generator.generate(
                data,
                pdfPath,
                backgroundPath
        );

        PdfValidationResult validation = validator.validate(
                generation.pdfPath(),
                data
        );

        BufferedImage background = ImageIO.read(
                generation.backgroundPath().toFile()
        );

        // Assert
        assertAll(
                () -> assertGeneratedPdf(generation),
                () -> assertGeneratedBackground(
                        generation,
                        background
                ),
                () -> assertValidationResult(
                        generation,
                        validation
                )
        );
    }

    @Test
    void shouldRejectSamePathForPdfAndBackground() {
        // Arrange
        CertificateData data = createCertificateData();

        Path duplicatedPath =
                temporaryDirectory.resolve("output.bin");

        PdfCertificateGenerator generator =
                new PdfCertificateGenerator();

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(
                        data,
                        duplicatedPath,
                        duplicatedPath
                )
        );

        // Assert
        assertTrue(
                exception.getMessage().contains(
                        "different output paths"
                )
        );
    }

    private static CertificateData createCertificateData() {
        return new CertificateData(
                "Matheus Eduardo",
                "Segurança no Trabalho em Altura — NR-35",
                8,
                LocalDate.of(2026, 8, 31),
                "Mariana Oliveira",
                "CERT-2026-000001"
        );
    }

    private static void assertGeneratedPdf(
            GenerationResult generation
    ) throws IOException {
        assertAll(
                () -> assertTrue(
                        Files.isRegularFile(generation.pdfPath()),
                        "The PDF file should exist"
                ),
                () -> assertTrue(
                        Files.size(generation.pdfPath()) > 0,
                        "The PDF file should not be empty"
                ),
                () -> assertEquals(
                        "certificate.pdf",
                        generation.pdfPath()
                                .getFileName()
                                .toString()
                ),
                () -> assertTrue(
                        generation.durationNanos() > 0,
                        "Generation duration should be measured"
                )
        );
    }

    private static void assertGeneratedBackground(
            GenerationResult generation,
            BufferedImage background
    ) {
        assertAll(
                () -> assertTrue(
                        Files.isRegularFile(
                                generation.backgroundPath()
                        ),
                        "The background file should exist"
                ),
                () -> assertNotNull(
                        background,
                        "The background should be a readable image"
                ),
                () -> assertEquals(
                        EXPECTED_BACKGROUND_WIDTH,
                        background.getWidth()
                ),
                () -> assertEquals(
                        EXPECTED_BACKGROUND_HEIGHT,
                        background.getHeight()
                )
        );
    }

    private static void assertValidationResult(
            GenerationResult generation,
            PdfValidationResult validation
    ) {
        assertAll(
                () -> assertEquals(
                        generation.pdfPath(),
                        validation.pdfPath()
                ),
                () -> assertEquals(
                        1,
                        validation.pageCount()
                ),
                () -> assertEquals(
                        PDRectangle.A4.getHeight(),
                        validation.pageWidth(),
                        PAGE_DIMENSION_TOLERANCE
                ),
                () -> assertEquals(
                        PDRectangle.A4.getWidth(),
                        validation.pageHeight(),
                        PAGE_DIMENSION_TOLERANCE
                ),
                () -> assertTrue(
                        validation.fileSizeBytes() > 0
                )
        );
    }
}