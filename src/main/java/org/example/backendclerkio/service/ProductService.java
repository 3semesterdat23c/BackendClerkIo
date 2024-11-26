package org.example.backendclerkio.service;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.example.backendclerkio.dto.ProductDTO;
import org.example.backendclerkio.dto.ProductsResponseDTO;
import org.example.backendclerkio.entity.Product;
import org.example.backendclerkio.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {
    private final WebClient webClient;

    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, WebClient.Builder webClient) {
        this.webClient = webClient.build();
        this.productRepository = productRepository;
    }

    /*public Mono<ProductDTO> getProductsFromIkea() {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://ikeaapi.p.rapidapi.com/keywordSearch?keyword=chair&countryCode=us&languageCode=en")
                .defaultHeader("x-rapidapi-key", "a5b84b7c39mshdae1688aabf7a42p196219jsnd2a0bf583d6a")
                .defaultHeader("x-rapidapi-host", "ikeaapi.p.rapidapi.com")
                .build();

        return webClient.get()
                .retrieve()
                .bodyToMono(ProductDTO.class);
    }
*/

    public Mono<ProductsResponseDTO> getProductsFromDummy() {
        return webClient.get()
                .uri("https://dummyjson.com/products")
                .retrieve()
                .bodyToMono(ProductsResponseDTO.class);
    }

}
