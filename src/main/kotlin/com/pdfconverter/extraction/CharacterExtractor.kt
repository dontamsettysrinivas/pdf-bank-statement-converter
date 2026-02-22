package com.pdfconverter.extraction

import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition
import java.awt.geom.Rectangle2D
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter

/**
 * Extracts characters with their bounding boxes from a PDF page.
 * Implements the first step of the PDF extraction pipeline.
 */
class CharacterExtractor {

    /**
     * Extracts all characters from a PDF page with their positions
     *
     * @param page The PDF page to process
     * @return List of CharAndBound objects containing character data
     */
    fun extractCharacters(page: PDPage): List<CharAndBound> {
        val characters = mutableListOf<CharAndBound>()
        
        // Create a custom PDFTextStripper to capture character positions
        val stripper = object : PDFTextStripper() {
            override fun writeString(text: String, textPositions: MutableList<TextPosition>) {
                for (textPosition in textPositions) {
                    val char = textPosition.unicode
                    
                    // Skip empty or whitespace-only characters
                    if (char.isBlank()) continue
                    
                    // Extract bounding box information
                    val x = textPosition.xDirAdj
                    val y = textPosition.yDirAdj
                    val width = textPosition.widthDirAdj
                    val height = textPosition.heightDir
                    
                    // Create bounding rectangle
                    val bound = Rectangle2D.Float(x, y, width, height)
                    
                    // Store character with its bounding box
                    characters.add(
                        CharAndBound(
                            char = char.first(),
                            bound = bound,
                            fontCode = textPosition.font.name ?: "",
                            rotation = textPosition.rotation.toInt(),
                            color = textPosition.font.fontDescriptor?.fontWeight ?: 0
                        )
                    )
                }
            }
        }
        
        // Process the page
        val output = ByteArrayOutputStream()
        val writer = OutputStreamWriter(output)
        stripper.output = writer
        stripper.processPage(page)
        writer.close()
        
        return characters
    }
}

/**
 * Data class representing a character with its bounding box
 * 
 * @property char The character value
 * @property bound The bounding rectangle on the page
 * @property fontCode The font code for this character
 * @property rotation Page rotation (0, 90, 180, 270)
 * @property color RGB color value
 */
data class CharAndBound(
    val char: Char,
    val bound: Rectangle2D.Float,
    val fontCode: String = "",
    val rotation: Int = 0,
    val color: Int = 0
)
