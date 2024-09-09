package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateProductDto;
import com.example.managerproduct.dto.request.UpdateProductDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.dto.response.ProductDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.ImageProduct;
import com.example.managerproduct.entity.Product;
import com.example.managerproduct.entity.ProductCategory;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateProductMapper;
import com.example.managerproduct.mapper.request.ImageProductMapper;
import com.example.managerproduct.mapper.request.UpdateProductMapper;
import com.example.managerproduct.mapper.response.CategoryMapper;
import com.example.managerproduct.mapper.response.ProductMapper;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.repository.ImageProductRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    private final ImageProductMapper imageProductMapper = ImageProductMapper.INSTANCE;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ImageProductRepository imageProductRepository;
    private final String IMAGE_DIRECTORY = "D:\\MyProject\\ImageProduct"; // Thay đổi đường dẫn nếu cần
    private final Path rootLocation = Paths.get(IMAGE_DIRECTORY);

//    @Override
//    public ApiResponse<Page<ProductDto>> getAllProduct(String name, String productCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        ApiResponse<Page<ProductDto>> apiResponse = new ApiResponse<>();
//        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));
//
//        Page<Product> products = productRepository.getAll(name, productCode, startDate, endDate, pageable);
//
//        Map<Long, ProductDto> storeProductDto = products.stream().collect(Collectors.toMap(
//                Product::getId,
//                productMapper::toDto
//        ));
//
//        for (Product product : products) {
//            List<Category> categories = product.getProductCategories()
//                    .stream().map(ProductCategory::getCategory)
//                    .collect(Collectors.toList());
//
//            storeProductDto.get(product.getId()).setCategories(categoryMapper.DTO_LIST(categories));
//        }
//
//
//        List<ProductDto> productDtos = new ArrayList<>(storeProductDto.values());
//        Page<ProductDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());
//
//        apiResponse.setResult(result);
//        apiResponse.setMessage(result.getTotalElements() != 0 ?
//                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
//                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
//        return apiResponse;
//    }

    @Override
    public ApiResponse<Page<ProductDto>> getAllProduct(String name, String status, String productCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        ApiResponse<Page<ProductDto>> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        // Truy vấn từ repository
        Page<Object[]> products = productRepository.searchAll(name, status, productCode, startDate, endDate, pageable);

        // Tạo danh sách ProductDto
        List<ProductDto> productDtos = products.stream().map(productObj -> {
            Object[] product = (Object[]) productObj;

            // Map từng trường của mảng Object[] sang ProductDto
            ProductDto dto = new ProductDto();
            dto.setPrice((Double) product[0]); // Giá
            dto.setStatus((String) product[1]); // Trạng thái
            dto.setCreatedDate((Date) product[2]); // Ngày tạo
            dto.setId((Long) product[3]); // ID
            dto.setModifiedDate(product[4] != null ? (Date) product[4] : null); // Ngày chỉnh sửa
            dto.setQuantity((Long) product[5]); // Số lượng
            dto.setCreatedBy((String) product[6]); // Người tạo
            dto.setDescription((String) product[7]); // Mô tả
            dto.setModifiedBy(product[8] != null ? (String) product[8] : null); // Người chỉnh sửa
            dto.setName((String) product[9]); // Tên sản phẩm
            dto.setProductCode((String) product[10]); // Mã sản phẩm
            dto.setImageLink((String) product[11]); // Ảnh sản phẩm
            dto.setNameCategory(product[12] != null ? (String) product[12] : null); // Danh mục sản phẩm

            return dto;
        }).collect(Collectors.toList());

        // Chuyển đổi thành Page
        Page<ProductDto> result = new PageImpl<>(productDtos, pageable, products.getTotalElements());

        // Cập nhật message và result vào apiResponse
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
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Product product = productRepository.getById(id);
        List<Category> categories = product.getProductCategories()
                .stream().filter(pc -> pc.getStatus().equals("AVAILABLE")
                        && pc.getCategory().getStatus().equals("AVAILABLE"))
                .map(ProductCategory::getCategory)
                .collect(Collectors.toList());

        List<ImageProduct> imageProducts = product.getImageProducts().stream().toList();

        ProductDto result = productMapper.toDto(product);
        result.setCategories(categoryMapper.DTO_LIST(categories));
        result.setImageProducts(imageProductMapper.DTO_LIST(imageProducts));

        apiResponse.setMessage(messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(result);
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<ProductDto> create(CreateProductDto productDto, MultipartFile[] images, String createBy) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        if (productRepository.existsByProductCode(productDto.getProductCode())) {
            throw new AppException(ErrorCode.PRODUCT_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

        // Xử lý Category code nếu có
        if (productDto.getCategories() != null && !productDto.getCategories().isEmpty()) {
            Set<String> categoryCodes = productDto.getCategories().stream()
                    .map(CategoryDto::getCategoryCode)
                    .collect(Collectors.toSet());

            List<String> existingCodes = categoryRepository.findExistingCategoryCodes(categoryCodes);

            // Báo categoryCode đã tồn tại
            if (!existingCodes.isEmpty()) {
                throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);
            }
        }


        Product createProduct = createProductMapper.toEntity(productDto);
        createProduct.setCreatedDate(new Date());
        createProduct.setCreatedBy(createBy);
        createProduct = productRepository.save(createProduct);

        Product product = createProduct;

        // Xử lý lưu ảnh
        List<ImageProduct> imageProducts = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageName = saveImageToFileSystem(file); // Lưu ảnh và lấy tên tệp duy nhất
                    String imagePath = IMAGE_DIRECTORY + imageName; // Đường dẫn ảnh nếu cần thiết

                    ImageProduct imageProduct = new ImageProduct();
                    imageProduct.setImageName(imageName); // Lưu tên hình ảnh duy nhất
                    imageProduct.setImagePath(imagePath);
                    imageProduct.setProduct(createProduct);
                    imageProduct.setCreatedBy(createBy);
                    imageProduct.setCreatedDate(new Date());
                    imageProduct.setStatus("AVAILABLE");

                    imageProducts.add(imageProduct);
                }
            }
            imageProductRepository.saveAll(imageProducts);
        }
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

        // Lấy tất cả Category id để cập nhập trong StudentCourse
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
        result.setImageProducts(imageProductMapper.DTO_LIST(imageProducts));

        apiResponse.setResult(result);

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.create", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    private String saveImageToFileSystem(MultipartFile file) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(rootLocation)) {
                Files.createDirectories(rootLocation);
            }

            // Tạo tên tệp duy nhất để tránh trùng lặp
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Đường dẫn tệp
            Path filePath = rootLocation.resolve(uniqueFileName);
            file.transferTo(filePath.toFile());

            // Trả về tên tệp duy nhất để lưu vào cơ sở dữ liệu
            return uniqueFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    private List<ImageProduct> saveImagesToDB(MultipartFile[] images, Product product, String modifiedBy) {

        List<ImageProduct> imagesToCreate = new ArrayList<>();
        // yêu cầu tải ảnh mới với MultipartFile
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageName = saveImageToFileSystem(file);  // Lưu ảnh và lấy tên tệp duy nhất
                    String imagePath = IMAGE_DIRECTORY + "\\" + imageName;

                    ImageProduct newImageProduct = new ImageProduct();
                    newImageProduct.setImageName(imageName);
                    newImageProduct.setImagePath(imagePath);
                    newImageProduct.setProduct(product);
                    newImageProduct.setCreatedDate(new Date());
                    newImageProduct.setCreatedBy(modifiedBy);
                    newImageProduct.setModifiedDate(new Date());
                    newImageProduct.setModifiedBy(modifiedBy);
                    newImageProduct.setStatus("AVAILABLE");

                    imagesToCreate.add(newImageProduct);
                }
            }
        }

        return imagesToCreate;
    }

    private List<ImageProduct> updateStatusImages(List<Long> imageIdsToDelete, String modifiedBy, Long productId) throws RuntimeException {
        // Xử lý ảnh sản phẩm
        List<ImageProduct> existingImages = imageProductRepository.findByProductId(productId);
        Map<Long, ImageProduct> existingImagesMap = existingImages.stream()
                .collect(Collectors.toMap(ImageProduct::getId, img -> img));

        Set<Long> newImageIds = new HashSet<>(imageIdsToDelete); // Danh sách ID ảnh mới được cung cấp
        Set<Long> existingImageIds = existingImagesMap.keySet(); // Danh sách ID ảnh hiện tại

        // Các ID ảnh cần được set trạng thái "UNAVAILABLE" vì không còn được sử dụng
        Set<Long> idsToSetUnavailable = existingImageIds.stream()
                .filter(existingId -> !newImageIds.contains(existingId))
                .collect(Collectors.toSet());

        if (!idsToSetUnavailable.isEmpty()) {
            List<ImageProduct> imagesToSetUnavailable = existingImages.stream()
                    .filter(img -> idsToSetUnavailable.contains(img.getId()))
                    .collect(Collectors.toList());

            imagesToSetUnavailable.forEach(img -> {
                img.setStatus("UNAVAILABLE");
                img.setModifiedDate(new Date());
                img.setModifiedBy(modifiedBy);
            });

            imageProductRepository.saveAll(imagesToSetUnavailable);
        }

        // Xử lý các ảnh được cung cấp, set trạng thái "AVAILABLE"
        List<ImageProduct> imagesToUpdate = new ArrayList<>();
        for (Long imageId : newImageIds) {
            ImageProduct imageProduct = existingImagesMap.get(imageId);

            if (imageProduct != null) {
                imageProduct.setStatus("AVAILABLE");
                imageProduct.setModifiedDate(new Date());
                imageProduct.setModifiedBy(modifiedBy);
                imagesToUpdate.add(imageProduct);

            }
        }

        return (imagesToUpdate);

    }

    @Transactional
    @Override
    public ApiResponse<ProductDto> update(UpdateProductDto productDto, MultipartFile[] images, String modifiedBy) {
        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Long id = productDto.getId();
        // Kiểm tra xem productCode đã tồn tại với sản phẩm khác chưa
        if (productRepository.existsByProductCodeAndIdNot(productDto.getProductCode(), id)) {
            throw new AppException(ErrorCode.PRODUCT_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        updateProductMapper.updateProductFromDto(productDto, product);
        product.setId(id);
        product.setModifiedDate(new Date());
        product.setModifiedBy(modifiedBy);

        // Xử lý Category code nếu có
        if (productDto.getCategories() != null && !productDto.getCategories().isEmpty()) {
            Set<String> categoryCodes = productDto.getCategories().stream()
                    .map(CategoryDto::getCategoryCode)
                    .collect(Collectors.toSet());

            List<String> existingCodes = categoryRepository.findExistingCategoryCodes(categoryCodes);

            // Báo categoryCode đã tồn tại
            if (!existingCodes.isEmpty()) {
                throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);
            }
        }

        // Xử lý ảnh sản phẩm
        List<ImageProduct> imagesToUpdate = new ArrayList<>();
        imagesToUpdate.addAll(updateStatusImages(productDto.getImageIds(), modifiedBy, id));
        imagesToUpdate.addAll(saveImagesToDB(images, product, modifiedBy));
        imagesToUpdate = imageProductRepository.saveAll(imagesToUpdate);

        // Xử lý các danh mục
        List<CategoryDto> categoryDtos = productDto.getCategories().stream()
                .map(categoryDto -> {
                    categoryDto.setCreatedBy(modifiedBy);
                    categoryDto.setCreatedDate(new Date());
                    return categoryDto;
                }).collect(Collectors.toList());
        List<Category> categories = CategoryMapper.INSTANCE.ENTITY_LIST(categoryDtos);
        categories = categoryRepository.saveAll(categories);

        Set<Long> idCategoriesNew = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        List<ProductCategory> existingPC = productCategoryRepository.findByProductId(product.getId());
        Map<Long, ProductCategory> productCategoryMap = existingPC.stream()
                .collect(Collectors.toMap(pc -> pc.getCategory().getId(), pc -> pc));

        Set<Long> newCategoryIds = new HashSet<>(productDto.getCategoryIds());
        newCategoryIds.addAll(idCategoriesNew);

        Set<Long> idToCloseCategories = productCategoryMap.keySet().stream()
                .filter(idRelation -> !newCategoryIds.contains(idRelation))
                .collect(Collectors.toSet());

        if (!idToCloseCategories.isEmpty()) {
            productCategoryRepository.changeStatusByProductAndCategories(product.getId(), idToCloseCategories, "UNAVAILABLE");
        }

        categories = categoryRepository.findAllById(newCategoryIds).stream()
                .filter(category -> "AVAILABLE".equals(category.getStatus()))
                .collect(Collectors.toList());

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
        result.setImageProducts(imageProductMapper.DTO_LIST(imagesToUpdate));  // Cập nhật danh sách ảnh với ảnh mới

        apiResponse.setResult(result);
        apiResponse.setMessage(messageSource.getMessage("success.update", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    public void deleteImageFromFileSystem(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("Deleted file: " + imagePath);
            } else {
                System.out.println("File not found: " + imagePath);
            }
        } catch (IOException e) {
            // Log lỗi hoặc ném ngoại lệ tùy vào yêu cầu xử lý lỗi của bạn
            System.err.println("Error deleting file: " + imagePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    @Transactional
    @Override
    public ApiResponse<ProductDto> deleteMem(Long id) {
        Locale locale = LocaleContextHolder.getLocale();

        ApiResponse<ProductDto> apiResponse = new ApiResponse<>();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_EXISTS));

        if (product.getStatus().equals("AVAILABLE")) {
            product.setStatus("UNAVAILABLE");
            product.setModifiedBy("admin");
            product.setModifiedDate(new Date());
            productRepository.save(product);

            List<ProductCategory> productCourses = productCategoryRepository.findProductCategoryByIdProduct(product.getId());
            productCourses.forEach(sc -> sc.setStatus("UNAVAILABLE"));
            productCourses.forEach(sc -> sc.setModifiedDate(new Date()));
            productCourses.forEach(sc -> sc.setModifiedBy("admin"));

            productCategoryRepository.saveAll(productCourses);

            ProductDto productDto = productMapper.toDto(product);
            apiResponse.setMessage(messageSource.getMessage("success.soft.delete", null, LocaleContextHolder.getLocale()));
            apiResponse.setResult(productDto);
        } else {
            throw new AppException(ErrorCode.PRODUCT_EXISTS);
        }
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<Boolean> delete(Long id) {
        ApiResponse<Boolean> apiResponse = new ApiResponse<>();
        apiResponse.setResult(false);
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));


        if (!productRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        productRepository.deleteById(id);

        apiResponse.setMessage(messageSource.getMessage("success.operation", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(true);
        return apiResponse;
    }
}
