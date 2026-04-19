package com.berrybyte.RPT.controllers;

import com.berrybyte.RPT.email.ReportEmailService;
import com.berrybyte.RPT.email.ReportEmailServiceImpl;
import com.berrybyte.RPT.export.MerchantOrderSummaryPdfService;
import com.berrybyte.RPT.model.MerchantOption;
import com.berrybyte.RPT.model.MerchantOrderSummaryReport;
import com.berrybyte.RPT.model.MerchantOrderSummaryRow;
import com.berrybyte.RPT.repository.ReportRepository;
import com.berrybyte.RPT.repository.ReportRepositoryImpl;
import com.berrybyte.RPT.services.ReportService;
import com.berrybyte.RPT.services.ReportServiceImpl;
import com.berrybyte.common.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/**
 * Represents merchant order summary report controller.
 */
public class MerchantOrderSummaryReportController extends ReportProfileMenuController {

    private static final String QUEUE_RECIPIENT_EMAIL = "ipos_commercial@yahoo.com";

    private final ReportRepository reportRepository = new ReportRepositoryImpl();
    private final ReportService reportService = new ReportServiceImpl(reportRepository);
    private final MerchantOrderSummaryPdfService merchantOrderSummaryPdfService = new MerchantOrderSummaryPdfService();
    private final ReportEmailService reportEmailService = new ReportEmailServiceImpl();

    private Integer merchantId;
    private String merchantName;
    private LocalDate afterDate;
    private LocalDate beforeDate;
    private MerchantOrderSummaryReport currentReport;

    @FXML
    private Label filterSummaryLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<MerchantOption> merchantComboBox;

    @FXML
    private DatePicker afterDatePicker;

    @FXML
    private DatePicker beforeDatePicker;

    @FXML
    private TableView<MerchantOrderSummaryRow> merchantOrderTable;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, Integer> orderIdColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, LocalDate> orderDateColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, Number> totalAmountColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> dispatchColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> deliveredColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> statusColumn;

    @FXML
    private TableColumn<MerchantOrderSummaryRow, String> paymentStatusColumn;

    @FXML
    private Button refreshButton;

    @FXML
    private Button exportPdfButton;

    @FXML
    private Button sendEmailButton;

    @FXML
    private Button backButton;

/**
 * Initializes controller state and bindings.
 *
 */
    @FXML
    public void initialize() {
        initializeProfileMenu();
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        dispatchColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getDispatchDateTime() == null
                    ? ""
                    : cellData.getValue().getDispatchDateTime().toString();
            return new SimpleStringProperty(value);
        });

        deliveredColumn.setCellValueFactory(cellData -> {
            String value = cellData.getValue().getDeliveredDateTime() == null
                    ? ""
                    : cellData.getValue().getDeliveredDateTime().toString();
            return new SimpleStringProperty(value);
        });
        bindColumnWidths();

        configureMerchantDropdown();
        loadMerchantOptions();
        updateFilterSummary();
    }
/**
 * Performs set filters.
 * This method coordinates the main operation for this action.
 *
 * @param merchantId merchant id
 * @param merchantName merchant name
 * @param afterDate after date
 * @param beforeDate before date
 */

    public void setFilters(Integer merchantId, String merchantName, LocalDate afterDate, LocalDate beforeDate) {
        this.merchantId = merchantId;
        this.merchantName = merchantName == null ? "" : merchantName.trim();
        this.afterDate = afterDate;
        this.beforeDate = beforeDate;
        applyFiltersToControls();
        updateFilterSummary();
        loadReport();
    }

/**
 * Handles refresh.
 *
 */
    @FXML
    private void handleRefresh() {
        if (!captureFiltersFromControls()) {
            return;
        }
        updateFilterSummary();
        loadReport();
    }

/**
 * Handles export pdf.
 *
 */
    @FXML
    private void handleExportPdf() {
        try {
            if (currentReport == null) {
                messageLabel.setText("No report loaded.");
                return;
            }

            Path pdfPath = merchantOrderSummaryPdfService.generateMerchantOrderSummaryPdf(currentReport);
            if (pdfPath == null || !java.nio.file.Files.exists(pdfPath)) {
                messageLabel.setText("PDF export failed.");
                return;
            }
            merchantOrderSummaryPdfService.openMerchantOrderSummaryPdf(pdfPath);
            messageLabel.setText("PDF opened: " + pdfPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to export PDF.");
        }
    }

/**
 * Handles send email.
 *
 */
    @FXML
    private void handleSendEmail() {
        try {
            if (currentReport == null) {
                messageLabel.setText("No report loaded.");
                return;
            }

            Path pdfPath = merchantOrderSummaryPdfService.generateMerchantOrderSummaryPdf(currentReport);

            String subject = "Merchant Order Report";
            String body = "Merchant order report generated by IPOS-SA.";

            reportEmailService.sendReportEmail(QUEUE_RECIPIENT_EMAIL, subject, body, pdfPath);

            messageLabel.setText("Email queued for: " + QUEUE_RECIPIENT_EMAIL);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to queue email.");
        }
    }

/**
 * Handles back.
 *
 * @param event event
 */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            SceneSwitcher.switchScene(event, "/RPT/reportsMenu.fxml", "Reports");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to go back.");
        }
    }
