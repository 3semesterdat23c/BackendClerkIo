package org.example.backendclerkio.service;

import org.example.backendclerkio.dto.ProductResponseDTO;
import org.example.backendclerkio.dto.ProductRequestDTO;
import org.example.backendclerkio.dto.ProductsRequestDTO;
import org.example.backendclerkio.entity.Category;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.entity.Tag;
import org.example.backendclerkio.repository.CategoryRepository;
import org.example.backendclerkio.repository.ProductRepository;
import org.example.backendclerkio.repository.TagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class ProductService {
    private final WebClient webClient;

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private TagRepository tagRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, TagRepository tagRepository, WebClient.Builder webClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.webClient = webClient.build();
    }

    public List<Product> getProductList(){
        return productRepository.findAll();
    }


    public Mono<ProductsRequestDTO> getProductsFromDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?limit=100")
                .retrieve()
                .bodyToMono(ProductsRequestDTO.class);
    }

    public Mono<ProductsRequestDTO> getProductsFromAnotherDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products?skip=100&limit=200")
                .retrieve()
                .bodyToMono(ProductsRequestDTO.class);

    }

    public Mono<ProductsRequestDTO> getAllProducts() {
        Mono<ProductsRequestDTO> firstBatchMono = getProductsFromDummy();
        Mono<ProductsRequestDTO> secondBatchMono = getProductsFromAnotherDummy();

        return Mono.zip(firstBatchMono, secondBatchMono)
                .map(tuple -> {
                    ProductsRequestDTO firstBatch = tuple.getT1();
                    ProductsRequestDTO secondBatch = tuple.getT2();

                    // Combine the product lists
                    List<ProductRequestDTO> combinedProducts = new ArrayList<>();
                    combinedProducts.addAll(firstBatch.products());
                    combinedProducts.addAll(secondBatch.products());

                    // Create a new ProductsResponseDTO with combined products
                    ProductsRequestDTO combinedResponse = new ProductsRequestDTO(
                            combinedProducts,
                            firstBatch.total() + secondBatch.total(), // Adjust total if necessary
                            firstBatch.skip(), // Adjust skip and limit as appropriate
                            firstBatch.limit() + secondBatch.limit()
                    );

                    return combinedResponse;
                });
    }

    public Page<Product> findAll(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Page<Product> findAllByCategory(Pageable pageable, int categoryID){
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(categoryID);
        Category category = new Category();
        if (categoryOptional.isPresent()){
        return productRepository.findAllByCategory(pageable, categoryOptional.get());}
     return null;}



    public Page<Product> findFilteredProducts(Pageable pageable, boolean lowStock, boolean outOfStock) {
        if (lowStock && outOfStock) {
            // Return products with stock count between 0 and 5 (inclusive)
            return productRepository.findByStockCountBetween(0, 5, pageable);
        } else if (lowStock) {
            // Return products with stock count between 1 and 5 (inclusive)
            return productRepository.findByStockCountBetween(1, 5, pageable);
        } else if (outOfStock) {
            // Return products with stock count of 0
            return productRepository.findByStockCount(0, pageable);
        }
        // Return all products if no filters are applied
        return productRepository.findAll(pageable);
    }

    public Product createProduct(ProductRequestDTO productRequestDTO) {
        // Get the category name from the DTO
        String categoryName = productRequestDTO.category();

        // Check if the category exists, or create it if it doesn't
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Create a set of tags
        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            // Check if the tag already exists, if not create and save it
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            // Add the tag to the set
            tags.add(tag);
        }




        // Create the product with the category and tags
        Product product = new Product(
                productRequestDTO.title(),
                productRequestDTO.description(),
                productRequestDTO.price(),
                productRequestDTO.discountPrice(),
                productRequestDTO.stockCount(),
                category,
                productRequestDTO.images(),
                tags
        );

        // Save the product to the repository
        productRepository.save(product);

        return product;
    }

    public void deleteProduct(int id){
    if (!productRepository.existsById(id)) {
        throw new IllegalArgumentException("Product not found");
    }
        productRepository.deleteById(id);

    }

    public Product updateStock(int id, int quantityToAdd) {
        Product productToUpdate = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + id));

        if (quantityToAdd < 0) {
            throw new IllegalArgumentException("Quantity to add must be non-negative");
        }

        int currentStock = productToUpdate.getStockCount();

        int updatedStock = currentStock + quantityToAdd;


        productToUpdate.setStockCount(updatedStock);

        return productRepository.save(productToUpdate);
    }

    public Product updateProduct(int id, ProductRequestDTO productRequestDTO) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Booking not found");
        }

        String categoryName = productRequestDTO.category();

        // Check if the category exists, or create it if it doesn't
        Category category = categoryRepository.findByCategoryName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

        // Handle tags
        Set<Tag> tags = new HashSet<>();
        for (String tagName : productRequestDTO.tags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));
            tags.add(tag);
        }

        // Set the properties for the product
        existingProduct.setTitle(productRequestDTO.title());
        existingProduct.setDescription(productRequestDTO.description());
        existingProduct.setPrice(productRequestDTO.price());
        existingProduct.setDiscountPrice(productRequestDTO.discountPrice());
        existingProduct.setStockCount(productRequestDTO.stockCount());
        existingProduct.setCategory(category);  // Single category set here
        existingProduct.setImages(productRequestDTO.images());
        existingProduct.setTags(tags);  // Set the tags for the product

        return productRepository.save(existingProduct);
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public Page<Product> findByCategory(String category, Pageable pageable) {
        return productRepository.findProductsByCategory_CategoryName(category, pageable);
    }
    public Page<Product> searchProductsByName(String title, Pageable pageable) {
        return productRepository.findByTitleContainingIgnoreCase(title, pageable);
    }
}



