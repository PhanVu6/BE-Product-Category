package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.ImageCategory;
import com.example.managerproduct.entity.ProductCategory;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateCategoryMapper;
import com.example.managerproduct.mapper.request.ImageCategoryMapper;
import com.example.managerproduct.mapper.request.UpdateCategoryMapper;
import com.example.managerproduct.mapper.response.CategoryMapper;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.repository.ImageCategoryRepository;
import com.example.managerproduct.repository.ProductCategoryRepository;
import com.example.managerproduct.service.ICategoryService;
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
public class CategoryService implements ICategoryService {

    private final String IMAGE_DIRECTORY = "D:\\MyProject\\ImageCategory";
    private final Path rootLocation = Paths.get(IMAGE_DIRECTORY);
    private final MessageSource messageSource;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;
    private final UpdateCategoryMapper updateCategoryMapper = UpdateCategoryMapper.INSTANCE;
    private final CreateCategoryMapper createCategoryMapper = CreateCategoryMapper.INSTANCE;
    private final ImageCategoryMapper imageCategoryMapper = ImageCategoryMapper.INSTANCE;
    private final ImageCategoryRepository imageCategoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    //    @Override
//    public ApiResponse<Page<CategoryDto>> getAllCategory(String name, String status, String categoryCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {
//        ApiResponse<Page<CategoryDto>> apiResponse = new ApiResponse<>();
//        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));
//
//        // Truy vấn từ repository
//        Page<Object[]> categorys = categoryRepository.searchAll(name, status, categoryCode, startDate, endDate, pageable);
//
//        // Tạo danh sách CategoryDto
//        List<CategoryDto> categoryDtos = categorys.stream().map(categoryObj -> {
//            Object[] category = (Object[]) categoryObj;
//
//            // Map từng trường của mảng Object[] sang CategoryDto
//            CategoryDto dto = new CategoryDto();// Giá
//            dto.setStatus((String) category[0]); // Trạng thái
//            dto.setCreatedDate((Date) category[1]); // Ngày tạo
//            dto.setId((Long) category[2]); // ID
//            dto.setModifiedDate(category[3] != null ? (Date) category[3] : null); // Ngày chỉnh sửa
//            dto.setCategoryCode((String) category[4]); // Mã sản phẩm
//            dto.setCreatedBy((String) category[5]); // Người tạo
//            dto.setDescription((String) category[6]); // Mô tả
//            dto.setModifiedBy(category[7] != null ? (String) category[8] : null); // Người chỉnh sửa
//            dto.setName((String) category[8]); // Tên danh mục sản phẩm
//
//            dto.setImageLink((String) category[9]); // Ảnh sản phẩm
//
//            return dto;
//        }).collect(Collectors.toList());
//
//        // Chuyển đổi thành Page
//        Page<CategoryDto> result = new PageImpl<>(categoryDtos, pageable, categorys.getTotalElements());
//
//        // Cập nhật message và result vào apiResponse
//        apiResponse.setResult(result);
//        apiResponse.setMessage(result.getTotalElements() != 0 ?
//                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
//                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
//
//        return apiResponse;
//    }
    @Override
    public ApiResponse<Page<CategoryDto>> getAllCategory(String name, String status, String categoryCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        ApiResponse<Page<CategoryDto>> apiResponse = new ApiResponse<>();
        Page<Category> category = categoryRepository.getAll(name, status, categoryCode, startDate, endDate, pageable);
        List<CategoryDto> categoryDtos = categoryMapper.DTO_LIST(category.getContent());

        Page<CategoryDto> result = new PageImpl<>(categoryDtos, pageable, category.getTotalElements());

        apiResponse.setResult(result);
        apiResponse.setMessage(result.getTotalElements() != 0 ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    @Override
    public ApiResponse<List<CategoryDto>> open(String name) {
        ApiResponse<List<CategoryDto>> apiResponse = new ApiResponse<>();
        List<Category> category = categoryRepository.open(name);
        List<CategoryDto> result = categoryMapper.DTO_LIST(category);


        apiResponse.setResult(result);
        apiResponse.setMessage(result != null ?
                messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale())
                : messageSource.getMessage("error.get.not.found", null, LocaleContextHolder.getLocale()));
        return apiResponse;
    }

    public ApiResponse<CategoryDto> getById(Long id) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Category category = categoryRepository.getById(id);

        CategoryDto result = categoryMapper.toDto(category);
        result.setImageCategories(imageCategoryMapper.DTO_LIST(category.getImageCategories().stream().toList()));

        apiResponse.setMessage(messageSource.getMessage("success.get.all", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(result);
        return apiResponse;
    }

    @Transactional
    @Override
    public ApiResponse<CategoryDto> createCategory(CreateCategoryDto categoryDto, MultipartFile[] images, String createBy) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        // Kiểm tra xem categoryCode đã tồn tại chưa
        if (categoryRepository.existsByCategoryCode(categoryDto.getCategoryCode())) {
            throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

        Category category = createCategoryMapper.toEntity(categoryDto);
        category.setCreatedDate(new Date());
        category.setCreatedBy(createBy);
        category = categoryRepository.save(category);
        CategoryDto result = categoryMapper.toDto(category);

        // Xử lý lưu ảnh
        List<ImageCategory> imageCategories = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageName = saveImageToFileSystem(file); // Lưu ảnh và lấy tên tệp duy nhất
                    String imagePath = IMAGE_DIRECTORY + imageName; // Đường dẫn ảnh nếu cần thiết

                    ImageCategory imageCategory = new ImageCategory();
                    imageCategory.setImageName(imageName); // Lưu tên hình ảnh duy nhất
                    imageCategory.setImagePath(imagePath);
                    imageCategory.setCategory(category);
                    imageCategory.setCreatedBy(createBy);
                    imageCategory.setCreatedDate(new Date());
                    imageCategory.setStatus("AVAILABLE");

                    imageCategories.add(imageCategory);
                }
            }
            imageCategoryRepository.saveAll(imageCategories);
        }

