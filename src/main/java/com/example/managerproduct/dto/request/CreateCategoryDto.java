package com.example.managerproduct.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryDto {
    @NotBlank(message = "error.notBlank")
    @Size(max = 50, min = 2, message = "error.invalidInput")
    private String name;

    private String description;

    @NotBlank(message = "error.notBlank")
    @Size(max = 50, min = 2, message = "error.invalidInput")
    private String category_code;

    @NotNull(message = "error.notBlank")
    @Pattern(regexp = "1|0", message = "error.statusInput")
    private String status;
}
