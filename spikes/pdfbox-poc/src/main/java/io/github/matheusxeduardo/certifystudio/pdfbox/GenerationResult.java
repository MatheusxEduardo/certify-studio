package io.github.matheusxeduardo.certifystudio.pdfbox;

import java.nio.file.Path;

public record GenerationResult(
        Path pdfPath,
        Path backgroundPath,
        long durationNanos
) {

    public double durationMillis() {
        return durationNanos / 1_000_000.0;
    }
}