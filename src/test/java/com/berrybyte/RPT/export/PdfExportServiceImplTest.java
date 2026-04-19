package com.berrybyte.RPT.export;

import com.berrybyte.RPT.model.LowStockItem;
import com.berrybyte.RPT.model.LowStockReport;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfExportServiceImplTest {

    private final LowStockPdfService service = new LowStockPdfService();

    @Test
    void generateLowStockPdf_nullReport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> service.generateLowStockPdf(null));
    }

    @Test
    void generateLowStockPdf_validReport_createsPdfFile() throws Exception {
        LowStockReport report = new LowStockReport(
                "Low Stock Report",
                LocalDateTime.now(),
                List.of(new LowStockItem(10000001, "Paracetamol 500mg", 5, 20))
        );

        Path pdfPath = service.generateLowStockPdf(report);

        try {
            assertTrue(Files.exists(pdfPath), "PDF file should exist");
            assertTrue(Files.size(pdfPath) > 0, "PDF file should not be empty");
        } finally {
            Files.deleteIfExists(pdfPath);
        }
    }

    @Test
    void generateLowStockPdf_emptyItemList_stillCreatesPdfFile() throws Exception {
        LowStockReport report = new LowStockReport(
                "Low Stock Report",
                LocalDateTime.now(),
                List.of()
        );

        Path pdfPath = service.generateLowStockPdf(report);

        try {
            assertTrue(Files.exists(pdfPath), "PDF file should exist even with no items");
            assertTrue(Files.size(pdfPath) > 0, "PDF file should not be empty");
        } finally {
            Files.deleteIfExists(pdfPath);
        }
    }

    @Test
    void generateLowStockPdf_multipleItems_createsPdfFile() throws Exception {
        LowStockReport report = new LowStockReport(
                "Low Stock Report",
                LocalDateTime.now(),
                List.of(
                        new LowStockItem(10000001, "Paracetamol 500mg", 3, 50),
                        new LowStockItem(10000002, "Ibuprofen 200mg", 1, 30),
                        new LowStockItem(10000003, "Aspirin 75mg", 0, 25)
                )
        );

        Path pdfPath = service.generateLowStockPdf(report);

        try {
            assertTrue(Files.exists(pdfPath), "PDF file should exist");
            assertTrue(Files.size(pdfPath) > 0, "PDF file should not be empty");
        } finally {
            Files.deleteIfExists(pdfPath);
        }
    }
}
