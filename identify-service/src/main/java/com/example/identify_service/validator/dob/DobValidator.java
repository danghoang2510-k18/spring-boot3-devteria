package com.example.identify_service.validator.dob;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;


//    Mỗi khi constraint này khởi tạo sẽ get được thông số annotation
//      Vd; khi muốn biết value người dùng nhậo là bao nhiêu thì có thể get
//          ở phương thức này
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

        min = constraintAnnotation.min();
    }

//    Hàm xử lý data này có đúng hay k( xuử lý logic)
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(value))
            return true;

        long years = ChronoUnit.YEARS.between(value,LocalDate.now());



        return years >= min;
    }
}
