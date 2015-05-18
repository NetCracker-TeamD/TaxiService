package com.teamd.taxi.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.*;

/**
 * Created on 14-May-15.
 *
 * @author Nazar Dub
 */
@Component
public class DriverValidateUtil extends HierarchyValidationUtil {
    private final static Set<String> VALIDATION_LEVEL_ONE =
            new HashSet<>(Arrays.asList("NotBlank", "NotNull", "NotBlankOrNull"));
    private final static Set<String> VALIDATION_LEVEL_TWO =
            new HashSet<>(Arrays.asList("DriverFeatures", "License", "Phone", "Email", "FreeCarId","ExistingDriverId"));
    private final static Set<String> VALIDATION_LEVEL_THREE =
            new HashSet<>(Arrays.asList("NotBlank", "UniqueDriverEmail"));

    public DriverValidateUtil() {
        super(VALIDATION_LEVEL_ONE, VALIDATION_LEVEL_TWO, VALIDATION_LEVEL_THREE);
    }

    @Override
    public List<FieldError> filterErrors(List<FieldError> errors) {
        HashMap<String, FieldError> result = new HashMap<>();
        for (FieldError error : errors) {
            String field = error.getField();
            if (!result.keySet().contains(field)) {
                result.put(field, error);
            } else {
                result.put(field, higherError(result.get(field), error));
            }
        }
        return new ArrayList<>(result.values());
    }
}
