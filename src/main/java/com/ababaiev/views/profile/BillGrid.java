package com.ababaiev.views.profile;

import com.ababaiev.views.profile.models.BillGridModel;
import com.ababaiev.views.profile.models.BillStatus;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

public class BillGrid extends Grid<BillGridModel> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public BillGrid() {
        super(BillGridModel.class, false);
        var dateCol = addColumn(i -> formatter.format(i.getBillingDate())).setHeader("Billing Date");
        addColumn(BillGridModel::getAmountDue).setHeader("Amount Due");
        addComponentColumn(this::getStatusColumn).setHeader("Status");
        appendFooterRow().getCell(dateCol).setComponent(getFooterButtons());
        setAllRowsVisible(true);
        setSelectionMode(SelectionMode.SINGLE);
        setEmptyStateText("No Billings found.");
    }

    private Component getFooterButtons() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setPadding(false);

        Button payBtn = new Button("Pay");
        payBtn.addClickListener(event -> handlePayClick());

        hl.add(payBtn);
        return hl;
    }

    private void handlePayClick() {
        var selectedItems = getSelectedItems();
        if (selectedItems.isEmpty()) {
            Notification.show("Please select the bill").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }



        BillGridModel selectedItem = selectedItems.iterator().next();
        if (selectedItem.getStatus() != BillStatus.UNPAID) {
            Notification.show("Select unpaid bill").addThemeVariants(NotificationVariant.LUMO_WARNING);
            return;
        }
        fireEvent(new PayBillEvent(this, selectedItem));
    }

    private Component getStatusColumn(BillGridModel billGridModel) {
        Span badge = new Span();
        switch (billGridModel.getStatus()) {
            case UNPAID: {
                badge.setText("Unpaid");
                badge.getElement().getThemeList().add("badge error");
                break;
            }
            case PAID: {
                badge.setText("Paid");
                badge.getElement().getThemeList().add("badge success");
                break;
            }
            case PROCESSING: {
                badge.setText("Processing");
                badge.getElement().getThemeList().add("badge");
                break;
            }
        }
        return badge;
    }

    public Registration addPayBillListener(ComponentEventListener<PayBillEvent> listener) {
        return addListener(PayBillEvent.class, listener);
    }

    @Getter
    public static class PayBillEvent extends ComponentEvent<BillGrid> {
        private BillGridModel model;

        public PayBillEvent(BillGrid source, BillGridModel model) {
            super(source, false);
            this.model = model;
        }
    }
}
