package com.teesolutions.ipospu.controllers;

import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.dto.InventoryItemDto;
import com.teesolutions.ipospu.dto.PaymentRequest;
import com.teesolutions.ipospu.integrations.MockMemberApiClient;
import com.teesolutions.ipospu.models.CartItem;
import com.teesolutions.ipospu.models.Order;
import com.teesolutions.ipospu.models.User;
import com.teesolutions.ipospu.services.AuthService;
import com.teesolutions.ipospu.services.CampaignService;
import com.teesolutions.ipospu.services.CatalogService;
import com.teesolutions.ipospu.services.OrderService;
import com.teesolutions.ipospu.services.ReportService;
import com.teesolutions.ipospu.utils.ReportDateRange;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.stage.Window;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class PortalController {
    private static final String BRAND_LOGO_RESOURCE = "/com/teesolutions/ipospu/assets/tee-solutions-logo.png";
    private static final String PROFILE_AVATAR_RESOURCE = "/com/teesolutions/ipospu/assets/profile-avatar.png";
    private static final Pattern CARD_NUMBER_PATTERN = Pattern.compile("\\d{8,19}");
    private static final Pattern SECURITY_CODE_PATTERN = Pattern.compile("\\d{3,4}");
    private static final String DEFAULT_STOCK_CHECK_MESSAGE =
            "Final stock check is performed when you pay. If stock changes, affected items will be removed before the order is confirmed.";

    private final AuthService authService = new AuthService();
    private final CatalogService catalogService = new CatalogService();
    private final OrderService orderService = new OrderService();
    private final CampaignService campaignService = new CampaignService();
    private final ReportService reportService = new ReportService();
    private final MockMemberApiClient memberApiClient = new MockMemberApiClient();
    private final ObservableList<InventoryItemDto> catalogue = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<MapRow> campaigns = FXCollections.observableArrayList();
    private Map<String, Double> currentDiscounts = Collections.emptyMap();
    private String latestGeneratedPassword;
    private User currentUser;
    private boolean guestSessionActive;
    private Integer selectedCampaignId;
    private String lastTrackingCodeToCopy;
    private PauseTransition campaignPreviewDebounce;
    private final AtomicInteger loadingDepth = new AtomicInteger(0);

    private static final String DEFAULT_TRACKING_HINT =
            "Use the email you used at checkout and the tracking code (shown after payment and in email_outbox).";
    private static final String DEFAULT_LAST_ORDER_TEXT = "No order placed yet in this session.";

    @FXML
    private VBox authPane;
    @FXML
    private VBox authLoginPane;
    @FXML
    private VBox authRegisterNonCommercialPane;
    @FXML
    private VBox authRegisterCommercialPane;
    @FXML
    private VBox authRegistrationResultPane;
    @FXML
    private TextField regNonCommercialEmailField;
    @FXML
    private Button regNonCommercialSubmitButton;
    @FXML
    private TextField regCommercialEmailField;
    @FXML
    private TextField regCommercialCompanyField;
    @FXML
    private TextField regCommercialDirectorField;
    @FXML
    private TextField regCommercialBusinessTypeField;
    @FXML
    private TextArea regCommercialAddressArea;
    @FXML
    private Button regCommercialSubmitButton;
    @FXML
    private TextField regResultEmailField;
    @FXML
    private TextField regResultPasswordField;
    @FXML
    private ImageView headerLogoImageView;
    @FXML
    private ImageView authLogoImageView;
    @FXML
    private TabPane appTabs;
    @FXML
    private Label statusLabel;
    @FXML
    private Button statusActionButton;
    @FXML
    private TextField loginEmailField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane catalogueCardsPane;
    @FXML
    private Button promotionsButton;
    @FXML
    private Label catalogueInfoLabel;
    @FXML
    private Tab promotionsTab;
    @FXML
    private VBox promotionsContentBox;
    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> cartProductCol;
    @FXML
    private TableColumn<CartItem, Number> cartQtyCol;
    @FXML
    private TableColumn<CartItem, Number> cartUnitPriceCol;
    @FXML
    private TableColumn<CartItem, Number> cartDiscountCol;
    @FXML
    private TableColumn<CartItem, Number> cartTotalCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Label lastOrderReferenceLabel;
    @FXML
    private Button copyTrackingButton;
    @FXML
    private Label discountSummaryLabel;
    @FXML
    private Label subtotalValueLabel;
    @FXML
    private Label promoSavingsValueLabel;
    @FXML
    private Label loyaltySavingsValueLabel;
    @FXML
    private Label grandTotalValueLabel;
    @FXML
    private Label checkoutFeedbackLabel;
    @FXML
    private TextField checkoutEmailField;
    @FXML
    private TextField deliveryAddressField;
    @FXML
    private TextField cardTypeField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField securityCodeField;
    @FXML
    private TextField expiryField;
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> orderIdCol;
    @FXML
    private TableColumn<Order, String> orderStatusCol;
    @FXML
    private TableColumn<Order, String> orderTrackingCol;
    @FXML
    private TableColumn<Order, Number> orderAmountCol;
    @FXML
    private Label ordersInfoLabel;
    @FXML
    private TextField trackingEmailField;
    @FXML
    private TextField trackingCodeField;
    @FXML
    private Label trackingLookupResultLabel;
    @FXML
    private TextField campaignNameField;
    @FXML
    private TextField campaignStartField;
    @FXML
    private TextField campaignEndField;
    @FXML
    private TextArea campaignItemsArea;
    @FXML
    private Label campaignConflictLabel;
    @FXML
    private TextArea campaignPreviewArea;
    @FXML
    private Label campaignEditorModeLabel;
    @FXML
    private TextField cancelCampaignIdField;
    @FXML
    private TableView<MapRow> campaignTable;
    @FXML
    private TableColumn<MapRow, String> campaignIdCol;
    @FXML
    private TableColumn<MapRow, String> campaignNameCol;
    @FXML
    private TableColumn<MapRow, String> campaignStatusCol;
    @FXML
    private TableColumn<MapRow, String> campaignStartCol;
    @FXML
    private TableColumn<MapRow, String> campaignEndCol;
    @FXML
    private DatePicker reportStartDate;
    @FXML
    private DatePicker reportEndDate;
    @FXML
    private Label reportHeaderLabel;
    @FXML
    private Label reportPeriodLabel;
    @FXML
    private TableView<MapRow> reportTable;
    @FXML
    private Label profileEmailValueLabel;
    @FXML
    private Label profileMemberTypeValueLabel;
    @FXML
    private Label profileOrdersValueLabel;
    @FXML
    private Label profileLoyaltyValueLabel;
    @FXML
    private ImageView profileAvatarImageView;
    @FXML
    private VBox toastContainer;
    @FXML
    private StackPane loadingOverlay;
    @FXML
    private Label loadingLabel;

    @FXML
    public void initialize() {
        bindTables();
        installLivePreviewHandlers();
        refreshCatalogueQuiet("");
        loadPromotionsView();
        appTabs.setVisible(false);
        appTabs.setManaged(false);
        reportStartDate.setValue(LocalDate.now().minusDays(30));
        reportEndDate.setValue(LocalDate.now());
        checkoutFeedbackLabel.setText(DEFAULT_STOCK_CHECK_MESSAGE);
        refreshCampaignPreview();
        updateReportHeader("Reports", resolveReportWindow());
        hideLoading();
        clearStatusAction();
        loadBrandAssets();
        updateSessionState();
        clearLastOrderBanner();
        Platform.runLater(this::installInteractiveAnimations);
    }

    private void bindTables() {
        cartProductCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProductName()));
        cartQtyCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getQuantity()));
        cartUnitPriceCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getUnitPrice()));
        cartDiscountCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getDiscountPercent()));
        cartTotalCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getLineTotal()));
        cartTable.setItems(cart);

        orderIdCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getOrderId()));
        orderStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus()));
        orderTrackingCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTrackingCode()));
        orderAmountCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getTotalAmount()));
        ordersTable.setItems(orders);

        campaignIdCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("id")));
        campaignNameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("name")));
        campaignStatusCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().get("status")));
        campaignStartCol.setCellValueFactory(d -> new SimpleStringProperty(
                formatCampaignTableCell(d.getValue().getRaw("start_time"))));
        campaignEndCol.setCellValueFactory(d -> new SimpleStringProperty(
                formatCampaignTableCell(d.getValue().getRaw("end_time"))));
        campaignTable.setItems(campaigns);
        campaignTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadCampaignIntoEditor(newSelection);
            }
        });
    }

    private void installLivePreviewHandlers() {
        if (campaignNameField != null) {
            campaignNameField.textProperty().addListener((obs, oldValue, newValue) -> scheduleCampaignPreviewRefresh());
        }
        if (campaignStartField != null) {
            campaignStartField.textProperty().addListener((obs, oldValue, newValue) -> scheduleCampaignPreviewRefresh());
        }
        if (campaignEndField != null) {
            campaignEndField.textProperty().addListener((obs, oldValue, newValue) -> scheduleCampaignPreviewRefresh());
        }
        if (campaignItemsArea != null) {
            campaignItemsArea.textProperty().addListener((obs, oldValue, newValue) -> scheduleCampaignPreviewRefresh());
        }
        if (checkoutEmailField != null) {
            checkoutEmailField.textProperty().addListener((obs, oldValue, newValue) -> updateProfileSummary());
        }
        if (promotionsTab != null) {
            promotionsTab.setOnSelectionChanged(event -> {
                if (promotionsTab.isSelected()) {
                    requestPromotionsRefresh();
                }
            });
        }
    }

    @FXML
    private void onLogin() {
        runAsyncAction(
                "Signing in...",
                () -> authService.login(loginEmailField.getText(), loginPasswordField.getText()),
                user -> {
            if (user.isEmpty()) {
                showCustomerMessage("Incorrect email or password.", CustomerMessageType.ERROR);
                return;
            }
            currentUser = user.get();
            if (currentUser.isFirstLogin() && "NON_COMMERCIAL".equals(currentUser.getMemberType())) {
                openForcePasswordChangeDialog();
                return;
            }
            completeLogin();
                },
                ex -> showCustomerMessage("Login failed: " + messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    private void openForcePasswordChangeDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Force Password Change");
        alert.setHeaderText("First login detected");
        VBox content = new VBox(8);
        PasswordField newPass = new PasswordField();
        newPass.setPromptText("New Password");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Confirm Password");
        content.getChildren().addAll(new Label("Set a new password to continue"), newPass, confirmPass);
        alert.getDialogPane().setContent(content);
        Optional<ButtonType> response = alert.showAndWait();
        if (response.isEmpty() || response.get() != ButtonType.OK) {
            showCustomerMessage("Password change is required before continuing.", CustomerMessageType.ERROR);
            return;
        }
        try {
            authService.forcePasswordChange(currentUser.getUserId(), newPass.getText(), confirmPass.getText());
            currentUser.setFirstLogin(false);
            showCustomerMessage("Password updated successfully.", CustomerMessageType.SUCCESS);
            completeLogin();
        } catch (Exception ex) {
            showCustomerMessage(ex.getMessage(), CustomerMessageType.ERROR);
        }
    }

    private void completeLogin() {
        guestSessionActive = false;
        enterPortalSession();
        showCustomerMessage("Logged in as " + currentUser.getEmail() + ".", CustomerMessageType.SUCCESS);
    }

    @FXML
    private void onContinueAsGuest() {
        currentUser = null;
        guestSessionActive = true;
        enterPortalSession();
        showCustomerMessage(
                "Guest mode is active. You can browse, add to cart, and checkout using your email address.",
                CustomerMessageType.INFO
        );
    }

    private void enterPortalSession() {
        authPane.setVisible(false);
        authPane.setManaged(false);
        appTabs.setVisible(true);
        appTabs.setManaged(true);
        promotionsButton.setVisible(catalogService.hasPromotions());
        promotionsButton.setManaged(catalogService.hasPromotions());
        Tab adminTab = appTabs.getTabs().stream()
                .filter(tab -> "Admin".equals(tab.getText()))
                .findFirst()
                .orElse(null);
        if (adminTab != null) {
            adminTab.setDisable(currentUser == null || !"ADMIN".equals(currentUser.getMemberType()));
        }
        updateSessionState();
        refreshCatalogue("");
        loadPromotionsView();
        onRefreshOrders();
        onRefreshCampaigns();
        Platform.runLater(this::installInteractiveAnimations);
    }

    @FXML
    private void onOpenNonCommercialRegistration() {
        if (regNonCommercialEmailField != null) {
            regNonCommercialEmailField.clear();
        }
        showAuthCard(authRegisterNonCommercialPane);
    }

    @FXML
    private void onOpenCommercialRegistration() {
        if (regCommercialEmailField != null) {
            regCommercialEmailField.clear();
        }
        if (regCommercialCompanyField != null) {
            regCommercialCompanyField.clear();
        }
        if (regCommercialDirectorField != null) {
            regCommercialDirectorField.clear();
        }
        if (regCommercialBusinessTypeField != null) {
            regCommercialBusinessTypeField.clear();
        }
        if (regCommercialAddressArea != null) {
            regCommercialAddressArea.clear();
        }
        showAuthCard(authRegisterCommercialPane);
    }

    @FXML
    private void onAuthBackToLogin() {
        showAuthCard(authLoginPane);
    }

    @FXML
    private void onSubmitNonCommercialRegistration() {
        runButtonAction(
                regNonCommercialSubmitButton,
                "Creating...",
                () -> {
                    String email = authService.requireValidEmail(regNonCommercialEmailField.getText());
                    return new GeneratedPasswordResult(email, authService.registerNonCommercial(email));
                },
                result -> {
                    showInlineRegistrationSuccess(result.email(), result.password());
                    showCustomerMessage("Account created successfully. Your temporary password is ready.", CustomerMessageType.SUCCESS);
                },
                ex -> showCustomerMessage(messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    @FXML
    private void onSubmitCommercialRegistration() {
        try {
            CommercialApplicationDto dto = authService.validateCommercialApplication(
                    regCommercialCompanyField.getText(),
                    regCommercialDirectorField.getText(),
                    regCommercialBusinessTypeField.getText(),
                    regCommercialAddressArea.getText(),
                    regCommercialEmailField.getText()
            );
            runButtonAction(
                    regCommercialSubmitButton,
                    "Submitting...",
                    () -> memberApiClient.submitCommercialApplication(dto),
                    submitted -> {
                        if (submitted) {
                            showAuthCard(authLoginPane);
                            showCustomerMessage(
                                    "Commercial membership application submitted successfully.",
                                    CustomerMessageType.SUCCESS
                            );
                        } else {
                            showCustomerMessage("Commercial application submission failed.", CustomerMessageType.ERROR);
                        }
                    },
                    ex -> showCustomerMessage(messageFromException(ex), CustomerMessageType.ERROR)
            );
        } catch (Exception ex) {
            showCustomerMessage(ex.getMessage(), CustomerMessageType.ERROR);
        }
    }

    @FXML
    private void onCopyInlineRegistrationPassword() {
        if (regResultPasswordField != null && !regResultPasswordField.getText().isBlank()) {
            copyToClipboard(regResultPasswordField.getText());
            showCustomerMessage("Temporary password copied to clipboard.", CustomerMessageType.SUCCESS);
        }
    }

    private void showAuthCard(VBox active) {
        VBox[] panes = {authLoginPane, authRegisterNonCommercialPane, authRegisterCommercialPane, authRegistrationResultPane};
        for (VBox pane : panes) {
            if (pane == null) {
                continue;
            }
            boolean on = pane == active;
            pane.setVisible(on);
            pane.setManaged(on);
        }
    }

    private void showInlineRegistrationSuccess(String email, String password) {
        copyToClipboard(password);
        latestGeneratedPassword = password;
        if (regResultEmailField != null) {
            regResultEmailField.setText(email);
        }
        if (regResultPasswordField != null) {
            regResultPasswordField.setText(password);
        }
        if (statusActionButton != null) {
            statusActionButton.setText("Copy Password");
            statusActionButton.setVisible(true);
            statusActionButton.setManaged(true);
        }
        showAuthCard(authRegistrationResultPane);
    }

    @FXML
    private void onSearch() {
        refreshCatalogue(searchField.getText());
    }

    private void refreshCatalogue(String keyword) {
        String kw = keyword == null ? "" : keyword;
        runAsyncAction(
                "Loading catalogue...",
                () -> loadCatalogueSnapshot(kw),
                this::applyCatalogueSnapshot,
                ex -> setStatus("Catalogue not loaded: " + messageFromException(ex))
        );
    }

    /** Loads catalogue in the background without the busy overlay (e.g. before login). */
    private void refreshCatalogueQuiet(String keyword) {
        String kw = keyword == null ? "" : keyword;
        Task<CatalogueSnapshot> task = new Task<>() {
            @Override
            protected CatalogueSnapshot call() throws Exception {
                return loadCatalogueSnapshot(kw);
            }
        };
        task.setOnSucceeded(event -> {
            try {
                applyCatalogueSnapshot(task.getValue());
            } catch (Exception e) {
                setStatus("Catalogue not loaded: " + messageFromException(e));
            }
        });
        task.setOnFailed(event -> setStatus("Catalogue not loaded: " + messageFromException(task.getException())));
        Thread worker = new Thread(task, "ipospu-catalogue-quiet");
        worker.setDaemon(true);
        worker.start();
    }

    private CatalogueSnapshot loadCatalogueSnapshot(String keyword) {
        List<InventoryItemDto> allProducts = catalogService.search(keyword);
        Map<String, Double> discounts = catalogService.activeDiscounts();
        boolean hasPromotions = catalogService.hasPromotions();
        return new CatalogueSnapshot(allProducts, discounts, hasPromotions);
    }

    private void applyCatalogueSnapshot(CatalogueSnapshot snap) {
        currentDiscounts = snap.discounts();
        List<InventoryItemDto> visibleProducts = new ArrayList<>();
        int promotionalProductCount = 0;
        for (InventoryItemDto product : snap.allProducts()) {
            boolean hasPromotion = currentDiscounts.getOrDefault(product.getProductId(), 0.0) > 0.0;
            if (hasPromotion) {
                promotionalProductCount++;
            }
            visibleProducts.add(product);
        }
        catalogue.setAll(visibleProducts);
        renderCatalogueCards(visibleProducts);
        updatePromotionState(promotionalProductCount, visibleProducts.size());
        promotionsButton.setVisible(snap.hasPromotions());
        promotionsButton.setManaged(snap.hasPromotions());
        for (CartItem item : cart) {
            item.setDiscountPercent(currentDiscounts.getOrDefault(item.getProductId(), 0.0));
        }
        cartTable.refresh();
        updateTotal();
    }

    @FXML
    private void onPromotionsView() {
        if (!catalogService.hasPromotions()) {
            showCustomerMessage("No active promotions are available right now.", CustomerMessageType.INFO);
            return;
        }
        if (appTabs != null && promotionsTab != null) {
            if (!promotionsTab.isSelected()) {
                appTabs.getSelectionModel().select(promotionsTab);
                return;
            }
        }
        requestPromotionsRefresh();
    }

    private void requestPromotionsRefresh() {
        runAsyncAction(
                "Loading promotions...",
                () -> {
                    campaignService.recordPromotionsView();
                    return campaignService.listPromotionItems();
                },
                rows -> {
                    renderPromotionsView(rows);
                    showCustomerMessage("Showing current and upcoming promotions.", CustomerMessageType.INFO);
                },
                ex -> showCustomerMessage("Promotions not loaded: " + messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    private void addProductToCart(InventoryItemDto selected, int qty, Button sourceButton) {
        if (selected == null) {
            showCustomerMessage("Select a product first.", CustomerMessageType.ERROR);
            return;
        }
        if (qty <= 0) {
            showCustomerMessage("Quantity must be positive.", CustomerMessageType.ERROR);
            return;
        }
        CartItem existing = cart.stream().filter(i -> i.getProductId().equals(selected.getProductId())).findFirst().orElse(null);
        int requestedTotal = qty + (existing == null ? 0 : existing.getQuantity());
        if (requestedTotal > selected.getStockQuantity()) {
            showCustomerMessage(
                    "Only " + selected.getStockQuantity() + " unit(s) are currently in stock.",
                    CustomerMessageType.ERROR
            );
            return;
        }
        Map<String, Double> discounts = catalogService.activeDiscounts();
        double discount = discounts.getOrDefault(selected.getProductId(), 0.0);
        runButtonAction(
                sourceButton,
                "Add To Cart",
                () -> {
                    campaignService.recordItemAdded(selected.getProductId(), qty);
                    return discount;
                },
                ignored -> {
                    CartItem currentItem = cart.stream()
                            .filter(i -> i.getProductId().equals(selected.getProductId()))
                            .findFirst()
                            .orElse(null);
                    if (currentItem == null) {
                        cart.add(new CartItem(selected.getProductId(), selected.getName(), selected.getRetailPrice(), qty, discount));
                    } else {
                        currentItem.setQuantity(currentItem.getQuantity() + qty);
                        currentItem.setDiscountPercent(discount);
                        cartTable.refresh();
                    }
                    updateTotal();
                    if (discount > 0) {
                        showCustomerMessage(
                                String.format("Item added to cart with %.0f%% promotional discount.", discount),
                                CustomerMessageType.SUCCESS
                        );
                    } else {
                        showCustomerMessage("Item added to cart.", CustomerMessageType.SUCCESS);
                    }
                },
                ex -> showCustomerMessage("Add to cart failed: " + messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    private void updateTotal() {
        double subtotal = 0.0;
        double discountedSubtotal = 0.0;
        for (CartItem item : cart) {
            subtotal += item.getUnitPrice() * item.getQuantity();
            discountedSubtotal += item.getLineTotal();
        }
        double promotionSavings = subtotal - discountedSubtotal;
        double loyaltySavings = currentUser != null && currentUser.isEligibleForLoyaltyDiscount()
                ? discountedSubtotal * 0.10
                : 0.0;
        double total = orderService.calculateCartTotal(cart, currentUser);
        totalLabel.setText(String.format("Total: %.2f", total));
        subtotalValueLabel.setText(formatMoney(subtotal));
        promoSavingsValueLabel.setText("-" + formatMoney(promotionSavings));
        loyaltySavingsValueLabel.setText("-" + formatMoney(loyaltySavings));
        grandTotalValueLabel.setText(formatMoney(total));
        updateDiscountSummary();
    }

    @FXML
    private void onCheckout() {
        try {
            String checkoutEmail = resolveCheckoutEmail();
            String normalizedCardNumber = normalizeCardNumber(cardNumberField.getText());
            normalizeSecurityCode(securityCodeField.getText());
            String normalizedExpiry = normalizeExpiryInput(expiryField.getText());
            PaymentRequest request = new PaymentRequest(
                    checkoutEmail,
                    0,
                    cardTypeField.getText(),
                    normalizedCardNumber.substring(0, 4),
                    normalizedCardNumber.substring(normalizedCardNumber.length() - 4),
                    normalizedExpiry
            );
            runAsyncAction(
                    "Processing checkout...",
                    () -> orderService.checkout(
                            currentUser,
                            checkoutEmail,
                            new ArrayList<>(cart),
                            deliveryAddressField.getText(),
                            request
                    ),
                    result -> {
                        if (result.isSuccess()) {
                            cart.clear();
                            updateTotal();
                            checkoutFeedbackLabel.setText(DEFAULT_STOCK_CHECK_MESSAGE);
                            showCustomerMessage(
                                    "Order placed successfully. Tracking code: " + result.getTrackingCode() + ".",
                                    CustomerMessageType.SUCCESS
                            );
                            trackingEmailField.setText(checkoutEmail);
                            trackingCodeField.setText(result.getTrackingCode());
                            trackingLookupResultLabel.setText("Latest tracked order: "
                                    + result.getOrderId() + " is currently RECEIVED with tracking code " + result.getTrackingCode() + ".");
                            updateLastOrderBanner(result.getOrderId(), result.getTrackingCode(), checkoutEmail);
                            updateProfileSummary();
                            onRefreshOrders();
                            onRefreshCampaigns();
                        } else {
                            if (!result.getUnavailableProductIds().isEmpty()) {
                                cart.removeIf(item -> result.getUnavailableProductIds().contains(item.getProductId()));
                                cartTable.refresh();
                                updateTotal();
                                checkoutFeedbackLabel.setText(
                                        "Stock changed during checkout. Removed from cart: "
                                                + String.join(", ", result.getUnavailableProductIds())
                                );
                            } else {
                                checkoutFeedbackLabel.setText(DEFAULT_STOCK_CHECK_MESSAGE);
                            }
                            showCustomerMessage(result.getMessage(), CustomerMessageType.ERROR);
                        }
                    },
                    ex -> showCustomerMessage("Checkout failed: " + messageFromException(ex), CustomerMessageType.ERROR)
            );
        } catch (Exception ex) {
            showCustomerMessage("Checkout failed: " + ex.getMessage(), CustomerMessageType.ERROR);
        }
    }

    @FXML
    private void onRefreshOrders() {
        if (currentUser == null) {
            orders.clear();
            ordersInfoLabel.setText("Guest mode does not expose private order history. Use the tracking lookup above with your email and tracking code.");
            return;
        }
        int userId = currentUser.getUserId();
        String email = currentUser.getEmail();
        runAsyncAction(
                "Refreshing orders...",
                () -> orderService.getOrderHistory(userId),
                list -> {
                    orders.setAll(list);
                    ordersInfoLabel.setText("Viewing private order history for " + email + ".");
                },
                ex -> showCustomerMessage("Orders not refreshed: " + messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    @FXML
    private void onLogout() {
        currentUser = null;
        guestSessionActive = false;
        selectedCampaignId = null;
        cart.clear();
        orders.clear();
        campaigns.clear();
        authPane.setVisible(true);
        authPane.setManaged(true);
        showAuthCard(authLoginPane);
        appTabs.setVisible(false);
        appTabs.setManaged(false);
        appTabs.getSelectionModel().selectFirst();
        deliveryAddressField.clear();
        cardTypeField.clear();
        cardNumberField.clear();
        securityCodeField.clear();
        expiryField.clear();
        checkoutEmailField.clear();
        trackingEmailField.clear();
        trackingCodeField.clear();
        trackingLookupResultLabel.setText(DEFAULT_TRACKING_HINT);
        checkoutFeedbackLabel.setText(DEFAULT_STOCK_CHECK_MESSAGE);
        clearLastOrderBanner();
        clearCampaignEditor();
        updateTotal();
        updateSessionState();
        showCustomerMessage("Logged out successfully.", CustomerMessageType.INFO);
    }

    @FXML
    private void onCreateCampaign() {
        try {
            LocalDateTime start = parseCampaignDateTime(campaignStartField.getText());
            LocalDateTime end = parseCampaignDateTime(campaignEndField.getText());
            String name = campaignNameField.getText();
            String items = campaignItemsArea.getText();
            runAsyncAction(
                    "Creating campaign...",
                    () -> campaignService.createCampaign(name, start, end, items),
                    campaignId -> {
                        setStatus("Campaign created with ID: " + campaignId);
                        clearCampaignEditor();
                        refreshCatalogue(searchField.getText());
                        onRefreshCampaigns();
                    },
                    ex -> setStatus("Campaign not created: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Campaign not created: " + ex.getMessage());
        }
    }

    @FXML
    private void onUpdateCampaign() {
        try {
            int campaignId = requireSelectedCampaignId();
            LocalDateTime start = parseCampaignDateTime(campaignStartField.getText());
            LocalDateTime end = parseCampaignDateTime(campaignEndField.getText());
            String name = campaignNameField.getText();
            String items = campaignItemsArea.getText();
            runAsyncAction(
                    "Updating campaign...",
                    () -> {
                        campaignService.updateCampaign(campaignId, name, start, end, items);
                        return null;
                    },
                    ignored -> {
                        setStatus("Campaign updated");
                        refreshCatalogue(searchField.getText());
                        onRefreshCampaigns();
                    },
                    ex -> setStatus("Campaign not updated: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Campaign not updated: " + ex.getMessage());
        }
    }

    @FXML
    private void onClearCampaignEditor() {
        clearCampaignEditor();
    }

    @FXML
    private void onCancelCampaign() {
        try {
            int campaignId = requireSelectedCampaignId();
            runAsyncAction(
                    "Cancelling campaign...",
                    () -> {
                        campaignService.cancelCampaign(campaignId);
                        return null;
                    },
                    ignored -> {
                        setStatus("Campaign cancelled");
                        clearCampaignEditor();
                        refreshCatalogue(searchField.getText());
                        onRefreshCampaigns();
                    },
                    ex -> setStatus("Campaign not cancelled: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Campaign not cancelled: " + ex.getMessage());
        }
    }

    @FXML
    private void onTerminateCampaign() {
        try {
            int campaignId = requireSelectedCampaignId();
            runAsyncAction(
                    "Ending campaign...",
                    () -> {
                        campaignService.terminateCampaignEarly(campaignId);
                        return null;
                    },
                    ignored -> {
                        setStatus("Campaign terminated early");
                        clearCampaignEditor();
                        refreshCatalogue(searchField.getText());
                        onRefreshCampaigns();
                    },
                    ex -> setStatus("Campaign not terminated: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Campaign not terminated: " + ex.getMessage());
        }
    }

    @FXML
    private void onDeleteCampaign() {
        try {
            int campaignId = requireSelectedCampaignId();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Campaign");
            alert.setHeaderText("Delete campaign " + campaignId + "?");
            alert.setContentText("This removes the campaign and its linked items/metrics.");
            Optional<ButtonType> response = alert.showAndWait();
            if (response.isEmpty() || response.get() != ButtonType.OK) {
                setStatus("Campaign delete cancelled");
                return;
            }
            runAsyncAction(
                    "Deleting campaign...",
                    () -> {
                        campaignService.deleteCampaign(campaignId);
                        return null;
                    },
                    ignored -> {
                        setStatus("Campaign deleted");
                        clearCampaignEditor();
                        refreshCatalogue(searchField.getText());
                        onRefreshCampaigns();
                    },
                    ex -> setStatus("Campaign not deleted: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Campaign not deleted: " + ex.getMessage());
        }
    }

    @FXML
    private void onRefreshCampaigns() {
        runAsyncAction(
                "Loading campaigns...",
                () -> {
                    List<MapRow> rows = new ArrayList<>();
                    for (Map<String, Object> row : campaignService.listCampaigns()) {
                        rows.add(new MapRow(row));
                    }
                    return rows;
                },
                rows -> {
                    campaigns.setAll(rows);
                    refreshCampaignPreview();
                },
                ex -> setStatus("Campaigns not loaded: " + messageFromException(ex))
        );
    }

    @FXML
    private void onSalesReport() {
        try {
            ReportWindow window = resolveReportWindow();
            updateReportHeader("Sales Report", window);
            runAsyncAction(
                    "Generating sales report...",
                    () -> reportService.salesReport(window.start(), window.endExclusive()),
                    this::loadReport,
                    ex -> setStatus("Report not generated: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Report not generated: " + ex.getMessage());
        }
    }

    @FXML
    private void onCampaignReport() {
        try {
            ReportWindow window = resolveReportWindow();
            updateReportHeader("Campaign Report", window);
            runAsyncAction(
                    "Generating campaign report...",
                    () -> reportService.campaignsReport(window.start(), window.endExclusive()),
                    this::loadReport,
                    ex -> setStatus("Report not generated: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Report not generated: " + ex.getMessage());
        }
    }

    @FXML
    private void onEngagementReport() {
        try {
            ReportWindow window = resolveReportWindow();
            updateReportHeader("Engagement Report", window);
            runAsyncAction(
                    "Generating engagement report...",
                    () -> reportService.engagementReport(window.start(), window.endExclusive()),
                    this::loadReport,
                    ex -> setStatus("Report not generated: " + messageFromException(ex))
            );
        } catch (Exception ex) {
            setStatus("Report not generated: " + ex.getMessage());
        }
    }

    @FXML
    private void onPrintReport() {
        if (reportTable.getColumns().isEmpty() || reportTable.getItems().isEmpty()) {
            setStatus("Generate a report before printing");
            return;
        }
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            setStatus("No printer is available right now");
            return;
        }
        boolean proceed = job.showPrintDialog(reportTable.getScene().getWindow());
        if (!proceed) {
            setStatus("Report printing cancelled");
            return;
        }
        boolean printed = job.printPage(reportTable);
        if (printed) {
            job.endJob();
            setStatus("Report sent to printer");
        } else {
            setStatus("Report could not be printed");
        }
    }

    @FXML
    private void onTrackOrder() {
        String email = trackingEmailField.getText();
        String code = trackingCodeField.getText();
        runAsyncAction(
                "Looking up order...",
                () -> orderService.findOrderByTracking(email, code),
                orderOpt -> {
                    if (orderOpt.isEmpty()) {
                        trackingLookupResultLabel.setText("No order matched that email and tracking code.");
                        showCustomerMessage("No order matched that email and tracking code.", CustomerMessageType.ERROR);
                        return;
                    }
                    Order matchedOrder = orderOpt.get();
                    trackingLookupResultLabel.setText("Order " + matchedOrder.getOrderId()
                            + " is currently " + matchedOrder.getStatus()
                            + ". Total: " + formatMoney(matchedOrder.getTotalAmount())
                            + ". Created: " + formatDateTime(matchedOrder.getOrderDate()) + ".");
                    showCustomerMessage("Tracking lookup completed.", CustomerMessageType.SUCCESS);
                },
                ex -> showCustomerMessage("Tracking lookup failed: " + messageFromException(ex), CustomerMessageType.ERROR)
        );
    }

    @FXML
    private void onCopyLastTracking() {
        if (lastTrackingCodeToCopy == null || lastTrackingCodeToCopy.isBlank()) {
            return;
        }
        copyToClipboard(lastTrackingCodeToCopy);
        showCustomerMessage("Tracking code copied to clipboard.", CustomerMessageType.SUCCESS);
    }

    @FXML
    private void onStatusAction() {
        if (latestGeneratedPassword == null || latestGeneratedPassword.isBlank()) {
            return;
        }
        copyToClipboard(latestGeneratedPassword);
        showCustomerMessage("Temporary password copied to clipboard.", CustomerMessageType.SUCCESS);
        statusActionButton.setText("Copy Again");
        statusActionButton.setVisible(true);
        statusActionButton.setManaged(true);
    }

    private ReportWindow resolveReportWindow() {
        LocalDate startDate = reportStartDate.getValue() == null ? LocalDate.now() : reportStartDate.getValue();
        LocalDate endDate = reportEndDate.getValue() == null ? LocalDate.now() : reportEndDate.getValue();
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Report start date must be on or before the end date");
        }
        return new ReportWindow(
                ReportDateRange.startOfDay(startDate),
                ReportDateRange.exclusiveEndOfDay(endDate)
        );
    }

    private void loadReport(List<Map<String, Object>> rows) {
        reportTable.getColumns().clear();
        if (rows.isEmpty()) {
            reportTable.getItems().clear();
            setStatus("No report data for selected period");
            return;
        }
        Map<String, Object> first = rows.get(0);
        for (String key : first.keySet()) {
            TableColumn<MapRow, String> col = new TableColumn<>(key);
            col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(key)));
            reportTable.getColumns().add(col);
        }
        ObservableList<MapRow> items = FXCollections.observableArrayList();
        for (Map<String, Object> row : rows) {
            items.add(new MapRow(row));
        }
        reportTable.setItems(items);
        setStatus("Report generated on screen");
    }

    private void renderCatalogueCards(List<InventoryItemDto> products) {
        catalogueCardsPane.getChildren().clear();
        if (products.isEmpty()) {
            Label empty = new Label("No products match your current search.");
            empty.getStyleClass().add("screen-subtitle");
            catalogueCardsPane.getChildren().add(empty);
            return;
        }
        for (InventoryItemDto product : products) {
            catalogueCardsPane.getChildren().add(createCatalogueCard(product));
        }
    }

    private VBox createCatalogueCard(InventoryItemDto product) {
        double discount = currentDiscounts.getOrDefault(product.getProductId(), 0.0);
        double promoPrice = product.getRetailPrice() * (1 - discount / 100.0);

        StackPane imagePlaceholder = createPlaceholderVisual("medicine-placeholder", product.getName(), "MED");

        Label productId = new Label(product.getProductId());
        productId.getStyleClass().add("card-meta");

        HBox cardHeader = new HBox(8, productId);
        if (discount > 0) {
            Label promoPill = new Label(formatPromotion(discount));
            promoPill.getStyleClass().add("promo-pill");
            cardHeader.getChildren().add(promoPill);
        }

        Label name = new Label(product.getName());
        name.getStyleClass().add("product-card-title");

        Label description = new Label(product.getDescription());
        description.getStyleClass().add("product-card-description");
        description.setWrapText(true);

        Label stock = new Label("Stock: " + product.getStockQuantity());
        stock.getStyleClass().add("card-meta");

        HBox priceRow = new HBox(8);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        if (discount > 0) {
            Label originalPrice = new Label(formatMoney(product.getRetailPrice()));
            originalPrice.getStyleClass().add("price-old");
            Label discountedPrice = new Label(formatMoney(promoPrice));
            discountedPrice.getStyleClass().add("price-new");
            priceRow.getChildren().addAll(originalPrice, discountedPrice);
        } else {
            Label price = new Label(formatMoney(product.getRetailPrice()));
            price.getStyleClass().add("price-new");
            priceRow.getChildren().add(price);
        }

        TextField qtyField = new TextField("1");
        qtyField.getStyleClass().add("input");
        qtyField.setPrefWidth(72);
        qtyField.setMaxWidth(72);

        Button addButton = new Button("Add to cart");
        addButton.getStyleClass().addAll("btn-primary", "btn-text-safe");
        installHoverAnimation(addButton, 1.04);
        addButton.setOnAction(event -> {
            try {
                int qty = Integer.parseInt(qtyField.getText().trim());
                addProductToCart(product, qty, addButton);
            } catch (NumberFormatException ex) {
                showCustomerMessage("Quantity must be a number.", CustomerMessageType.ERROR);
            }
        });

        HBox actionRow = new HBox(10, qtyField, addButton);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(10, imagePlaceholder, cardHeader, name, description, stock, priceRow, actionRow);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(290);
        return card;
    }

    private void loadPromotionsView() {
        renderPromotionsView(campaignService.listPromotionItems());
    }

    private void renderPromotionsView(List<Map<String, Object>> rows) {
        if (promotionsContentBox == null) {
            return;
        }
        promotionsContentBox.getChildren().clear();
        if (rows.isEmpty()) {
            Label empty = new Label("There are no current or upcoming promotions to show.");
            empty.getStyleClass().add("screen-subtitle");
            promotionsContentBox.getChildren().add(empty);
            return;
        }

        Map<Integer, List<Map<String, Object>>> groupedRows = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Integer campaignId = ((Number) row.get("campaign_id")).intValue();
            groupedRows.computeIfAbsent(campaignId, key -> new ArrayList<>()).add(row);
        }

        for (List<Map<String, Object>> campaignRows : groupedRows.values()) {
            promotionsContentBox.getChildren().add(createPromotionSection(campaignRows));
        }
    }

    private VBox createPromotionSection(List<Map<String, Object>> campaignRows) {
        Map<String, Object> first = campaignRows.get(0);
        String campaignName = String.valueOf(first.get("campaign_name"));
        String status = String.valueOf(first.get("status"));
        LocalDateTime start = toDbCalendarDateTime(first.get("start_time"));
        LocalDateTime end = toDbCalendarDateTime(first.get("end_time"));

        Label title = new Label(campaignName + " (" + status + ")");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(formatCampaignRangeUtc(start, end));
        subtitle.getStyleClass().add("helper-text");

        VBox section = new VBox(10, title, subtitle);
        section.getStyleClass().add("promo-section");

        boolean hasItems = false;
        for (Map<String, Object> row : campaignRows) {
            if (row.get("product_id") == null) {
                continue;
            }
            hasItems = true;
            section.getChildren().add(createPromotionRow(row));
        }

        if (!hasItems) {
            Label noItems = new Label("No items are currently linked to this campaign.");
            noItems.getStyleClass().add("helper-text");
            section.getChildren().add(noItems);
        }
        return section;
    }

    private HBox createPromotionRow(Map<String, Object> row) {
        String productId = String.valueOf(row.get("product_id"));
        String productName = String.valueOf(row.get("product_name"));
        String description = String.valueOf(row.get("description"));
        double retailPrice = toDouble(row.get("retail_price"));
        double discount = toDouble(row.get("discount_percent"));
        int stock = toInt(row.get("stock_quantity"));
        String status = String.valueOf(row.get("status"));

        StackPane imagePlaceholder = createPlaceholderVisual("medicine-placeholder-small", productName, "MED");

        VBox details = new VBox(4);
        Label name = new Label(productName + " (" + productId + ")");
        name.getStyleClass().add("product-card-title");
        Label desc = new Label(description);
        desc.getStyleClass().add("product-card-description");
        desc.setWrapText(true);
        Label meta = new Label("Stock: " + stock + "  |  Discount: " + formatPromotion(discount));
        meta.getStyleClass().add("card-meta");
        details.getChildren().addAll(name, desc, meta);
        HBox.setHgrow(details, Priority.ALWAYS);

        VBox pricing = new VBox(4);
        pricing.setAlignment(Pos.CENTER_RIGHT);
        Label original = new Label(formatMoney(retailPrice));
        original.getStyleClass().add("price-old");
        Label promo = new Label(formatMoney(retailPrice * (1 - discount / 100.0)));
        promo.getStyleClass().add("price-new");
        pricing.getChildren().addAll(original, promo);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actionBox = new HBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        if ("ACTIVE".equalsIgnoreCase(status)) {
            TextField qtyField = new TextField("1");
            qtyField.getStyleClass().add("input");
            qtyField.setPrefWidth(60);
            Button addButton = new Button("Add to cart");
            addButton.getStyleClass().addAll("btn-primary", "btn-text-safe");
            installHoverAnimation(addButton, 1.04);
            addButton.setOnAction(event -> {
                try {
                    int qty = Integer.parseInt(qtyField.getText().trim());
                    addProductToCart(new InventoryItemDto(productId, productName, description, retailPrice, stock), qty, addButton);
                } catch (NumberFormatException ex) {
                    showCustomerMessage("Quantity must be a number.", CustomerMessageType.ERROR);
                }
            });
            actionBox.getChildren().addAll(qtyField, addButton);
        } else {
            Label upcoming = new Label("Starts soon");
            upcoming.getStyleClass().add("upcoming-pill");
            actionBox.getChildren().add(upcoming);
        }

        HBox promotionRowNode = new HBox(14, imagePlaceholder, details, pricing, spacer, actionBox);
        promotionRowNode.setAlignment(Pos.CENTER_LEFT);
        promotionRowNode.getStyleClass().add("promo-item-row");
        return promotionRowNode;
    }

    private void updatePromotionState(int promotionalProductCount, int visibleProductCount) {
        if (promotionalProductCount <= 0) {
            catalogueInfoLabel.setText("Browse all active catalogue items. There are no active promotions right now.");
            return;
        }
        catalogueInfoLabel.setText(promotionalProductCount
                + " product(s) currently have an active promotion. Open the Promotions screen to browse campaign pricing and upcoming deals.");
    }

    private void updateDiscountSummary() {
        int discountedLines = 0;
        double promotionSavings = 0.0;
        for (CartItem item : cart) {
            if (item.getDiscountPercent() > 0) {
                discountedLines++;
                promotionSavings += item.getUnitPrice() * item.getQuantity() * (item.getDiscountPercent() / 100.0);
            }
        }

        List<String> messages = new ArrayList<>();
        if (discountedLines > 0) {
            messages.add(String.format(
                    "Promotions applied to %d cart line(s), saving %.2f before loyalty discount.",
                    discountedLines,
                    promotionSavings
            ));
        }
        if (currentUser != null && currentUser.isEligibleForLoyaltyDiscount()) {
            messages.add("Loyalty offer active: this order gets an extra 10% off.");
        }
        if (messages.isEmpty()) {
            discountSummaryLabel.setText("No active discounts are applied yet.");
        } else {
            discountSummaryLabel.setText(String.join(" ", messages));
        }
    }

    private void scheduleCampaignPreviewRefresh() {
        if (campaignPreviewDebounce == null) {
            campaignPreviewDebounce = new PauseTransition(Duration.millis(400));
            campaignPreviewDebounce.setOnFinished(e -> refreshCampaignPreview());
        }
        campaignPreviewDebounce.stop();
        campaignPreviewDebounce.playFromStart();
    }

    private void refreshCampaignPreview() {
        if (campaignPreviewArea == null || campaignConflictLabel == null) {
            return;
        }
        if (isBlank(campaignNameField.getText()) && isBlank(campaignStartField.getText())
                && isBlank(campaignEndField.getText()) && isBlank(campaignItemsArea.getText())) {
            campaignPreviewArea.setText("Items added to the draft campaign will appear here.");
            campaignConflictLabel.setText("Conflict rules and preview information will appear here.");
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        if (!isBlank(campaignStartField.getText()) && !isBlank(campaignEndField.getText())) {
            try {
                start = parseCampaignDateTime(campaignStartField.getText());
                end = parseCampaignDateTime(campaignEndField.getText());
            } catch (Exception ex) {
                campaignConflictLabel.setText(ex.getMessage());
                campaignPreviewArea.setText("Enter a valid campaign window to preview campaign items.");
                return;
            }
        }

        List<String> previewLines = new ArrayList<>();
        List<String> conflictLines = new ArrayList<>();
        String[] rows = campaignItemsArea.getText().split("\\n");
        for (String row : rows) {
            String trimmed = row.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            String[] parts = trimmed.split(":");
            if (parts.length != 2) {
                previewLines.add(trimmed + " -> invalid format");
                continue;
            }

            String productId = parts[0].trim();
            String discountText = parts[1].trim().replace("%", "");
            double discount;
            try {
                discount = Double.parseDouble(discountText);
            } catch (NumberFormatException ex) {
                previewLines.add(productId + " -> invalid discount");
                continue;
            }

            Optional<InventoryItemDto> product = catalogService.findProduct(productId);
            if (product.isEmpty()) {
                previewLines.add(productId + " -> product not found in stock");
                continue;
            }

            InventoryItemDto item = product.get();
            previewLines.add(item.getProductId() + " - " + item.getName() + " - " + formatPromotion(discount));
            if (start != null && end != null
                    && campaignService.isProductInOverlappingCampaign(productId, start, end, selectedCampaignId)) {
                conflictLines.add(item.getName() + " already belongs to an overlapping campaign");
            }
        }

        if (previewLines.isEmpty()) {
            campaignPreviewArea.setText("Add one item per line using PRODUCT_ID:DISCOUNT, for example 10000002:5 (see university sample IDs, e.g. 10000001)");
        } else {
            campaignPreviewArea.setText(String.join("\n", previewLines));
        }

        if (conflictLines.isEmpty()) {
            campaignConflictLabel.setText(selectedCampaignId == null
                    ? "No overlap conflicts detected for the current draft."
                    : "No overlap conflicts detected for the selected campaign update.");
        } else {
            campaignConflictLabel.setText(String.join(" | ", conflictLines)
                    + " Resolve this by changing the dates, removing the listed product, or keeping only one discount per product.");
        }
    }

    private LocalDateTime parseCampaignDateTime(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Start and end date/time are required");
        }

        String trimmed = value.trim();
        DateTimeFormatter[] formats = {
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        };
        for (DateTimeFormatter format : formats) {
            try {
                return LocalDateTime.parse(trimmed, format);
            } catch (Exception ignored) {
                // Try the next supported format.
            }
        }
        throw new IllegalArgumentException("Use date format yyyy-MM-ddTHH:mm or yyyy-MM-dd HH:mm");
    }

    private String formatPromotion(double discountPercent) {
        if (discountPercent <= 0) {
            return "None";
        }
        if (Math.rint(discountPercent) == discountPercent) {
            return String.format("%.0f%% off", discountPercent);
        }
        return String.format("%.1f%% off", discountPercent);
    }

    private void updateReportHeader(String reportType, ReportWindow window) {
        reportHeaderLabel.setText(reportType);
        reportPeriodLabel.setText("Period: " + formatDate(window.start().toLocalDate()) + " -> "
                + formatDate(window.endExclusive().minusDays(1).toLocalDate()));
    }

    private void loadBrandAssets() {
        applyImage(headerLogoImageView, BRAND_LOGO_RESOURCE, 180, 46);
        applyImage(authLogoImageView, BRAND_LOGO_RESOURCE, 300, 120);
        applyImage(profileAvatarImageView, PROFILE_AVATAR_RESOURCE, 132, 132);
        if (toastContainer != null) {
            toastContainer.setManaged(false);
            toastContainer.setVisible(false);
        }
    }

    private void applyImage(ImageView imageView, String resourcePath, double fitWidth, double fitHeight) {
        if (imageView == null) {
            return;
        }
        URL url = PortalController.class.getResource(resourcePath);
        if (url == null) {
            imageView.setVisible(false);
            imageView.setManaged(false);
            return;
        }
        imageView.setImage(new Image(url.toExternalForm()));
        imageView.setFitWidth(fitWidth);
        imageView.setFitHeight(fitHeight);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    private void showCustomerMessage(String message, CustomerMessageType type) {
        if (isBlank(message)) {
            return;
        }
        clearStatusAction();
        if (toastContainer == null) {
            statusLabel.setText(message);
            return;
        }
        Label text = new Label(message);
        text.getStyleClass().add("customer-toast-text");
        text.setWrapText(true);
        text.setMaxWidth(520);

        StackPane toast = new StackPane(text);
        toast.getStyleClass().addAll("customer-toast", type.styleClass());
        toast.setOpacity(0.0);
        toast.setTranslateY(-18);

        toastContainer.getChildren().add(0, toast);
        toastContainer.setManaged(true);
        toastContainer.setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toast);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(180), toast);
        slideIn.setFromY(-18);
        slideIn.setToY(0);

        PauseTransition hold = new PauseTransition(type == CustomerMessageType.ERROR
                ? Duration.seconds(4.8)
                : Duration.seconds(3.6));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(220), toast);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(220), toast);
        slideOut.setFromY(0);
        slideOut.setToY(-10);

        SequentialTransition sequence = new SequentialTransition(
                new ParallelTransition(fadeIn, slideIn),
                hold,
                new ParallelTransition(fadeOut, slideOut)
        );
        sequence.setOnFinished(event -> {
            toastContainer.getChildren().remove(toast);
            if (toastContainer.getChildren().isEmpty()) {
                toastContainer.setVisible(false);
                toastContainer.setManaged(false);
            }
        });
        sequence.play();
    }

    private void setStatus(String message) {
        clearStatusAction();
        statusLabel.setText(message);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String formatMoney(double value) {
        return String.format("£%.2f", value);
    }

    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
    }

    /** Campaign times in DB are stored as UTC wall-clock (matches JDBC URL serverTimezone=UTC). */
    private String formatCampaignRangeUtc(LocalDateTime startUtc, LocalDateTime endUtc) {
        return formatDateTime(startUtc) + " UTC → " + formatDateTime(endUtc) + " UTC";
    }

    private String formatCampaignTableCell(Object value) {
        if (value == null) {
            return "";
        }
        try {
            return formatCampaignInput(toDbCalendarDateTime(value)) + " UTC";
        } catch (Exception ex) {
            return String.valueOf(value);
        }
    }

    private int toInt(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0.0;
    }

    private LocalDateTime toDbCalendarDateTime(Object value) {
        if (value instanceof java.sql.Timestamp timestamp) {
            return timestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
        }
        if (value instanceof LocalDateTime dateTime) {
            return dateTime;
        }
        throw new IllegalArgumentException("Unsupported date value: " + value);
    }

    private void updateSessionState() {
        if (checkoutEmailField != null) {
            if (currentUser != null) {
                checkoutEmailField.setText(currentUser.getEmail());
                checkoutEmailField.setDisable(true);
            } else {
                checkoutEmailField.setDisable(false);
                if (!guestSessionActive) {
                    checkoutEmailField.clear();
                }
            }
        }
        if (trackingEmailField != null) {
            if (currentUser != null) {
                trackingEmailField.setText(currentUser.getEmail());
            } else if (!guestSessionActive) {
                trackingEmailField.clear();
            }
        }
        if (ordersInfoLabel != null) {
            if (currentUser != null) {
                ordersInfoLabel.setText("Refresh shows private order history for " + currentUser.getEmail()
                        + ". Tracking lookup can still be used for any confirmation email.");
            } else if (guestSessionActive) {
                ordersInfoLabel.setText("Guest mode: use tracking lookup with your checkout email and tracking code.");
            } else {
                ordersInfoLabel.setText("Refresh shows the current signed-in user's order history. Tracking lookup works for both registered customers and guest checkouts.");
            }
        }
        updateProfileSummary();
    }

    private void updateLastOrderBanner(String orderId, String trackingCode, String email) {
        lastTrackingCodeToCopy = trackingCode;
        if (lastOrderReferenceLabel != null) {
            lastOrderReferenceLabel.setText(String.format(
                    "Last successful order: %s  |  Tracking: %s  |  Email used: %s",
                    orderId,
                    trackingCode,
                    email));
        }
        if (copyTrackingButton != null) {
            copyTrackingButton.setVisible(true);
            copyTrackingButton.setManaged(true);
        }
    }

    private void clearLastOrderBanner() {
        lastTrackingCodeToCopy = null;
        if (lastOrderReferenceLabel != null) {
            lastOrderReferenceLabel.setText(DEFAULT_LAST_ORDER_TEXT);
        }
        if (copyTrackingButton != null) {
            copyTrackingButton.setVisible(false);
            copyTrackingButton.setManaged(false);
        }
    }

    private void updateProfileSummary() {
        if (profileEmailValueLabel == null) {
            return;
        }
        if (guestSessionActive) {
            profileEmailValueLabel.setText(checkoutEmailField == null || isBlank(checkoutEmailField.getText())
                    ? "Guest checkout"
                    : checkoutEmailField.getText().trim());
            profileMemberTypeValueLabel.setText("GUEST");
            profileOrdersValueLabel.setText("Tracking only");
            profileLoyaltyValueLabel.setText("No loyalty discount");
            return;
        }
        if (currentUser == null) {
            profileEmailValueLabel.setText("Not signed in");
            profileMemberTypeValueLabel.setText("—");
            profileOrdersValueLabel.setText("0");
            profileLoyaltyValueLabel.setText("Not active");
            return;
        }
        profileEmailValueLabel.setText(currentUser.getEmail());
        profileMemberTypeValueLabel.setText(currentUser.getMemberType());
        profileOrdersValueLabel.setText(String.valueOf(currentUser.getCompletedOrderCount()));
        profileLoyaltyValueLabel.setText(currentUser.isEligibleForLoyaltyDiscount()
                ? "10th order discount ready"
                : "Next loyalty discount not active");
    }

    private void loadCampaignIntoEditor(MapRow row) {
        try {
            int campaignId = Integer.parseInt(row.get("id"));
            Optional<Map<String, Object>> campaign = campaignService.findCampaign(campaignId);
            if (campaign.isEmpty()) {
                setStatus("Campaign could not be loaded");
                return;
            }
            Map<String, Object> data = campaign.get();
            selectedCampaignId = campaignId;
            cancelCampaignIdField.setText(String.valueOf(campaignId));
            campaignNameField.setText(String.valueOf(data.get("name")));
            campaignStartField.setText(formatCampaignInput(toDbCalendarDateTime(data.get("start_time"))));
            campaignEndField.setText(formatCampaignInput(toDbCalendarDateTime(data.get("end_time"))));
            campaignItemsArea.setText(campaignService.getCampaignItemSpec(campaignId));
            campaignEditorModeLabel.setText("Editing campaign " + campaignId + " (" + data.get("status")
                    + "). Update dates/discounts, cancel it, end it now, or delete it.");
            refreshCampaignPreview();
        } catch (Exception ex) {
            setStatus("Campaign could not be loaded: " + ex.getMessage());
        }
    }

    private void clearCampaignEditor() {
        selectedCampaignId = null;
        if (campaignTable != null) {
            campaignTable.getSelectionModel().clearSelection();
        }
        if (cancelCampaignIdField != null) {
            cancelCampaignIdField.clear();
        }
        if (campaignNameField != null) {
            campaignNameField.clear();
        }
        if (campaignStartField != null) {
            campaignStartField.clear();
        }
        if (campaignEndField != null) {
            campaignEndField.clear();
        }
        if (campaignItemsArea != null) {
            campaignItemsArea.clear();
        }
        if (campaignEditorModeLabel != null) {
            campaignEditorModeLabel.setText("Draft mode: create a new campaign, or select a row below to load and edit an existing one.");
        }
        refreshCampaignPreview();
    }

    private int requireSelectedCampaignId() {
        if (selectedCampaignId != null) {
            return selectedCampaignId;
        }
        if (cancelCampaignIdField != null && !isBlank(cancelCampaignIdField.getText())) {
            try {
                return Integer.parseInt(cancelCampaignIdField.getText().trim());
            } catch (NumberFormatException ignored) {
                // Fall through to the shared error below.
            }
        }
        throw new IllegalArgumentException("Select a campaign row first");
    }

    private String normalizeCardNumber(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Card number is required");
        }
        String digitsOnly = value.replaceAll("\\D", "");
        if (!CARD_NUMBER_PATTERN.matcher(digitsOnly).matches()) {
            throw new IllegalArgumentException("Card number must contain between 8 and 19 digits");
        }
        return digitsOnly;
    }

    private String normalizeSecurityCode(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Security code is required");
        }
        String digitsOnly = value.replaceAll("\\D", "");
        if (!SECURITY_CODE_PATTERN.matcher(digitsOnly).matches()) {
            throw new IllegalArgumentException("Security code must be 3 or 4 digits");
        }
        return digitsOnly;
    }

    private String normalizeExpiryInput(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("Expiry date is required");
        }
        String trimmed = value.trim();
        YearMonth parsed = tryParseYearMonth(trimmed, "M/yy", "MM/yy", "M/yyyy", "MM/yyyy");
        if (parsed == null) {
            LocalDate date = tryParseDate(trimmed, "d/M/yyyy", "dd/MM/yyyy");
            if (date != null) {
                parsed = YearMonth.from(date);
            }
        }
        if (parsed == null) {
            throw new IllegalArgumentException(
                    "Expiry date must use a supported sample-data format such as 09/28, 9/2028, or 30/08/2030"
            );
        }
        return parsed.format(DateTimeFormatter.ofPattern("MM/yy"));
    }

    private YearMonth tryParseYearMonth(String value, String... patterns) {
        for (String pattern : patterns) {
            try {
                return YearMonth.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
                // Try the next accepted pattern.
            }
        }
        return null;
    }

    private LocalDate tryParseDate(String value, String... patterns) {
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
                // Try the next accepted pattern.
            }
        }
        return null;
    }

    private String resolveCheckoutEmail() {
        if (currentUser != null) {
            return authService.requireValidEmail(currentUser.getEmail());
        }
        return authService.requireValidEmail(checkoutEmailField.getText());
    }

    private String formatCampaignInput(LocalDateTime value) {
        return value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private StackPane createPlaceholderVisual(String styleClass, String title, String fallbackText) {
        StackPane placeholder = new StackPane();
        placeholder.getStyleClass().add(styleClass);
        Label text = new Label(isBlank(title) ? fallbackText : title.substring(0, Math.min(3, title.length())).toUpperCase());
        text.getStyleClass().add("placeholder-visual-text");
        placeholder.getChildren().add(text);
        return placeholder;
    }

    private void installInteractiveAnimations() {
        if (authPane == null || authPane.getScene() == null) {
            return;
        }
        attachHoverAnimations(authPane.getScene().getRoot().lookupAll(".button"), 1.03);
        attachHoverAnimations(appTabs.lookupAll(".tab"), 1.06);
    }

    private void attachHoverAnimations(Iterable<Node> nodes, double hoverScale) {
        for (Node node : nodes) {
            installHoverAnimation(node, hoverScale);
        }
    }

    private void installHoverAnimation(Node node, double hoverScale) {
        if (node == null || Boolean.TRUE.equals(node.getProperties().get("ipos-hover-installed"))) {
            return;
        }
        node.getProperties().put("ipos-hover-installed", Boolean.TRUE);
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(140), node);
        scaleUp.setToX(hoverScale);
        scaleUp.setToY(hoverScale);
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(140), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        node.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            scaleDown.stop();
            scaleUp.playFromStart();
        });
        node.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            scaleUp.stop();
            scaleDown.playFromStart();
        });
    }

    private void clearStatusAction() {
        latestGeneratedPassword = null;
        if (statusActionButton != null) {
            statusActionButton.setVisible(false);
            statusActionButton.setManaged(false);
            statusActionButton.setText("Copy Password");
        }
    }

    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    private void showLoading(String message) {
        loadingLabel.setText(message);
        if (loadingDepth.getAndIncrement() == 0 && loadingOverlay != null) {
            loadingOverlay.setVisible(true);
            loadingOverlay.setManaged(true);
        }
    }

    private void hideLoading() {
        int remaining = loadingDepth.decrementAndGet();
        if (remaining <= 0) {
            loadingDepth.set(0);
            if (loadingOverlay != null) {
                loadingOverlay.setVisible(false);
                loadingOverlay.setManaged(false);
            }
        }
    }

    private String messageFromException(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error";
        }
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null || cause.getMessage().isBlank()
                ? "Unknown error"
                : cause.getMessage();
    }

    private ProgressIndicator createInlineSpinner() {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(14, 14);
        spinner.setMaxSize(14, 14);
        spinner.getStyleClass().add("inline-spinner");
        return spinner;
    }

    private <T> void runButtonAction(
            Button button,
            String busyText,
            BackgroundAction<T> action,
            Consumer<T> onSuccess,
            Consumer<Throwable> onFailure
    ) {
        if (button == null) {
            runAsyncAction(busyText, action, onSuccess, onFailure);
            return;
        }

        String originalText = button.getText();
        Node originalGraphic = button.getGraphic();
        button.setDisable(true);
        button.setText(busyText);
        button.setGraphic(createInlineSpinner());

        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return action.run();
            }
        };
        task.setOnSucceeded(event -> {
            button.setDisable(false);
            button.setText(originalText);
            button.setGraphic(originalGraphic);
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            button.setDisable(false);
            button.setText(originalText);
            button.setGraphic(originalGraphic);
            onFailure.accept(task.getException());
        });
        Thread worker = new Thread(task, "ipospu-inline-button-action");
        worker.setDaemon(true);
        worker.start();
    }

    private <T> void runAsyncAction(
            String loadingMessage,
            BackgroundAction<T> action,
            Consumer<T> onSuccess,
            Consumer<Throwable> onFailure
    ) {
        showLoading(loadingMessage);
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return action.run();
            }
        };
        task.setOnSucceeded(event -> {
            try {
                onSuccess.accept(task.getValue());
            } finally {
                hideLoading();
            }
        });
        task.setOnFailed(event -> {
            try {
                onFailure.accept(task.getException());
            } finally {
                hideLoading();
            }
        });
        Thread worker = new Thread(task, "ipospu-background-action");
        worker.setDaemon(true);
        worker.start();
    }

    private record ReportWindow(LocalDateTime start, LocalDateTime endExclusive) {
    }

    private record CatalogueSnapshot(
            List<InventoryItemDto> allProducts,
            Map<String, Double> discounts,
            boolean hasPromotions
    ) {
    }

    private record GeneratedPasswordResult(String email, String password) {
    }

    private enum CustomerMessageType {
        SUCCESS("customer-toast-success"),
        ERROR("customer-toast-error"),
        INFO("customer-toast-info");

        private final String styleClass;

        CustomerMessageType(String styleClass) {
            this.styleClass = styleClass;
        }

        private String styleClass() {
            return styleClass;
        }
    }

    @FunctionalInterface
    private interface BackgroundAction<T> {
        T run() throws Exception;
    }

    public static class MapRow {
        private final Map<String, Object> data;

        public MapRow(Map<String, Object> data) {
            this.data = data;
        }

        public Object getRaw(String key) {
            return data.get(key);
        }

        public String get(String key) {
            Object value = data.get(key);
            return value == null ? "" : String.valueOf(value);
        }
    }
}
