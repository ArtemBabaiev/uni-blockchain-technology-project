package com.ababaiev.views.signup;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpModel {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String address;
    @NotEmpty
    private String confirmPassword;
}