/**
 * Loads report.
 *
 */

    private void loadReport() {
        try {
            if (merchantId == null) {
                merchantOrderTable.setItems(FXCollections.observableArrayList());
                messageLabel.setText("Select a merchant from the reports menu.");
                return;
            }

            LocalDate startDate = afterDate != null ? afterDate : LocalDate.of(2000, 1, 1);
            LocalDate endDate = beforeDate != null ? beforeDate : LocalDate.now();

            currentReport = reportService.generateMerchantOrderSummary(merchantId, startDate, endDate);
            merchantOrderTable.setItems(FXCollections.observableArrayList(currentReport.getRows()));
            messageLabel.setText("Loaded " + currentReport.getRows().size() + " order(s).");
        } catch (Exception e) {
            e.printStackTrace();
            merchantOrderTable.setItems(FXCollections.observableArrayList());
            messageLabel.setText("Unable to load merchant order report.");
        }
    }
/**
 * Executes the update filter summary workflow.
 * This method coordinates the main operation for this action.
 *
 */

    private void updateFilterSummary() {
        String merchantText = (merchantName == null || merchantName.isBlank()) ? "Merchant required" : merchantName;
        String afterText = afterDate == null ? "Any" : afterDate.toString();
        String beforeText = beforeDate == null ? "Any" : beforeDate.toString();

        filterSummaryLabel.setText("Merchant: " + merchantText + " | After: " + afterText + " | Before: " + beforeText);
    }
/**
 * Performs bind column widths.
 *
 */

    private void bindColumnWidths() {
        orderIdColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.10));
        orderDateColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.1875));
        totalAmountColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.18375));
        dispatchColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.13375));
        deliveredColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.1375));
        statusColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.1175));
        paymentStatusColumn.prefWidthProperty().bind(merchantOrderTable.widthProperty().multiply(0.14));
    }
/**
 * Loads merchant options.
 *
 */

    private void loadMerchantOptions() {
        try {
            if (merchantComboBox == null) {
                return;
            }

            List<MerchantOption> merchants = reportRepository.findMerchantOptions();
            merchantComboBox.setItems(FXCollections.observableArrayList(merchants));
            selectMerchantInDropdown();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load merchant options.");
        }
    }
/**
 * Performs apply filters to controls.
 *
 */

    private void applyFiltersToControls() {
        if (afterDatePicker != null) {
            afterDatePicker.setValue(afterDate);
        }

        if (beforeDatePicker != null) {
            beforeDatePicker.setValue(beforeDate);
        }

        selectMerchantInDropdown();
    }
/**
 * Performs select merchant in dropdown.
 *
 */

    private void selectMerchantInDropdown() {
        if (merchantComboBox == null || merchantComboBox.getItems() == null) {
            return;
        }

        if (merchantId == null) {
            merchantComboBox.getSelectionModel().clearSelection();
            if (merchantComboBox.getEditor() != null) {
                merchantComboBox.getEditor().clear();
            }
            return;
        }

        for (MerchantOption option : merchantComboBox.getItems()) {
            if (option.getMerchantId() == merchantId) {
                merchantComboBox.getSelectionModel().select(option);
                merchantName = option.getCompanyName();
                return;
            }
        }
    }
/**
 * Performs capture filters from controls.
 *
 * @return result value
 */

    private boolean captureFiltersFromControls() {
        MerchantOption selectedMerchant = resolveSelectedMerchant();
        merchantId = selectedMerchant == null ? null : selectedMerchant.getMerchantId();
        merchantName = selectedMerchant == null ? "" : selectedMerchant.getCompanyName();
        afterDate = afterDatePicker == null ? null : afterDatePicker.getValue();
        beforeDate = beforeDatePicker == null ? null : beforeDatePicker.getValue();

        if (afterDate != null && beforeDate != null && afterDate.isAfter(beforeDate)) {
            messageLabel.setText("After date cannot be later than Before date.");
            return false;
        }

        return true;
    }
/**
 * Performs configure merchant dropdown.
 *
 */

    private void configureMerchantDropdown() {
        if (merchantComboBox == null) {
            return;
        }

        merchantComboBox.setEditable(true);
        merchantComboBox.setConverter(new StringConverter<>() {
/**
 * Performs to string.
 *
 * @param option option
 * @return result value
 */
            @Override
            public String toString(MerchantOption option) {
                return option == null ? "" : option.getCompanyName();
            }

/**
 * Performs from string.
 *
 * @param value value
 * @return result value
 */
            @Override
            public MerchantOption fromString(String value) {
                if (value == null) {
                    return null;
                }

                String search = value.trim().toLowerCase(Locale.ROOT);
                if (search.isEmpty()) {
                    return null;
                }

                for (MerchantOption option : merchantComboBox.getItems()) {
                    if (option.getCompanyName().equalsIgnoreCase(search)) {
                        return option;
                    }
                }

                for (MerchantOption option : merchantComboBox.getItems()) {
                    if (option.getCompanyName().toLowerCase(Locale.ROOT).contains(search)) {
                        return option;
                    }
                }

                return null;
            }
        });
    }
/**
 * Performs resolve selected merchant.
 *
 * @return result value
 */

    private MerchantOption resolveSelectedMerchant() {
        if (merchantComboBox == null) {
            return null;
        }

        Object rawValue = ((ComboBox<?>) merchantComboBox).getValue();
        if (rawValue instanceof MerchantOption selected) {
            return selected;
        }

        if (merchantComboBox.getEditor() == null) {
            return null;
        }

        String typedText = merchantComboBox.getEditor().getText();
        if (typedText == null) {
            return null;
        }

        String search = typedText.trim().toLowerCase(Locale.ROOT);
        if (search.isEmpty()) {
            return null;
        }

        for (MerchantOption option : merchantComboBox.getItems()) {
            if (option.getCompanyName().equalsIgnoreCase(search)) {
                return option;
            }
        }

        for (MerchantOption option : merchantComboBox.getItems()) {
            if (option.getCompanyName().toLowerCase(Locale.ROOT).contains(search)) {
                return option;
            }
        }

        return null;
    }
}
