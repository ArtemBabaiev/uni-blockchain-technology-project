package com.ababaiev.views.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Home")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@AnonymousAllowed
public class HomeView extends VerticalLayout {

    public HomeView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // Header
        H1 header = new H1("Welcome to UtilityPay");
        header.addClassNames(
                LumoUtility.Margin.Top.LARGE,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontSize.XXXLARGE
        );

        // Subheading / Description
        Span description = new Span("Easily manage and pay your utility bills in one place.");
        description.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        // Card container
        Div card = new Div(header, description);
        card.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Width.FULL,
                LumoUtility.MaxWidth.SCREEN_SMALL
        );

        add(card);
    }
}
