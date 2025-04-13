package com.ababaiev.views.meterReading;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.services.MeterReadingService;
import com.ababaiev.views.profile.ProfileView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Add reading")
@Route("readings/add")
@Menu(order = 2, icon = LineAwesomeIconUrl.EDIT_SOLID)
@PermitAll
public class AddReadingView extends VerticalLayout {
    ReadingForm readingForm = new ReadingForm();
    Button submitBtn = new Button("Submit");

    private final MeterReadingService meterReadingService;

    public AddReadingView(MeterReadingService meterReadingService) {
        super();
        this.meterReadingService = meterReadingService;
        submitBtn.addClickListener(this::handleSubmitClick);
        add(readingForm, submitBtn);

    }

    private void handleSubmitClick(ClickEvent<Button> buttonClickEvent) {
        var binder = readingForm.getBinder();
        if (binder.validate().isOk()) {
            try {
                meterReadingService.createMeterReading(binder.getBean());
                UI.getCurrent().navigate(ProfileView.class, QueryParameters.of("tab", "meter_readings"));
            } catch (BadRequestException e) {
                Notification.show(e.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);

            }
        }
    }
}
