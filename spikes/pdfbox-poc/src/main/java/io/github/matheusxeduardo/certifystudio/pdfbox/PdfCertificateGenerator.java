package io.github.matheusxeduardo.certifystudio.pdfbox;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

public final class PdfCertificateGenerator {

    private static final int BACKGROUND_WIDTH = 1_754;
    private static final int BACKGROUND_HEIGHT = 1_240;

    private static final String REGULAR_FONT_RESOURCE =
            "/fonts/DejaVuSans.ttf";

    private static final String BOLD_FONT_RESOURCE =
            "/fonts/DejaVuSans-Bold.ttf";

    private static final Color PRIMARY_COLOR =
            new Color(15, 76, 129);

    private static final Color TITLE_COLOR =
            new Color(24, 42, 66);

    private static final Color TEXT_COLOR =
            new Color(55, 65, 81);

    private static final Color MUTED_TEXT_COLOR =
            new Color(75, 85, 99);

    private static final float OUTER_MARGIN = 18;
    private static final float INNER_MARGIN = 27;
    private static final float TEXT_HORIZONTAL_MARGIN = 70;

    private static final float BRAND_Y = 535;
    private static final float TITLE_Y = 475;
    private static final float INTRODUCTION_Y = 430;
    private static final float PARTICIPANT_NAME_Y = 370;
    private static final float COURSE_INTRODUCTION_Y = 320;
    private static final float COURSE_NAME_Y = 280;
    private static final float COMPLETION_INFORMATION_Y = 235;
    private static final float VALIDATION_CODE_Y = 55;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd 'de' MMMM 'de' yyyy",
                    Locale.forLanguageTag("pt-BR")
            );

    public GenerationResult generate(
            CertificateData data,
            Path pdfOutputPath,
            Path backgroundOutputPath
    ) throws IOException {
        Objects.requireNonNull(data, "data is required");
        Objects.requireNonNull(
                pdfOutputPath,
                "pdfOutputPath is required"
        );
        Objects.requireNonNull(
                backgroundOutputPath,
                "backgroundOutputPath is required"
        );

        Path normalizedPdfPath = normalize(pdfOutputPath);
        Path normalizedBackgroundPath = normalize(backgroundOutputPath);

        validateDifferentPaths(
                normalizedPdfPath,
                normalizedBackgroundPath
        );

        createParentDirectory(normalizedPdfPath);
        createParentDirectory(normalizedBackgroundPath);

        long startTime = System.nanoTime();

        BufferedImage background = createAndSaveBackground(
                normalizedBackgroundPath
        );

        generatePdf(
                data,
                normalizedPdfPath,
                background
        );

        long durationNanos = System.nanoTime() - startTime;

        return new GenerationResult(
                normalizedPdfPath,
                normalizedBackgroundPath,
                durationNanos
        );
    }

    private static BufferedImage createAndSaveBackground(
            Path backgroundOutputPath
    ) throws IOException {
        BufferedImage background =
                CertificateBackgroundRenderer.render(
                        BACKGROUND_WIDTH,
                        BACKGROUND_HEIGHT
                );

        boolean written = ImageIO.write(
                background,
                "png",
                backgroundOutputPath.toFile()
        );

        if (!written) {
            throw new IOException(
                    "No PNG writer was found for the certificate background"
            );
        }

        return background;
    }

    private static void generatePdf(
            CertificateData data,
            Path pdfOutputPath,
            BufferedImage background
    ) throws IOException {
        try (PDDocument document = new PDDocument();
             InputStream regularFontStream =
                     openResource(REGULAR_FONT_RESOURCE);
             InputStream boldFontStream =
                     openResource(BOLD_FONT_RESOURCE)) {

            PDPage page = createLandscapeA4Page();
            document.addPage(page);

            PDFont regularFont = PDType0Font.load(
                    document,
                    regularFontStream
            );

            PDFont boldFont = PDType0Font.load(
                    document,
                    boldFontStream
            );

            PDImageXObject backgroundImage =
                    LosslessFactory.createFromImage(
                            document,
                            background
                    );

            drawCertificate(
                    document,
                    page,
                    backgroundImage,
                    regularFont,
                    boldFont,
                    data
            );

            document.save(pdfOutputPath.toFile());
        } catch (IOException exception) {
            throw new IOException(
                    "Could not generate certificate at: "
                            + pdfOutputPath,
                    exception
            );
        }
    }

    private static PDPage createLandscapeA4Page() {
        PDRectangle landscapeA4 = new PDRectangle(
                PDRectangle.A4.getHeight(),
                PDRectangle.A4.getWidth()
        );

        return new PDPage(landscapeA4);
    }

    private static void drawCertificate(
            PDDocument document,
            PDPage page,
            PDImageXObject backgroundImage,
            PDFont regularFont,
            PDFont boldFont,
            CertificateData data
    ) throws IOException {
        float pageWidth = page.getMediaBox().getWidth();
        float pageHeight = page.getMediaBox().getHeight();
        float maximumTextWidth =
                pageWidth - (TEXT_HORIZONTAL_MARGIN * 2);

        try (PDPageContentStream content =
                     new PDPageContentStream(document, page)) {

            content.drawImage(
                    backgroundImage,
                    0,
                    0,
                    pageWidth,
                    pageHeight
            );

            drawBorders(content, pageWidth, pageHeight);

            drawCenteredText(
                    content,
                    boldFont,
                    14,
                    14,
                    maximumTextWidth,
                    pageWidth,
                    BRAND_Y,
                    PRIMARY_COLOR,
                    "CERTIFY STUDIO"
            );

            drawCenteredText(
                    content,
                    boldFont,
                    30,
                    24,
                    maximumTextWidth,
                    pageWidth,
                    TITLE_Y,
                    TITLE_COLOR,
                    "CERTIFICADO"
            );

            drawCenteredText(
                    content,
                    regularFont,
                    14,
                    11,
                    maximumTextWidth,
                    pageWidth,
                    INTRODUCTION_Y,
                    TEXT_COLOR,
                    "Certificamos que"
            );

            drawCenteredText(
                    content,
                    boldFont,
                    26,
                    16,
                    maximumTextWidth,
                    pageWidth,
                    PARTICIPANT_NAME_Y,
                    PRIMARY_COLOR,
                    data.participantName()
            );

            drawCenteredText(
                    content,
                    regularFont,
                    14,
                    11,
                    maximumTextWidth,
                    pageWidth,
                    COURSE_INTRODUCTION_Y,
                    TEXT_COLOR,
                    "concluiu com aproveitamento o curso"
            );

            drawCenteredText(
                    content,
                    boldFont,
                    18,
                    12,
                    maximumTextWidth,
                    pageWidth,
                    COURSE_NAME_Y,
                    TITLE_COLOR,
                    data.courseName()
            );

            String completionInformation = String.format(
                    "Carga horária: %d horas — Conclusão: %s",
                    data.workloadHours(),
                    data.completionDate().format(DATE_FORMATTER)
            );

            drawCenteredText(
                    content,
                    regularFont,
                    12,
                    9,
                    maximumTextWidth,
                    pageWidth,
                    COMPLETION_INFORMATION_Y,
                    TEXT_COLOR,
                    completionInformation
            );

            drawSignature(
                    content,
                    regularFont,
                    boldFont,
                    pageWidth,
                    maximumTextWidth,
                    data.instructorName()
            );

            drawCenteredText(
                    content,
                    regularFont,
                    9,
                    7,
                    maximumTextWidth,
                    pageWidth,
                    VALIDATION_CODE_Y,
                    MUTED_TEXT_COLOR,
                    "Código de validação: "
                            + data.validationCode()
            );
        }
    }

    private static void drawBorders(
            PDPageContentStream content,
            float pageWidth,
            float pageHeight
    ) throws IOException {
        content.setStrokingColor(PRIMARY_COLOR);

        content.setLineWidth(3);
        content.addRect(
                OUTER_MARGIN,
                OUTER_MARGIN,
                pageWidth - (OUTER_MARGIN * 2),
                pageHeight - (OUTER_MARGIN * 2)
        );
        content.stroke();

        content.setLineWidth(1);
        content.addRect(
                INNER_MARGIN,
                INNER_MARGIN,
                pageWidth - (INNER_MARGIN * 2),
                pageHeight - (INNER_MARGIN * 2)
        );
        content.stroke();
    }

    private static void drawSignature(
            PDPageContentStream content,
            PDFont regularFont,
            PDFont boldFont,
            float pageWidth,
            float maximumTextWidth,
            String instructorName
    ) throws IOException {
        float signatureLineWidth = 220;
        float signatureLineStartX =
                (pageWidth - signatureLineWidth) / 2;

        content.setStrokingColor(TEXT_COLOR);
        content.setLineWidth(1);
        content.moveTo(signatureLineStartX, 155);
        content.lineTo(
                signatureLineStartX + signatureLineWidth,
                155
        );
        content.stroke();

        drawCenteredText(
                content,
                boldFont,
                11,
                8,
                maximumTextWidth,
                pageWidth,
                135,
                TITLE_COLOR,
                instructorName
        );

        drawCenteredText(
                content,
                regularFont,
                9,
                7,
                maximumTextWidth,
                pageWidth,
                118,
                MUTED_TEXT_COLOR,
                "Instrutor responsável"
        );
    }

    private static void drawCenteredText(
            PDPageContentStream content,
            PDFont font,
            float preferredFontSize,
            float minimumFontSize,
            float maximumWidth,
            float pageWidth,
            float y,
            Color color,
            String text
    ) throws IOException {
        float fontSize = calculateFontSize(
                font,
                text,
                preferredFontSize,
                minimumFontSize,
                maximumWidth
        );

        float textWidth = calculateTextWidth(
                font,
                text,
                fontSize
        );

        float x = (pageWidth - textWidth) / 2;

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(x, y);
        content.showText(text);
        content.endText();
    }

    private static float calculateFontSize(
            PDFont font,
            String text,
            float preferredFontSize,
            float minimumFontSize,
            float maximumWidth
    ) throws IOException {
        float fontSize = preferredFontSize;

        while (fontSize > minimumFontSize
                && calculateTextWidth(font, text, fontSize)
                > maximumWidth) {
            fontSize -= 0.5f;
        }

        if (calculateTextWidth(font, text, fontSize)
                > maximumWidth) {
            throw new IllegalArgumentException(
                    "Text is too long to fit in the certificate: "
                            + text
            );
        }

        return fontSize;
    }

    private static float calculateTextWidth(
            PDFont font,
            String text,
            float fontSize
    ) throws IOException {
        return font.getStringWidth(text) / 1_000 * fontSize;
    }

    private static InputStream openResource(
            String resourcePath
    ) throws IOException {
        InputStream resource =
                PdfCertificateGenerator.class.getResourceAsStream(
                        resourcePath
                );

        if (resource == null) {
            throw new FileNotFoundException(
                    "Required resource was not found: "
                            + resourcePath
            );
        }

        return resource;
    }

    private static Path normalize(Path path) {
        return path.toAbsolutePath().normalize();
    }

    private static void validateDifferentPaths(
            Path pdfPath,
            Path backgroundPath
    ) {
        if (pdfPath.equals(backgroundPath)) {
            throw new IllegalArgumentException(
                    "PDF and background must use different output paths"
            );
        }
    }

    private static void createParentDirectory(
            Path path
    ) throws IOException {
        Path parent = path.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}