package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", nullable = false)
    private int categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @ManyToMany
    @JoinTable(
            name = "category_product", // Join table name
            joinColumns = @JoinColumn(name = "category_id"), // Foreign key for Category
            inverseJoinColumns = @JoinColumn(name = "product_id") // Foreign key for Product
    )
    private Set<Product> products = new HashSet<>();

    public Category(String categoryName, Set<Product> products) {
        this.categoryName = categoryName;
        this.products = products;
    }
}

