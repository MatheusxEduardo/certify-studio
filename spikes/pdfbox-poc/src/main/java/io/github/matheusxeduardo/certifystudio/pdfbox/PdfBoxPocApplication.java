package io.github.matheusxeduardo.certifystudio.pdfbox;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

public final class PdfBoxPocApplication {

    private PdfBoxPocApplication() {
    }

    public static void main(String[] args) throws IOException {
        Path outputDirectory = resolveOutputDirectory(args);

        Path pdfOutputPath =
                outputDirectory.resolve("certificate-poc.pdf");

        Path backgroundOutputPath =
                outputDirectory.resolve(
                        "certificate-background.png"
                );

        CertificateData certificateData =
                createSampleCertificateData();

        PdfCertificateGenerator generator =
                new PdfCertificateGenerator();

        GenerationResult generationResult = generator.generate(
                certificateData,
                pdfOutputPath,
                backgroundOutputPath
        );

        PdfCertificateValidator validator =
                new PdfCertificateValidator();

        PdfValidationResult validationResult =
                validator.validate(
                        generationResult.pdfPath(),
                        certificateData
                );

        printResult(
                generationResult,
                validationResult
        );
    }

    private static Path resolveOutputDirectory(
            String[] args
    ) {
        if (args.length == 0 || args[0].isBlank()) {
            throw new IllegalArgumentException(
                    "The Maven target directory argument is required"
            );
        }

        return Path.of(args[0])
                .toAbsolutePath()
                .normalize();
    }

    private static CertificateData createSampleCertificateData() {
        return new CertificateData(
                "Matheus Eduardo",
                "Segurança no Trabalho em Altura — NR-35",
                8,
                LocalDate.of(2026, 8, 31),
                "Mariana Oliveira",
                "CERT-2026-000001"
        );
    }

    private static void printResult(
            GenerationResult generation,
            PdfValidationResult validation
    ) {
        System.out.println();
        System.out.println("PDFBox certificate POC completed");
        System.out.println("--------------------------------");

        System.out.printf(
                "Generation time: %.2f ms%n",
                generation.durationMillis()
        );

        System.out.println(
                "PDF: " + validation.pdfPath()
        );

        System.out.println(
                "Background: " + generation.backgroundPath()
        );

        System.out.println(
                "File size: " + validation.fileSizeBytes()
                        + " bytes"
        );

        System.out.println(
                "Pages: " + validation.pageCount()
        );

        System.out.printf(
                "Page dimensions: %.2f x %.2f points%n",
                validation.pageWidth(),
                validation.pageHeight()
        );

        System.out.println(
                "Validation: SUCCESS"
        );
    }
}