        result.setImageCategories(imageCategoryMapper.DTO_LIST(imageCategories));

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

    private List<ImageCategory> saveImagesToDB(MultipartFile[] images, Category category, String modifiedBy) {

        List<ImageCategory> imagesToCreate = new ArrayList<>();
        // yêu cầu tải ảnh mới với MultipartFile
        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageName = saveImageToFileSystem(file);  // Lưu ảnh và lấy tên tệp duy nhất
                    String imagePath = IMAGE_DIRECTORY + "\\" + imageName;

                    ImageCategory newImageCategory = new ImageCategory();
                    newImageCategory.setImageName(imageName);
                    newImageCategory.setImagePath(imagePath);
                    newImageCategory.setCategory(category);
                    newImageCategory.setCreatedDate(new Date());
                    newImageCategory.setCreatedBy(modifiedBy);
                    newImageCategory.setModifiedDate(new Date());
                    newImageCategory.setModifiedBy(modifiedBy);
                    newImageCategory.setStatus("AVAILABLE");

                    imagesToCreate.add(newImageCategory);
                }
            }
        }

        return imagesToCreate;
    }

    private List<ImageCategory> updateStatusImages(List<Long> imageIdsToDelete, String modifiedBy, Long categoryId) throws RuntimeException {
        // Xử lý ảnh sản phẩm
        List<ImageCategory> existingImages = imageCategoryRepository.findByCategoryId(categoryId);
        Map<Long, ImageCategory> existingImagesMap = existingImages.stream()
                .collect(Collectors.toMap(ImageCategory::getId, img -> img));

        Set<Long> newImageIds = new HashSet<>(imageIdsToDelete); // Danh sách ID ảnh mới được cung cấp
        Set<Long> existingImageIds = existingImagesMap.keySet(); // Danh sách ID ảnh hiện tại

        // Các ID ảnh cần được set trạng thái "UNAVAILABLE" vì không còn được sử dụng
        Set<Long> idsToSetUnavailable = existingImageIds.stream()
                .filter(existingId -> !newImageIds.contains(existingId))
                .collect(Collectors.toSet());

        if (!idsToSetUnavailable.isEmpty()) {
            List<ImageCategory> imagesToSetUnavailable = existingImages.stream()
                    .filter(img -> idsToSetUnavailable.contains(img.getId()))
                    .collect(Collectors.toList());

            imagesToSetUnavailable.forEach(img -> {
                img.setStatus("UNAVAILABLE");
                img.setModifiedDate(new Date());
                img.setModifiedBy(modifiedBy);
            });

            imageCategoryRepository.saveAll(imagesToSetUnavailable);
        }

        // Xử lý các ảnh được cung cấp, set trạng thái "AVAILABLE"
        List<ImageCategory> imagesToUpdate = new ArrayList<>();
        for (Long imageId : newImageIds) {
            ImageCategory imageProduct = existingImagesMap.get(imageId);

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
    public ApiResponse<CategoryDto> updateCategoryImages(UpdateCategoryDto categoryDto, MultipartFile[] images, String modifiedBy) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        Long id = categoryDto.getId();
        // Kiểm tra xem categoryCode đã tồn tại với danh mục khác chưa
        if (categoryRepository.existsByCategoryCodeAndIdNot(categoryDto.getCategoryCode(), id)) {
            throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        updateCategoryMapper.updateCategoryFromDto(categoryDto, category);
        category.setId(id);
        category.setCategoryCode(category.getCategoryCode());
        category.setModifiedDate(new Date());
        category.setModifiedBy(modifiedBy);

        // Xử lý ảnh sản phẩm
        List<ImageCategory> imagesToUpdate = new ArrayList<>();
        imagesToUpdate.addAll(updateStatusImages(categoryDto.getImageIds(), modifiedBy, id));
        imagesToUpdate.addAll(saveImagesToDB(images, category, modifiedBy));
        imagesToUpdate = imageCategoryRepository.saveAll(imagesToUpdate);


        CategoryDto result = categoryMapper.toDto(category);
        result.setImageCategories(imageCategoryMapper.DTO_LIST(imagesToUpdate));  // Cập nhật danh sách ảnh với ảnh mới


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
    public ApiResponse<CategoryDto> create(CreateCategoryDto categoryDto, String createBy) {
        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        apiResponse.setMessage(messageSource.getMessage("error.operation", null, LocaleContextHolder.getLocale()));

        // Kiểm tra xem categoryCode đã tồn tại chưa
        if (categoryRepository.existsByCategoryCode(categoryDto.getCategoryCode())) {
            throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

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
        // Kiểm tra xem categoryCode đã tồn tại với danh mục khác chưa
        if (categoryRepository.existsByCategoryCodeAndIdNot(categoryDto.getCategoryCode(), id)) {
            throw new AppException(ErrorCode.CATEGORY_CODE_ALREADY_EXISTS);  // Tùy biến exception của bạn
        }

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

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
    public ApiResponse<CategoryDto> deleteMem(Long id) {
        Locale locale = LocaleContextHolder.getLocale();

        ApiResponse<CategoryDto> apiResponse = new ApiResponse<>();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_EXISTS));

        if (category.getStatus().equals("AVAILABLE")) {
            category.setStatus("UNAVAILABLE");
            category.setModifiedBy("admin");
            category.setModifiedDate(new Date());
            categoryRepository.save(category);

            List<ProductCategory> categoryCourses = productCategoryRepository.findProductCategoryByIdCategory(category.getId());
            categoryCourses.forEach(sc -> sc.setStatus("UNAVAILABLE"));
            categoryCourses.forEach(sc -> sc.setModifiedDate(new Date()));
            categoryCourses.forEach(sc -> sc.setModifiedBy("admin"));

            productCategoryRepository.saveAll(categoryCourses);

            CategoryDto result = categoryMapper.toDto(category);
            apiResponse.setMessage(messageSource.getMessage("success.soft.delete", null, LocaleContextHolder.getLocale()));
            apiResponse.setResult(result);
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


        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        categoryRepository.deleteById(id);

        apiResponse.setMessage(messageSource.getMessage("success.operation", null, LocaleContextHolder.getLocale()));
        apiResponse.setResult(true);
        return apiResponse;
    }
}
