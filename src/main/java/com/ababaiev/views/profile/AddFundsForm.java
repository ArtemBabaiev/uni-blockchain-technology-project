package com.ababaiev.views.profile;

import com.ababaiev.views.profile.models.AddFundsModel;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;

public class AddFundsForm extends FormLayout {
    @Getter
    Binder<AddFundsModel> binder = new BeanValidationBinder<>(AddFundsModel.class);

    NumberField funds = new NumberField("Funds");

    public AddFundsForm() {
        super();
        binder.bindInstanceFields(this);
        binder.setBean(new AddFundsModel());
        add(funds);
    }
}
