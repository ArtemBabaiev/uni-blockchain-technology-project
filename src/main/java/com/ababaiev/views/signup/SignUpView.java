package com.ababaiev.views.signup;

import com.ababaiev.exceptions.BadRequestException;
import com.ababaiev.services.UserService;
import com.ababaiev.views.home.HomeView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;

@Route(value = "sign-up", autoLayout = false)
@PageTitle("Sign Up")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {
    private SignUpForm signUpForm;

    private final UserService userService;

    public SignUpView(UserService userService) {
        signUpForm = new SignUpForm();
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);


        VerticalLayout layout = new VerticalLayout();
        Button submitButton = new Button("Submit", e -> handleSubmit());
        submitButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submitButton.setWidthFull();
        layout.add(signUpForm, submitButton);
        layout.addClassNames(LumoUtility.Background.CONTRAST_5);
        layout.setMaxWidth("360px");
        add(new H1("Sign-Up"), layout);

    }

    private void handleSubmit() {
        if (signUpForm.getBinder().validate().isOk()) {
            try {

            } catch (BadRequestException e) {
                Notification.show(e.getMessage()).addThemeName(NotificationVariant.LUMO_ERROR.getVariantName());
            }
            SignUpModel signUpModel = signUpForm.getBinder().getBean();

            byte[] privateKey = userService.signup(signUpModel);

            StreamResource resource = new StreamResource("private.pem", () -> new ByteArrayInputStream(privateKey));
            final StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
            UI.getCurrent().getPage().open(registration.getResourceUri().toString());
            UI.getCurrent().navigate(HomeView.class);


        }
    }
}