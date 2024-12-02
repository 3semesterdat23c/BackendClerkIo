package org.example.backendclerkio.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.backendclerkio.entity.Category;

import java.util.List;

public record ProductResponseDTO(
        @JsonProperty("id") int id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("price") float price,
        @JsonProperty("stock") int stock,
        @JsonProperty("category") Category category,
        @JsonProperty("tags") List<String> tags,
        @JsonProperty("discountPercentage") float discountPercentage,
        @JsonProperty("images") List<String> images
) {}
