package com.example.managerproduct.repository;


import com.example.managerproduct.repository.common.ExistsInDatabase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistsInDatabaseValidator implements ConstraintValidator<ExistsInDatabase, Long> {

    @PersistenceContext
    private EntityManager entityManager;

    private Class<?> entityClass;

    @Override
    public void initialize(ExistsInDatabase constraintAnnotation) {
        this.entityClass = constraintAnnotation.entity();
    }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        if (id == null) {
            return true; // Không kiểm tra nếu ID là null
        }

        // Kiểm tra sự tồn tại của ID trong cơ sở dữ liệu
        return entityManager.find(entityClass, id) != null;
    }
}
