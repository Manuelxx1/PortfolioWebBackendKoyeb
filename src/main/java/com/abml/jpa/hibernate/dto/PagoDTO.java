package com.abml.jpa.hibernate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public class PagoDTO {
    private Long id;

    @JsonProperty("user")
    private UserDTO user;

    // Datos del payer de Mercado Pago
    @JsonProperty("mp_payer_name")
    private String mpPayerName;

    @JsonProperty("mp_payer_email")
    private String mpPayerEmail;

    @JsonProperty("preference_id")
    private String preferenceId;

    @JsonProperty("external_reference")
    private String externalReference;

    @JsonProperty("items")
    private List<ItemDTO> items;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }

    public String getMpPayerName() { return mpPayerName; }
    public void setMpPayerName(String mpPayerName) { this.mpPayerName = mpPayerName; }

    public String getMpPayerEmail() { return mpPayerEmail; }
    public void setMpPayerEmail(String mpPayerEmail) { this.mpPayerEmail = mpPayerEmail; }

    public String getPreferenceId() { return preferenceId; }
    public void setPreferenceId(String preferenceId) { this.preferenceId = preferenceId; }

    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }

    public List<ItemDTO> getItems() { return items; }
    public void setItems(List<ItemDTO> items) { this.items = items; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ------------------ UserDTO ------------------
    public static class UserDTO {
        private Long id;
        private String username;
        private String email;

        @JsonProperty("mp_user_id")
        private Long mpUserId;

        private String name;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public Long getMpUserId() { return mpUserId; }
        public void setMpUserId(Long mpUserId) { this.mpUserId = mpUserId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    // ------------------ ItemDTO ------------------
    public static class ItemDTO {
        private Long id;
        private ProductDTO product;
        private Integer quantity;

        private BigDecimal price;
        private BigDecimal amount;

        @JsonProperty("product_name")
        private String productName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public ProductDTO getProduct() { return product; }
        public void setProduct(ProductDTO product) { this.product = product; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
    }

    // ------------------ ProductDTO ------------------
    public static class ProductDTO {
        private Long id;
        private String name;
        private String description;

        private BigDecimal price;
        private Integer stock;

        @JsonProperty("image_url")
        private String imageUrl;

        private String category;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
