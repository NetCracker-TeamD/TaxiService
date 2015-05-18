package com.teamd.taxi.validation;

import com.teamd.taxi.models.admin.CarModel;
import com.teamd.taxi.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Anatoliy on 15.05.2015.
 */
@Component
public class AdminCarValidator implements Validator{
    private static final Pattern PATTERN_MODEL_NAME = Pattern.compile("[!@%$|*\\\\#/><;^?,=]+");
    private static final Pattern PATTERN_CLASS_ID = Pattern.compile("^[123]$");
    private static final Pattern PATTERN_CATEGORY = Pattern.compile("^[ABCD]$");
    private static final Pattern PATTERN_CAR_ENABLE = Pattern.compile("^(true|false)$");
    private static final Pattern PATTERN_DRIVER_ID = Pattern.compile("^(-1|\\d+)$");
    private static final Pattern PATTERN_CAR_FEATURE_ID = Pattern.compile("^\\d+$");
    private static final Pattern PATTERN_CAR_FEATURE_VALUE = PATTERN_CAR_ENABLE;

    private static final String MODEL_NAME = "modelName";
    private static final String CLASS_ID = "classId";
    private static final String CATEGORY = "category";
    private static final String ENABLE = "enable";
    private static final String DRIVER_ID = "driverId";
    private static final String MAP_FEATURES = "mapFeatures";

    @Autowired
    private CarService carService;

    @Override
    public boolean supports(Class<?> aClass) {
        return CarModel.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object value, Errors errors) {
        CarModel carModel = (CarModel) value;

        if (carModel.getModelName().isEmpty() || carModel.getModelName().replaceAll(" ","").isEmpty()) {
            errors.rejectValue(MODEL_NAME, "admin.validate.carModelName.empty");
        } else {
            if(PATTERN_MODEL_NAME.matcher(carModel.getModelName()).find()){
                errors.rejectValue(MODEL_NAME, "admin.validate.carModelName.illegalArgument");
            } else if(carModel.getModelName().length() > 50){
                errors.rejectValue(MODEL_NAME, "admin.validate.carModelName.length");
            }
        }

        if (carModel.getClassId().isEmpty() || carModel.getClassId().replaceAll(" ", "").isEmpty()) {
            errors.rejectValue(CLASS_ID,"admin.validate.carClassId.empty");
        } else {
            if(!PATTERN_CLASS_ID.matcher(carModel.getClassId()).matches()){
                errors.rejectValue(CLASS_ID,"admin.validate.carClassId.illegalArgument");
            }
        }

        if (carModel.getCategory().isEmpty() || carModel.getCategory().replaceAll(" ", "").isEmpty()) {
            errors.rejectValue(CATEGORY,"admin.validate.carCategory.empty");
        } else {
            if(!PATTERN_CATEGORY.matcher(carModel.getCategory()).matches()){
                errors.rejectValue(CATEGORY,"admin.validate.carCategory.illegalArgument");
            }
        }

        if (carModel.getEnable().isEmpty() || carModel.getEnable().replaceAll(" ", "").isEmpty()) {
            errors.rejectValue(ENABLE,"admin.validate.carEnable.empty");
        } else {
            if(!PATTERN_CAR_ENABLE.matcher(carModel.getEnable()).matches()){
                errors.rejectValue(ENABLE,"admin.validate.carEnable.illegalArgument");
            }
        }

        if (carModel.getDriverId().isEmpty() || carModel.getDriverId().replaceAll(" ", "").isEmpty()) {
            errors.rejectValue(DRIVER_ID,"admin.validate.carDriverId.empty");
        } else {
            if(!PATTERN_DRIVER_ID.matcher(carModel.getDriverId()).matches()){
                errors.rejectValue(DRIVER_ID,"admin.validate.carDriverId.illegalArgument");
            }else{
                if (!carModel.getDriverId().equals("-1")) {
                    List<Integer> listId = carService.getAllIdDrivers();
                    if(!listId.contains(Integer.parseInt(carModel.getDriverId()))){
                        errors.rejectValue(DRIVER_ID,"admin.validate.carDriverId.notExistSuchDriverId");
                    }
                }
            }
        }

        if (!carModel.getMapFeatures().isEmpty()) {
            boolean isErrorInFeatureId = false;
            boolean isErrorInFeatureValue = false;
            int j=0;
            Integer [] arrayFeatureId = new Integer[carModel.getMapFeatures().keySet().size()];

            for(String featureId : carModel.getMapFeatures().keySet()){
                if(!PATTERN_CAR_FEATURE_ID.matcher(featureId).matches()){
                    isErrorInFeatureId = true;
                    break;
                }else{
                    arrayFeatureId[j] = Integer.parseInt(featureId);
                    j++;
                }
            }
            if(!isErrorInFeatureId){
                for(String featureValue : carModel.getMapFeatures().values()){
                    if(!PATTERN_CAR_FEATURE_VALUE.matcher(featureValue).matches()){
                        isErrorInFeatureValue = true;
                        break;
                    }
                }
                if(!isErrorInFeatureValue){
                    List<Integer> listId = carService.getAllIdFeatures();
                    for(int i=0; i<arrayFeatureId.length; i++){
                        if(!listId.contains(arrayFeatureId[i])){
                            errors.rejectValue(MAP_FEATURES,"admin.validate.carMapFeatures.notExistFeatureId");
                        }
                    }
                }
                else errors.rejectValue(MAP_FEATURES,"admin.validate.carMapFeatures.illegalArgument");
            }
            else errors.rejectValue(MAP_FEATURES,"admin.validate.carMapFeatures.illegalArgument");
        }else errors.rejectValue(MAP_FEATURES,"admin.validate.carMapFeatures.empty");
    }
}
