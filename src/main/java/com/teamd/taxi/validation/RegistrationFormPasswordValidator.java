package com.teamd.taxi.validation;


import com.teamd.taxi.models.RegistrationForm;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RegistrationFormPasswordValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return RegistrationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegistrationForm form = ((RegistrationForm) o);
        if (!form.getPassword().equals(form.getPasswordConfirmation())) {
            errors.rejectValue(
                    "passwordConfirmation",
                    "RegistrationForm.passwordConfirmation.mismatch",
                    "Password and confirmation password did not match"
            );
        }
    }
}
