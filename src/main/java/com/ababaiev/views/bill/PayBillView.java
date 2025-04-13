package com.ababaiev.views.bill;

import com.ababaiev.models.Bill;
import com.ababaiev.services.BillService;
import com.ababaiev.services.TransactionService;
import com.ababaiev.views.bill.models.PayBillModel;
import com.ababaiev.views.components.BillCardComponent;
import com.ababaiev.views.profile.ProfileView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;

import java.util.Map;
import java.util.UUID;

@PageTitle("Pay bill")
@Route("bill/pay")
@PermitAll
public class PayBillView extends VerticalLayout implements HasUrlParameter<String> {

    private UUID billId;

    PayBillForm form;

    Button payBtn;
    BillCardComponent billCard;

    private final BillService billService;
    private final TransactionService transactionService;

    public PayBillView(BillService billService, TransactionService transactionService) {
        super();
        this.billService = billService;
        this.transactionService = transactionService;

        setSizeFull();
        form = new PayBillForm();
        payBtn = new Button("Pay");
        payBtn.addClickListener(e -> this.handlePayClick());
        billCard = new BillCardComponent();
        add(billCard, form, payBtn);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param) {
        try {
            billId = UUID.fromString(param);
            Bill bill = this.billService.getBill(billId);
            PayBillModel model = new PayBillModel();
            model.setBillId(bill.getId());
            form.setBean(model);
            billCard.setBill(bill);

        } catch (Exception e){
            Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_WARNING);
            beforeEvent.rerouteTo(ProfileView.class);

        }

    }

    private void handlePayClick() {
        if (form.getBinder().validate().isOk()) {
            try {
                this.transactionService.createPendingTransaction(form.getBinder().getBean());
                UI.getCurrent().navigate(ProfileView.class, QueryParameters.of("tab", "bills"));
            } catch (Exception e) {
                Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        } else{
            Notification.show("Please fill in all fields").addThemeVariants(NotificationVariant.LUMO_WARNING);
        }
    }

}
