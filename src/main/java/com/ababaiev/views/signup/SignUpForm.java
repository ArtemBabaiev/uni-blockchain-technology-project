package com.ababaiev.views.signup;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SignUpForm extends FormLayout {

    @Getter
    Binder<SignUpModel> binder = new BeanValidationBinder<>(SignUpModel.class);

    TextField username ;
    PasswordField password;
    PasswordField confirmPassword;

    public SignUpForm() {
        username = new TextField("Username");
        password = new PasswordField("Password");
        confirmPassword = new PasswordField("Confirm Password");

        binder.setBean(new SignUpModel());
        binder.forField(confirmPassword)
                .asRequired("Confirm Password cannot be empty") // equivalent to @NotEmpty
                .withValidator(confirm -> Objects.equals(confirm, password.getValue()), "Passwords do not match")
                .bind(SignUpModel::getConfirmPassword, SignUpModel::setConfirmPassword);

        password.addValueChangeListener(e -> binder.validate());

        binder.bindInstanceFields(this);

        add(username, password, confirmPassword);
    }

}