package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateCategoryMapper;
import com.example.managerproduct.mapper.request.UpdateCategoryMapper;
import com.example.managerproduct.mapper.response.CategoryMapper;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final MessageSource messageSource;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final UpdateCategoryMapper updateCategoryMapper = UpdateCategoryMapper.INSTANCE;
    private final CreateCategoryMapper createCategoryMapper = CreateCategoryMapper.INSTANCE;

    @Override
    public ApiResponse<Page<CategoryDto>> getAllCategory(String name, Pageable pageable) {
        ApiResponse<Page<CategoryDto>> apiResponse = new ApiResponse<>();
        Page<Category> products = categoryRepository.getAll(name, pageable);
        List<CategoryDto> productDtos = categoryMapper.DTO_LIST(products.getContent());

        Page<CategoryDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        apiResponse.setResult(result);
        apiResponse.setMessage(result.getTotalElements() != 0 ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Override
    public ApiResponse<List<CategoryDto>> open(String name) {
        ApiResponse<List<CategoryDto>> apiResponse = new ApiResponse<>();
        List<Category> products = categoryRepository.open(name);
        List<CategoryDto> result = categoryMapper.DTO_LIST(products);


        apiResponse.setResult(result);
        apiResponse.setMessage(result != null ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }


    @Transactional
    @Override
    public ApiResponse<CategoryDto> create(CreateCategoryDto categoryDto, String createBy) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Category category = createCategoryMapper.toEntity(categoryDto);
        category.setCreatedDate(new Date());
        category.setCreatedBy(createBy);
        category.setStatus("AVAILABLE");
        category = categoryRepository.save(category);
        CategoryDto result = categoryMapper.toDto(category);

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.create", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<CategoryDto> update(UpdateCategoryDto categoryDto, String modifiedBy) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Long id = categoryDto.getId();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        updateCategoryMapper.updateCategoryFromDto(categoryDto, category);
        category.setId(id);
        category.setModifiedDate(new Date());
        category.setModifiedBy(modifiedBy);
        CategoryDto result = categoryMapper.toDto(category);

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.update", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<Boolean> delete(Long id) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setResult(false);
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));


        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        categoryRepository.deleteById(id);

        apiResponse.setMessage(messageSource.getMessage("success.operation", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(true);
        return apiResponse;
    }
}
