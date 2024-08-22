package com.example.managerproduct.dto.request;

import com.example.managerproduct.dto.response.CategoryDto;
import com.example.managerproduct.entity.Product;
import com.example.managerproduct.repository.common.ExistsInDatabase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDto {
    @NotNull(message = "error.notBlank")
    @ExistsInDatabase(entity = Product.class, message = "error.notFound")
    private Long id;

    @NotBlank(message = "error.notBlank")
    @Size(max = 50, min = 2, message = "error.invalidInput")
    private String name;

    private String description;

    @NotNull(message = "error.notBlank")
    private Double price;

    @NotBlank(message = "error.notBlank")
    @Size(max = 20, message = "error.invalidInput")
    private String product_code;

    @NotNull(message = "error.notBlank")
    private Long quantity;

    private String imageLink;

    @NotNull(message = "error.notBlank")
    @Pattern(regexp = "1|0", message = "error.statusInput")
    private String status;

    private List<Long> categoryIds;

    private List<@Valid CategoryDto> categories;
}
