package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.Product;
import com.example.managerproduct.entity.ProductCategory;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateProductMapper;
import com.example.managerproduct.mapper.request.UpdateProductMapper;
import com.example.managerproduct.mapper.response.CategoryMapper;
import com.example.managerproduct.mapper.response.ProductMapper;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.repository.ProductCategoryRepository;
import com.example.managerproduct.repository.ProductRepository;
import com.example.managerproduct.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final MessageSource messageSource;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;
    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final UpdateProductMapper updateProductMapper = UpdateProductMapper.INSTANCE;
    private final CreateProductMapper createProductMapper = CreateProductMapper.INSTANCE;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public ApiResponse<Page<ProductDto>> getAllProduct(String name, String productCode, java.sql.Date startDate, java.sql.Date endDate, Pageable pageable) {
        ApiResponse<Page<ProductDto>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Page<Product> products = productRepository.getAll(name, productCode, startDate, endDate, pageable);

        Map<Long, ProductDto> storeProductDto = products.stream().collect(Collectors.toMap(
                Product::getId,
                productMapper::toDto
        ));

        for (Product product : products) {
            List<Category> categories = product.getProductCategories()
                    .stream().map(ProductCategory::getCategory)
                    .collect(Collectors.toList());

            storeProductDto.get(product.getId()).setCategories(categoryMapper.DTO_LIST(categories));
        }


        List<ProductDto> productDtos = new ArrayList<>(storeProductDto.values());
        Page<ProductDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        apiResponse.setResult(result);
        apiResponse.setMessage(result.getTotalElements() != 0 ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Override
    public ApiResponse<Page<ProductDto>> open(String str, Pageable pageable) {
        ApiResponse<Page<ProductDto>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Page<Product> products = productRepository.open(str, pageable);

        Map<Long, ProductDto> storeProductDto = products.stream().collect(Collectors.toMap(
                Product::getId,
                productMapper::toDto
        ));

        for (Product product : products) {
            List<Category> categories = product.getProductCategories()
                    .stream().filter(pc -> pc.getStatus().equals("AVAILABLE")
                            && pc.getCategory().getStatus().equals("AVAILABLE"))
                    .map(ProductCategory::getCategory)
                    .collect(Collectors.toList());

            storeProductDto.get(product.getId()).setCategories(categoryMapper.DTO_LIST(categories));
        }


        List<ProductDto> productDtos = new ArrayList<>(storeProductDto.values());
        Page<ProductDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        apiResponse.setResult(result);
        apiResponse.setMessage(result.getTotalElements() != 0 ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    public ApiResponse<ProductDto> getById(Long id) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Product product = productRepository.getById(id);
        List<Category> categories = product.getProductCategories()
                .stream().filter(pc -> pc.getStatus().equals("AVAILABLE")
                        && pc.getCategory().getStatus().equals("AVAILABLE"))
                .map(ProductCategory::getCategory)
                .collect(Collectors.toList());


        ProductDto result = productMapper.toDto(product);
        result.setCategories(categoryMapper.DTO_LIST(categories));

        apiResponse.setMessage(messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(result);
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<ProductDto> create(CreateProductDto productDto, String createBy) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Product createProduct = createProductMapper.toEntity(productDto);
        createProduct.setCreatedDate(new Date());
        createProduct.setCreatedBy(createBy);
        createProduct = productRepository.save(createProduct);

        Product product = createProduct;

        // Tạo trực tiếp Category mới
        List<CategoryDto> categoryDtos = productDto.getCategories().stream()
                .map(categoryDto -> {
                    // Lưu thời gian tạo mới
                    categoryDto.setCreatedBy(createBy);
                    categoryDto.setCreatedDate(new Date());
                    return categoryDto;
                }).collect(Collectors.toList());
        List<Category> categories = CategoryMapper.INSTANCE.ENTITY_LIST(categoryDtos);
        categories = categoryRepository.saveAll(categories);

        // Lấy ra id các Categry mới
        Set<Long> idCategoriesNew = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        // Thêm các Category id update và mới create
        Set<Long> newCategoryIds = new HashSet<>(productDto.getCategoryIds());
        newCategoryIds.addAll(idCategoriesNew);


        // Lấy tất cả Categoy id để cập nhập trong StudentCourse
        categories = categoryRepository.findAllById(newCategoryIds);

        Set<ProductCategory> newProductCategory = categories.stream()
                .map(category -> {
                    ProductCategory productCategory = new ProductCategory();

                    productCategory.setCreatedDate(new Date());
                    productCategory.setCreatedBy(createBy);
                    productCategory.setProduct(product);
                    productCategory.setCategory(category);
                    productCategory.setStatus("AVAILABLE");
                    return productCategory;
                })
                .collect(Collectors.toSet());


        productCategoryRepository.saveAll(newProductCategory);

        ProductDto result = productMapper.toDto(product);
        result.setCategories(categoryMapper.DTO_LIST(categories));

        apiResponse.setResult(result);

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.create", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<ProductDto> update(UpdateProductDto productDto, String modifiedBy) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Long id = productDto.getId();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        updateProductMapper.updateProductFromDto(productDto, product);
        product.setId(id);
        product.setModifiedDate(new Date());
        product.setModifiedBy(modifiedBy);

        // Tạo trực tiếp Category mới
        List<CategoryDto> categoryDtos = productDto.getCategories().stream()
                .map(categoryDto -> {
                    // Lưu thời gian tạo mới
                    categoryDto.setCreatedBy(modifiedBy);
                    categoryDto.setCreatedDate(new Date());
                    return categoryDto;
                }).collect(Collectors.toList());
        List<Category> categories = CategoryMapper.INSTANCE.ENTITY_LIST(categoryDtos);
        categories = categoryRepository.saveAll(categories);

        // Lấy ra id các Categry mới
        Set<Long> idCategoriesNew = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        // Lấy ra ProductCategory có quan hệ theo id Product
        List<ProductCategory> existingPC = productCategoryRepository.findByStudentId(product.getId());
        Map<Long, ProductCategory> productCategoryMap = existingPC.stream()
                .collect(Collectors.toMap(pc -> pc.getCategory().getId(), pc -> pc));

        // Thêm các Category id update và mới create
        Set<Long> newCategoryIds = new HashSet<>(productDto.getCategoryIds());
        newCategoryIds.addAll(idCategoriesNew);

        // Lấy ra các id có trong student course, nhưng không có trong update để hủy Course
        Set<Long> idToCloseCategories = productCategoryMap.keySet().stream()
                .filter(idRelation -> !newCategoryIds.contains(idRelation))
                .collect(Collectors.toSet());

        if (!idToCloseCategories.isEmpty()) {
            productCategoryRepository.changeStatusByProductAndCategories(product.getId(), idToCloseCategories, "UNAVAILABLE");
        }

        // Lấy tất cả Categoy id để cập nhập trong StudentCourse
        categories = categoryRepository.findAllById(newCategoryIds);

        Set<ProductCategory> newProductCategory = categories.stream()
                .map(category -> {
                    ProductCategory productCategory = productCategoryMap.get(category.getId());
                    if (productCategory == null) {
                        productCategory = new ProductCategory();

                        productCategory.setCreatedDate(new Date());
                        productCategory.setCreatedBy(modifiedBy);
                    }
                    productCategory.setProduct(product);
                    productCategory.setCategory(category);
                    productCategory.setModifiedDate(new Date());
                    productCategory.setModifiedBy(modifiedBy);
                    productCategory.setStatus("AVAILABLE");
                    return productCategory;
                })
                .collect(Collectors.toSet());


        productCategoryRepository.saveAll(newProductCategory);

        ProductDto result = productMapper.toDto(product);
        result.setCategories(categoryMapper.DTO_LIST(categories));

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


        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        productRepository.deleteById(id);

        apiResponse.setMessage(messageSource.getMessage("success.operation", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(true);
        return apiResponse;
    }
}
