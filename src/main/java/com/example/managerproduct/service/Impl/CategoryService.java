package com.example.managerproduct.service.Impl;

import com.example.managerproduct.dto.request.CreateCategoryDto;
import com.example.managerproduct.dto.request.UpdateCategoryDto;
import com.example.managerproduct.dto.response.ApiResponse;
import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.entity.Category;
import com.example.managerproduct.entity.ImageCategory;
import com.example.managerproduct.exception.AppException;
import com.example.managerproduct.exception.ErrorCode;
import com.example.managerproduct.mapper.request.CreateCategoryMapper;
import com.example.managerproduct.mapper.request.ImageCategoryMapper;
import com.example.managerproduct.mapper.request.UpdateCategoryMapper;
import com.example.managerproduct.mapper.response.CategoryMapper;
import com.example.managerproduct.repository.CategoryRepository;
import com.example.managerproduct.repository.ImageCategoryRepository;
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

    @Override
    public ApiResponse<Page<CategoryDto>> getAllCategory(String name, String categoryCode, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        ApiResponse<Page<CategoryDto>> apiResponse = new ApiResponse<>();
        Page<Category> category = categoryRepository.getAll(name, categoryCode, startDate, endDate, pageable);
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

        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        Category category = categoryRepository.getById(id);

        CategoryDto result = categoryMapper.toDto(category);

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
        category.setStatus("AVAILABLE");
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
        category.setModifiedDate(new Date());
        category.setModifiedBy(modifiedBy);

        // Lưu các hình ảnh mới
        List<ImageCategory> existingImages = imageCategoryRepository.findByCategoryId(category.getId());
        Set<String> newImagePaths = new HashSet<>();
        List<ImageCategory> newImageCategories = new ArrayList<>();

        if (images == null || images.length == 0) {
            // Xóa hết các hình ảnh cũ nếu không có hình ảnh mới
            if (!existingImages.isEmpty()) {
                existingImages.forEach(img -> deleteImageFromFileSystem(img.getImagePath())); // Xóa ảnh từ hệ thống tệp
                imageCategoryRepository.deleteAll(existingImages);
            }
        } else {
            // Xử lý các hình ảnh mới
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    String imageName = saveImageToFileSystem(file); // Lưu ảnh và lấy tên tệp duy nhất
                    String imagePath = IMAGE_DIRECTORY + "\\" + imageName; // Đường dẫn ảnh nếu cần thiết
                    newImagePaths.add(imagePath);

                    ImageCategory imageCategory = existingImages.stream()
                            .filter(img -> img.getImageName().equals(imageName))
                            .findFirst()
                            .orElse(null);

                    if (imageCategory == null) {
                        imageCategory = new ImageCategory();
                        imageCategory.setCreatedDate(new Date());
                        imageCategory.setCreatedBy(modifiedBy);
                        imageCategory.setCategory(category);
                    }

                    imageCategory.setImageName(imageName); // Lưu tên hình ảnh duy nhất
                    imageCategory.setImagePath(imagePath);
                    imageCategory.setStatus("AVAILABLE");
                    imageCategory.setModifiedDate(new Date());
                    imageCategory.setModifiedBy(modifiedBy);

                    newImageCategories.add(imageCategory);
                }
            }

            // Lưu tất cả các hình ảnh mới vào cơ sở dữ liệu
            imageCategoryRepository.saveAll(newImageCategories);

            // Xóa hình ảnh cũ không còn được sử dụng
            List<ImageCategory> imagesToDelete = existingImages.stream()
                    .filter(img -> !newImagePaths.contains(img.getImagePath()))
                    .collect(Collectors.toList());

            if (!imagesToDelete.isEmpty()) {
                imagesToDelete.forEach(img -> deleteImageFromFileSystem(img.getImagePath())); // Xóa ảnh từ hệ thống tệp

                category.getImageCategories().clear();
                category.getImageCategories().addAll(newImageCategories);
            }
        }

        CategoryDto result = categoryMapper.toDto(category);
        result.setImageCategories(imageCategoryMapper.DTO_LIST(newImageCategories));

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
