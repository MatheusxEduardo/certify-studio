# Apache PDFBox Certificate POC

Proof of concept created for Issue #4 to evaluate Apache PDFBox as the PDF generation library for Certify Studio.

## Objective

Validate whether Apache PDFBox can support the certificate generation requirements of Certify Studio before creating the Spring Boot application.

The POC evaluates:

- A4 landscape PDF generation;
- precise element positioning using coordinates;
- dynamically generated background image;
- borders and decorative elements;
- embedded TrueType fonts;
- dynamic certificate data;
- automatic font-size reduction for long text;
- generation time measurement;
- reopening and validating the generated PDF;
- automated integration testing.

## Technology

- Java 21
- Maven
- Apache PDFBox 3.0.8
- JUnit Jupiter 5.13.4
- DejaVu Sans TrueType fonts

The DejaVu font license notice is available at:

```text
src/main/resources/fonts/DEJAVU-LICENSE.txt
```

## Project structure

```text
pdfbox-poc/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── io/github/matheusxeduardo/certifystudio/pdfbox/
    │   │       ├── CertificateBackgroundRenderer.java
    │   │       ├── CertificateData.java
    │   │       ├── GenerationResult.java
    │   │       ├── PdfBoxPocApplication.java
    │   │       ├── PdfCertificateGenerator.java
    │   │       ├── PdfCertificateValidator.java
    │   │       └── PdfValidationResult.java
    │   └── resources/
    │       └── fonts/
    │           ├── DEJAVU-LICENSE.txt
    │           ├── DejaVuSans-Bold.ttf
    │           └── DejaVuSans.ttf
    └── test/
        └── java/
            └── io/github/matheusxeduardo/certifystudio/pdfbox/
                └── PdfCertificateGenerationIntegrationTest.java
```

## Run the automated tests

From the Certify Studio repository root:

```bash
mvn -f spikes/pdfbox-poc/pom.xml clean test
```

The tests verify:

- PDF and background file creation;
- non-empty PDF output;
- readable PNG background;
- expected background dimensions;
- successful PDF reopening;
- single-page output;
- A4 landscape dimensions;
- presence of dynamic certificate text;
- rejection of identical PDF and background paths.

## Run the POC

From the Certify Studio repository root:

```bash
mvn -f spikes/pdfbox-poc/pom.xml clean compile exec:java
```

Generated files:

```text
spikes/pdfbox-poc/target/certificate-poc.pdf
spikes/pdfbox-poc/target/certificate-background.png
```

The `target/` directory is ignored by Git.

## Observed result

One local execution produced the following result:

```text
Generation time: 373.60 ms
File size: 69319 bytes
Pages: 1
Page dimensions: 841.89 x 595.28 points
Validation: SUCCESS
```

The generation time includes:

- background creation;
- TrueType font loading and embedding;
- certificate drawing;
- PDF serialization.

This measurement is informational and is not a formal performance benchmark. Results may vary depending on hardware, operating system, JVM warm-up and file-system performance.

## Validation strategy

After generation, the PDF is reopened with Apache PDFBox and checked for:

- successful file creation;
- non-zero file size;
- successful document parsing;
- expected page count;
- A4 landscape dimensions;
- participant name;
- course name;
- validation code.

This provides stronger evidence than checking only whether a file with the `.pdf` extension exists.

## Findings

Apache PDFBox successfully demonstrated:

- precise positioning of text, images and borders;
- support for A4 landscape certificates;
- TrueType font embedding;
- Unicode and accented Portuguese text;
- dynamic participant and course information;
- full-page background images;
- automatic text-size adjustment;
- programmatic PDF reopening and inspection;
- support for adding additional pages;
- support for drawing additional content over existing pages.

PDFBox uses a low-level coordinate-based API. This provides strong layout control but requires Certify Studio to implement its own layout abstractions.

Editing text already written into a finalized PDF is not equivalent to editing a Word document. Certify Studio should store editable template definitions and regenerate the PDF when certificate data or layout changes.

## Decision

Apache PDFBox is suitable for the Certify Studio MVP.

The library provides the required layout precision, supports embedded fonts and images, allows automated validation and uses the Apache License 2.0.

The production application should use PDFBox behind an application-level abstraction so that domain and service layers do not depend directly on PDFBox APIs.

## Next steps

This POC does not create the Spring Boot application.

After Issue #4 is reviewed and merged, the next implementation issue may introduce the initial Spring Boot project and Maven Wrapper according to the project roadmap.