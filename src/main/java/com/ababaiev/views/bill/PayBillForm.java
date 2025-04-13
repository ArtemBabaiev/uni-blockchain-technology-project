package com.ababaiev.views.bill;

import com.ababaiev.views.bill.models.PayBillModel;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;

import java.io.IOException;

public class PayBillForm extends FormLayout {

    @Getter
    Binder<PayBillModel> binder = new BeanValidationBinder<>(PayBillModel.class);

    MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    Upload upload = new Upload(buffer);

    public PayBillForm() {
        super();
        upload.setMaxFiles(1);

        upload.setDropLabel(new Span("Key to sign payment"));
        upload.addSucceededListener(event -> handleSuccededEvent(event));

        add(upload);
    }


    private void handleSuccededEvent(SucceededEvent event) {
        try {
            binder.getBean().setPrivateKey(buffer.getInputStream(event.getFileName()).readAllBytes());

        } catch (Exception e) {
            Notification.show("Failed to upload key").addThemeVariants(NotificationVariant.LUMO_WARNING);
        }

    }


    public void setBean(PayBillModel model) {
        binder.setBean(model);
    }


}
