package org.example.backendclerkio;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
public record ProductsResponseDTO(
        @JsonProperty("products") List<ProductDTO> products,
        @JsonProperty("total") int total,
        @JsonProperty("skip") int skip,
        @JsonProperty("limit") int limit
) {}
