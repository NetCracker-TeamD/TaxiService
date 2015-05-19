package com.teamd.taxi.validation;

import com.google.maps.errors.NotFoundException;
import com.teamd.taxi.exception.MapServiceNotAvailableException;
import com.teamd.taxi.models.AddressForm;
import com.teamd.taxi.models.RegistrationForm;
import com.teamd.taxi.persistence.repository.UserAddressRepository;
import com.teamd.taxi.persistence.repository.UserRepository;
import com.teamd.taxi.service.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class AddressExistenceValidator implements Validator {

    @Autowired
    private MapService mapService;

    @Override
    public boolean supports(Class<?> aClass) {
        return AddressForm.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        AddressForm form = (AddressForm) o;
        if (!errors.hasFieldErrors("address")) {
            String address = form.getAddress();
            if (address != null) {
                try {
                    mapService.checkAddress(address);
                } catch (NotFoundException e) {
                    errors.rejectValue("address", "AddressForm.address.notExist");
                } catch (MapServiceNotAvailableException e) {
                    errors.reject("MapService.unavailable", null);
                }
            }
        }
    }
}
