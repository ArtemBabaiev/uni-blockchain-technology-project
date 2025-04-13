package com.ababaiev.views.profile;

import com.ababaiev.models.UtilityType;
import com.ababaiev.services.BillService;
import com.ababaiev.services.MeterReadingService;
import com.ababaiev.views.bill.PayBillView;
import com.ababaiev.views.components.UtilityTypeComboBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.Tabs;
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

    private final MeterReadingService meterReadingService;
    private final BillService billService;
    public ProfileView(MeterReadingService meterReadingService, BillService billService) {
        super();
        this.meterReadingService = meterReadingService;
        this.billService = billService;

        setSizeFull();

        tabSheet = new TabSheet();

        Tab tab = new Tab(LineAwesomeIcon.BOOK_OPEN_SOLID.create(), new Span("Meter Readings"));
        tab.setId("meter_readings");
        tabSheet.add(tab, getMeterGrid());
        tab = new Tab(LineAwesomeIcon.MONEY_BILL_SOLID.create(), new Span("Bills"));
        tab.setId("bills");
        tabSheet.add(tab, getBillGrid());

        tabSheet.addSelectedChangeListener(event -> {
            if (event.isFromClient()){
                String baseUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
                String urlWithParameters = baseUrl + "?tab=" + event.getSelectedTab().getId().get();
                UI.getCurrent().getPage().getHistory().replaceState(null, urlWithParameters);
            }
        });

        tabSheet.setSizeFull();

        add(tabSheet);

    }

    private Component getMeterGrid() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(false);
        vl.setPadding(false);

        MeterReadingGrid grid = new MeterReadingGrid();

        UtilityTypeComboBox comboBox = new UtilityTypeComboBox();
        comboBox.addValueChangeListener(event ->
                grid.setItems(meterReadingService.findAllForCurrentUser(event.getValue()))
        );
        comboBox.setValue(UtilityType.ELECTRICITY);
        vl.add(comboBox, grid);

        return vl;
    }

    private Component getBillGrid() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setSpacing(false);
        vl.setPadding(false);

        BillGrid grid = new BillGrid();
        grid.addPayBillListener(this::handlePayBillEvent);


        UtilityTypeComboBox comboBox = new UtilityTypeComboBox();
        comboBox.addValueChangeListener(event ->
                grid.setItems(billService.findAllForCurrentUser(event.getValue()))
        );
        comboBox.setValue(UtilityType.ELECTRICITY);
        vl.add(comboBox, grid);

        return vl;
    }

    private void handlePayBillEvent(BillGrid.PayBillEvent payBillEvent) {
        UI.getCurrent().navigate(PayBillView.class, payBillEvent.getModel().getId().toString());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        event.getLocation().getQueryParameters().getSingleParameter("tab")
                .ifPresent(tabId -> {
                    for (int i=0; i < tabSheet.getTabCount(); i++) {
                        Tab tab = tabSheet.getTabAt(i);
                        if (tab.getId().get().equals(tabId)) {
                            tabSheet.setSelectedTab(tab);
                            return;
                        }
                    }

                });
    }
}
