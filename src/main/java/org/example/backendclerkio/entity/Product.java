package org.example.backendclerkio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name", nullable = false)
    private String title;

    @Column(name = "product_price", nullable = false)
    private float price;

    @Column(name = "product_description")
    private String description;

    @Column(name = "stock_count", nullable = false)
    private int stockCount;

    // Change from single String to List<String>
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images;

    @Column(name = "discount")
    private float discount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToMany
    @JoinTable(
            name = "product_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    // Convenience constructor
    public Product(String title, String description, float price, int stock, Category category, List<String> images, float discount) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.discount = discount;
        this.category = category;
        this.images = images;
    }

    public Product(String title, String description, float price, int stock, String category, List<String> images, float discount) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.images = images;
        this.discount = discount;
    }

    // Constructor
    public Product(String title, String description, float price, int stock,
                   Category category, List<String> images, Set<Tag> tags, float discount) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stockCount = stock;
        this.category = category;
        this.images = images;
        this.tags = tags;
        this.discount = discount;
    }
}
