package org.example.backendclerkio.config;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.TagRepository;
import org.example.backendclerkio.service.ProductService;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class InitData {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ProductService productService;

    public InitData(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.productService = productService;
    }

    @Transactional
    @PostConstruct
    public void init() {
        if (productRepository.count() == 0) {
            ProductsResponseDTO response = productService.getAllProducts().block();
            if (response != null && response.products() != null) {
                List<Product> products = response.products().stream()
                        .map(this::mapToEntity)
                        .collect(Collectors.toList());

                productRepository.saveAll(products);
            } else {
                System.err.println("No products received from the API.");
            }
        }
    }

    private Product mapToEntity(ProductResponseDTO dto) {
        // Find or create the category
        Category category = categoryRepository.findByCategoryName(dto.category().getCategoryName())
                .orElseGet(() -> categoryRepository.save(new Category(dto.category().getCategoryName(), null)));

        // Map tags
        Set<Tag> tags = dto.tags().stream()
                .map(tagName -> {
                    System.out.println("Processing tag: " + tagName); // Debugging log
                    return tagRepository.findByTagName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = new Tag(0, tagName, null);
                                tagRepository.save(newTag);
                                return newTag;
                            });
                })
                .collect(Collectors.toSet());

        // Create the product with category and tags
        return new Product(
                dto.title(),
                dto.description(),
                dto.price(),
                dto.stock(),
                dto.category(),  // Associate the found/created category
                dto.images(),
                tags,  // Associate the found/created tags
                dto.discountPercentage()
        );
    }
}
