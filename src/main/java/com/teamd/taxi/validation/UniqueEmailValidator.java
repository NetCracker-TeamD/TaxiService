package com.teamd.taxi.validation;

import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.*;

public class UniqueEmailValidator implements Validator {

    @Autowired
    private UserRepository repository;

    @Override
    public boolean supports(Class<?> aClass) {
        return RegistrationForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        RegistrationForm form = (RegistrationForm) o;
        if (!errors.hasFieldErrors("email")) {
            String email = form.getEmail();
            if (email != null) {
                if (repository.findByEmail(email) != null) {
                    errors.rejectValue(
                            "email",
                            "RegistrationForm.email.notUnique",
                            "User with such email already exist in the system"
                    );
                }
            }
        }
    }
}
