# PDF Bank Statement Converter

A Kotlin-based PDF bank statement converter that extracts transaction data and converts to CSV. Built following Angus Cheng's Bank Statement Converter architecture with PDFBox for PDF processing.

## Overview

This project replicates the core functionality of [Bank Statement Converter](https://bankstatementconverter.com) - a sophisticated PDF extraction tool that handles bank statements from various banks and formats. It demonstrates:

- PDF text extraction with precise bounding boxes
- Intelligent header detection using keyword matching
- Column-based transaction mapping
- CSV export functionality

## Architecture

### Core Components

```
src/main/kotlin/com/pdfconverter/
├── extraction/
│   ├── CharacterExtractor.kt      # Extract characters and bounding boxes
│   ├── WordMerger.kt               # Merge characters into words
│   └── LineExtractor.kt            # Group words into lines
├── headers/
│   ├── HeaderDetector.kt           # Detect transaction table headers
│   ├── HeaderMatcher.kt            # Match text to column types
│   └── ColumnType.kt               # Column type definitions
├── mapping/
│   ├── TransactionMapper.kt        # Map words to columns
│   ├── GridBuilder.kt              # Build transaction grid
│   └── XRange.kt                   # Column boundary definitions
├── export/
│   ├── CSVExporter.kt              # Export to CSV format
│   └── Transaction.kt              # Transaction data class
├── models/
│   ├── CharAndBound.kt             # Character with position
│   ├── TextAndBound.kt             # Text with bounding box
│   └── TableHeader.kt              # Header definition
└── BankStatementConverter.kt       # Main converter class
```

## How It Works

### Step 1: Extract Characters & Bounding Boxes
Uses Apache PDFBox to extract each character with its precise position on the page.

```kotlin
// FastCharacterParser listens to PDF rendering events
// Calculates character position, font, rotation, etc.
val characters = fastCharacterParser.stripPage(page)
```

### Step 2: Merge into Words
Groups characters into words based on horizontal and vertical distance thresholds.

```kotlin
val xDistance = current.bound.left() - previous.bound.right()
val yDistance = Math.abs(current.bound.bottom() - previous.bound.bottom())
if (xDistance > threshold || yDistance > threshold) {
    // Start new word
}
```

### Step 3: Detect Headers
Uses keyword matching to identify transaction table headers (Date, Description, Debit, Credit, Balance, etc.).

```kotlin
private val dateHeaders = listOf("date", "datum", "posted", "tanggal")
private val creditHeaders = listOf("credit", "deposit", "paid in")
private val debitHeaders = listOf("debit", "withdrawal", "paid out")
```

### Step 4: Map Transactions
Associates each transaction value with the correct column using XRange (column boundary) intersection.

### Step 5: Export to CSV
Formats and exports the transaction data as CSV.

## Features

- ✅ Multi-page PDF support
- ✅ Intelligent header detection (works across 1-3 lines)
- ✅ Multi-language support (keywords in multiple languages)
- ✅ Handles complex PDF layouts
- ✅ CSV export with proper formatting
- ✅ Robust error handling

## Technology Stack

- **Language**: Kotlin
- **PDF Processing**: Apache PDFBox 2.x
- **Build System**: Gradle
- **Testing**: JUnit 5
- **CSV Generation**: OpenCSV

## Installation

### Prerequisites
- JDK 11 or higher
- Gradle 7.0+

### Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/pdf-bank-statement-converter.git
cd pdf-bank-statement-converter

# Build the project
./gradlew build

# Run tests
./gradlew test
```

## Usage

```kotlin
val converter = BankStatementConverter()
val transactions = converter.extractTransactions("/path/to/statement.pdf")
converter.exportToCSV(transactions, "/path/to/output.csv")
```

## Project Structure

```
pdf-bank-statement-converter/
├── src/
│   ├── main/kotlin/          # Source code
│   └── test/kotlin/          # Unit tests
├── build.gradle.kts          # Gradle build configuration
├── README.md                  # This file
├── LICENSE                    # MIT License
└── .gitignore               # Git ignore rules
```

## Development Roadmap

- [ ] Phase 1: Core extraction engine (character → words → lines → headers)
- [ ] Phase 2: Transaction mapping and CSV export
- [ ] Phase 3: Bank-specific parsers (HSBC, NAB, etc.)
- [ ] Phase 4: Web UI with file upload
- [ ] Phase 5: JSON API support
- [ ] Phase 6: Performance optimization

## Collaboration Guidelines

### How to Contribute

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/your-feature`
3. **Make changes** and commit: `git commit -am 'Add feature'`
4. **Push** to branch: `git push origin feature/your-feature`
5. **Submit** a Pull Request

### Code Standards

- Follow Kotlin style guide
- Write tests for new functionality
- Use meaningful commit messages
- Document complex logic with comments

### Pair Programming

We recommend using:
- **GitHub Live Sharing** for real-time collaboration
- **VS Code Live Share** extension for pair coding
- **Replit** for quick prototyping and testing

## Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests HeaderDetectorTest

# Run with coverage
./gradlew test jacocoTestReport
```

## References

- [Bank Statement Converter Blog](https://bankstatementconverter.com/blog/)
- [Angus Cheng's Book: How I Created Bank Statement Converter](https://bankstatementconverter.com/book)
- [Apache PDFBox Documentation](https://pdfbox.apache.org/)
- [Kotlin Language Guide](https://kotlinlang.org/docs/)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Inspiration

This project is inspired by and follows the architecture described in [Angus Cheng's Bank Statement Converter](https://bankstatementconverter.com), a production application successfully handling thousands of bank statement formats.

## Status

🚀 **Project Status**: Active Development

We're building this step-by-step. Check the Issues and Projects tab for what we're working on.

---

**Ready to contribute?** Start with good-first-issue label or reach out to discuss ideas!
