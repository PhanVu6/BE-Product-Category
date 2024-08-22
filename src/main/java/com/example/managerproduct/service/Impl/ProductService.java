package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.entity.Product;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateProductMapper;
import com.example.managerproduct.mapper.request.UpdateProductMapper;
import com.example.managerproduct.mapper.response.ProductMapper;
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

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final MessageSource messageSource;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;
    private final UpdateProductMapper updateProductMapper = UpdateProductMapper.INSTANCE;
    private final CreateProductMapper createProductMapper = CreateProductMapper.INSTANCE;

    @Override
    public ApiResponse<Page<ProductDto>> getAllProduct(String name, Pageable pageable) {
        ApiResponse<Page<ProductDto>> apiResponse = new ApiResponse<>();
        Page<Product> products = productRepository.getAll(name, pageable);
        List<ProductDto> productDtos = productMapper.DTO_LIST(products.getContent());

        Page<ProductDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        apiResponse.setResult(result);
        apiResponse.setMessage(result.getTotalElements() != 0 ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<ProductDto> create(CreateProductDto productDto, String createBy) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Product product = createProductMapper.toEntity(productDto);
        product.setCreatedDate(new Date());
        product.setCreatedBy(createBy);
        product = productRepository.save(product);
        ProductDto result = productMapper.toDto(product);

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
        ProductDto result = productMapper.toDto(product);

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.update", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

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
