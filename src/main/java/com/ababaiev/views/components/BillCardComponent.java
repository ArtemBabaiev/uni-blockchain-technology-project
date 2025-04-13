package com.ababaiev.views.components;

import com.ababaiev.models.Bill;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;

public class BillCardComponent extends Div {

    private final VerticalLayout contentLayout = new VerticalLayout();
    private final Span amountSpan = new Span();
    private final Span dateSpan = new Span();
    private final Span utilitySpan = new Span();

    private Bill bill;

    public BillCardComponent() {
        initLayout();
    }

    public BillCardComponent(Bill bill) {
        this();
        setBill(bill);
    }

    public void setBill(Bill bill) {
        this.bill = bill;
        updateContent();
    }

    private void initLayout() {
        addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Margin.Bottom.SMALL
        );

        amountSpan.addClassName(LumoUtility.FontWeight.BOLD);
        utilitySpan.addClassName(LumoUtility.TextColor.SECONDARY);

        contentLayout.setPadding(false);
        contentLayout.setSpacing(false);
        contentLayout.add(amountSpan, dateSpan, utilitySpan);

        add(contentLayout);
    }

    private void updateContent() {
        if (bill == null) {
            amountSpan.setText("No bill data available.");
            dateSpan.setText("");
            utilitySpan.setText("");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        amountSpan.setText("Amount Due: $" + String.format("%.2f", bill.getAmountDue()));
        dateSpan.setText("Billing Date: " + bill.getBillingDate().format(formatter));

        String utility = (bill.getMeterReading() != null)
                ? bill.getMeterReading().getUtilityType().name()
                : "N/A";

        utilitySpan.setText("Utility Type: " + utility);
    }
}