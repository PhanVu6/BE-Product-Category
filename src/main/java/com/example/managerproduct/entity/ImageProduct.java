package com.example.managerproduct.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "image_product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] data;

//    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
//    @JoinColumn(name = "product_id", nullable = false)
//    private Product product;
}
