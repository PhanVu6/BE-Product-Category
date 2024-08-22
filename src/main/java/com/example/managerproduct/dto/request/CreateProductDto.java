package com.example.managerproduct.dto.request;

import com.example.managerproduct.dto.response.CategoryDto;
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
public class CreateProductDto {
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

    @NotBlank(message = "error.notBlank")
    @Pattern(regexp = "1|0", message = "error.statusInput")
    private String status;

    private List<@Valid CategoryDto> categories;
}
