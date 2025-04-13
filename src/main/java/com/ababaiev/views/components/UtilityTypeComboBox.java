package com.ababaiev.views.components;

import com.ababaiev.models.UtilityType;
import com.vaadin.flow.component.combobox.ComboBox;

public class UtilityTypeComboBox extends ComboBox<UtilityType> {
    public UtilityTypeComboBox() {
        super();
        setItems(UtilityType.values());
        setItemLabelGenerator(UtilityType::getValue);
    }
}
