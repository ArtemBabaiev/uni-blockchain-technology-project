package com.ababaiev.views.profile;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.models.UtilityType;
import com.ababaiev.services.BillService;
import com.ababaiev.services.MeterReadingService;
import com.ababaiev.services.UserService;
import com.ababaiev.views.bill.PayBillView;
import com.ababaiev.views.components.UtilityTypeComboBox;
import com.ababaiev.views.profile.models.AddFundsModel;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIcon;
import org.vaadin.lineawesome.LineAwesomeIconUrl;


@PageTitle("Profile")
@Route("profile")
@Menu(order = 1, icon = LineAwesomeIconUrl.USER_SOLID)
@PermitAll
public class ProfileView extends VerticalLayout implements BeforeEnterObserver {

    TabSheet tabSheet;

    ReadingForm readingForm;
    Button addReadingBtn;

    AddFundsForm addFundsForm;
    Button addFundsBtn;

    MeterReadingGrid meterReadingGrid;
    UtilityTypeComboBox meterUtilityTypeComboBox;

    BillGrid billGrid;
    UtilityTypeComboBox billUtilityTypeComboBox;

    Span balance;

    private final MeterReadingService meterReadingService;
    private final BillService billService;
    private final UserService userService;

    public ProfileView(MeterReadingService meterReadingService, BillService billService, UserService userService) {
        super();
        this.meterReadingService = meterReadingService;
        this.billService = billService;
        this.userService = userService;

        setSizeFull();

        tabSheet = new TabSheet();


        Tab tab = new Tab(LineAwesomeIcon.PLUS_SOLID.create(), new Span("Add funds"));
        tab.setId("add_funds");
        tabSheet.add(tab, getAddFunds());
        tab = new Tab(LineAwesomeIcon.EDIT_SOLID.create(), new Span("Add meter reading"));
        tab.setId("add_remeter_eading");
        tabSheet.add(tab, getAddMeter());
        tab = new Tab(LineAwesomeIcon.BOOK_OPEN_SOLID.create(), new Span("Meter Readings"));
        tab.setId("meter_readings");
        tabSheet.add(tab, getMeterGrid());
        tab = new Tab(LineAwesomeIcon.MONEY_BILL_SOLID.create(), new Span("Bills"));
        tab.setId("bills");
        tabSheet.add(tab, getBillGrid());

        tabSheet.addSelectedChangeListener(event -> {
            if (event.isFromClient()) {
                String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
                String urlWithParameters = baseUrl + "?tab=" + event.getSelectedTab().getId().get();
                UI.getCurrent().getPage().getHistory().replaceState(null, urlWithParameters);
            }
        });

        tabSheet.setSizeFull();
        balance = new Span();
        Button refreshBalanceBtn = new Button(LineAwesomeIcon.REDO_ALT_SOLID.create());
        refreshBalanceBtn.addClickListener(event -> refreshBalance());

        HorizontalLayout balanceLayout = new HorizontalLayout(new Paragraph(new Span("Balance: "), balance), refreshBalanceBtn);

        add(balanceLayout, tabSheet);
        refreshBalance();
    }

    private void refreshBalance() {
        String balanceValue = userService.getFunds().toString();
        balance.setText(balanceValue);
    }

    private Component getAddFunds() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setPadding(false);

        addFundsForm = new AddFundsForm();
        addFundsBtn = new Button("Add funds");

        addFundsBtn.addClickListener(this::handleAddFundsClick);
        vl.add(addFundsForm, addFundsBtn);
        return vl;
    }

    private Component getAddMeter() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSpacing(false);
        vl.setPadding(false);

        readingForm = new ReadingForm();
        addReadingBtn = new Button("Submit");

        addReadingBtn.addClickListener(this::handleAddReadingClick);
        vl.add(readingForm, addReadingBtn);
        return vl;
    }

    private Component getMeterGrid() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(false);
        vl.setPadding(false);


        meterReadingGrid = new MeterReadingGrid();

        meterUtilityTypeComboBox = new UtilityTypeComboBox();
        meterUtilityTypeComboBox.addValueChangeListener(event -> refreshMeterGrid(event.getValue()));
        meterUtilityTypeComboBox.setValue(UtilityType.ELECTRICITY);
        vl.add(meterUtilityTypeComboBox, meterReadingGrid);

        return vl;
    }

    private void refreshMeterGrid(UtilityType value) {
        meterReadingGrid.setItems(meterReadingService.findAllForCurrentUser(value));
    }

    private Component getBillGrid() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(false);
        vl.setPadding(false);


        billGrid = new BillGrid();
        billGrid.addPayBillListener(this::handlePayBillEvent);


        billUtilityTypeComboBox = new UtilityTypeComboBox();
        billUtilityTypeComboBox.addValueChangeListener(event -> refreshBillGrid(event.getValue()));
        billUtilityTypeComboBox.setValue(UtilityType.ELECTRICITY);


        vl.add(new HorizontalLayout(billUtilityTypeComboBox), billGrid);

        return vl;
    }

    private void refreshBillGrid(UtilityType value) {
        billGrid.setItems(billService.findAllForCurrentUser(value));
    }

    private void handlePayBillEvent(BillGrid.PayBillEvent payBillEvent) {
        var ids = payBillEvent.getModels().stream().map(i -> i.getId().toString()).toList();
        UI.getCurrent().navigate(PayBillView.class, String.join(",", ids));
    }

    private void handleAddReadingClick(ClickEvent<Button> buttonClickEvent) {
        var binder = readingForm.getBinder();
        if (binder.validate().isOk()) {
            try {
                meterReadingService.createMeterReading(binder.getBean());
                refreshMeterGrid(meterUtilityTypeComboBox.getValue());
                refreshBillGrid(billUtilityTypeComboBox.getValue());
            } catch (BadRequestException e) {
                Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);

            }
        }
    }

    private void handleAddFundsClick(ClickEvent<Button> buttonClickEvent) {
        var binder = addFundsForm.getBinder();
        if (binder.validate().isOk()) {
            userService.addFunds(binder.getBean().getFunds());
            binder.setBean(new AddFundsModel());
            refreshBalance();
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter("tab").ifPresent(tabId -> {
            for (int i = 0; i < tabSheet.getTabCount(); i++) {
                Tab tab = tabSheet.getTabAt(i);
                if (tab.getId().get().equals(tabId)) {
                    tabSheet.setSelectedTab(tab);
                    return;
                }
            }

        });
    }
}
