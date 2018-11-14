package com.genie.home.genieapp.auth;

import java.util.List;

public class UserRegistrationError {

    private List<FieldValidationError> fieldErrors;
    private List<String> objectErrors;

    public List<FieldValidationError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldValidationError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public List<String> getObjectErrors() {
        return objectErrors;
    }

    public void setObjectErrors(List<String> objectErrors) {
        this.objectErrors = objectErrors;
    }

}
