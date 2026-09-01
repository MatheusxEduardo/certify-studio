package io.github.matheusxeduardo.certifystudio.pdfbox;

import java.nio.file.Path;

public record PdfValidationResult(
        Path pdfPath,
        int pageCount,
        float pageWidth,
        float pageHeight,
        long fileSizeBytes
) {
}