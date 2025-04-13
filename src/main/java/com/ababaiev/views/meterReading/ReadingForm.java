package com.ababaiev.views.meterReading;

import com.ababaiev.views.components.UtilityTypeComboBox;
import com.ababaiev.views.meterReading.models.CreateReadingModel;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;

public class ReadingForm extends FormLayout {
    @Getter
    Binder<CreateReadingModel> binder = new BeanValidationBinder<>(CreateReadingModel.class);
    IntegerField reading = new IntegerField("Reading");
    UtilityTypeComboBox utilityType = new UtilityTypeComboBox();


    public ReadingForm() {
        super();
        utilityType.setLabel("Utility Type");
        binder.bindInstanceFields(this);
        binder.setBean(new CreateReadingModel());

        add(reading, utilityType);
    }

}
