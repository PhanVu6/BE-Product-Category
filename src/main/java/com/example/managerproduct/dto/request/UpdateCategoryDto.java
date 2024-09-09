package com.example.managerproduct.dto.request;

import com.example.managerproduct.entity.Category;
import com.example.managerproduct.repository.common.ExistsInDatabase;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCategoryDto {
    @NotNull(message = "error.notBlank")
    @ExistsInDatabase(entity = Category.class, message = "error.notFound")
    private Long id;

    @NotBlank(message = "error.notBlank")
    @Size(max = 50, min = 2, message = "error.invalidInput")
    private String name;

    private String description;

    @NotBlank(message = "error.notBlank")
    @Size(max = 10, message = "error.invalidInput")
    private String categoryCode;

    private String imageLink;

    @NotNull(message = "error.notBlank")
    @Pattern(regexp = "AVAILABLE|UNAVAILABLE", message = "error.statusInput")
    private String status;

    private List<Long> imageIds;

    private List<MultipartFile> imageProducts;
}
