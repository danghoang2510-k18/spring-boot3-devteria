package com.example.identify_service.validator.dob;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
//Target cos nghĩa là annotation này sẽ được apply ở đâu
//    -Tùy theo viêc cấu hình exception như nào thì cọn ElementType tương ứng,
//     có thể để all nhưng sẽ nặng --> nên lựa chọn theo mục đích mà định cấu hình
//      Trong project này chỉ thực hiện catch trên 1 filed ---> chọn field

//Retension cos nghiax là annotation này sẽ được xử lý ở lúc nào
//        -Trong ví dụ này đang config annotation ở runtime(tìm hiểu thêm các kiểu khác)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(List.class)  -- tìm hiểu
//@Documented -- tìm hiểu

//Constraint là khai báo 1 lớp thực hiện validator với interface đã cấu hình này
@Constraint(
        validatedBy = {DobValidator.class}
)
public @interface DobConstraint {
//    Các property cơ bản đối với việc annotation dành cho validation

//    Messagae
    String message() default "Invalid date of birth";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

//    Validator custom (tự config)
//Khai báo tôi thiểu
    int min();
}
