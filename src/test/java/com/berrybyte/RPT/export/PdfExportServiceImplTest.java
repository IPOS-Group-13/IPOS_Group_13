package com.berrybyte.RPT.export;

import com.berrybyte.RPT.model.LowStockReport;
import com.berrybyte.RPT.repository.ReportRepository;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.RPT.services.ReportService;
import com.berrybyte.RPT.services.ReportServiceImpl;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfExportServiceImplTest {
/*
    @Test
    void exportLowStockReport_createsPdfFile() throws Exception {
        ReportRepository repository = new ReportRepositoryImpl();
        ReportService reportService = new ReportServiceImpl(repository);
        PdfExportService pdfExportService = new PdfExportServiceImpl();

        LowStockReport report = reportService.generateLowStockReport();

        Path tempFile = Files.createTempFile("low-stock-report-", ".pdf");

        pdfExportService.exportLowStockReport(report, tempFile.toString());

        assertTrue(Files.exists(tempFile), "PDF file should exist");
        assertTrue(Files.size(tempFile) > 0, "PDF file should not be empty");
    }
    */
}