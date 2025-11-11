package com.abml.jpa.hibernate.model;

@Entity
@Table(name = "cart_items")
public class CartItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  private Integer quantity;

  @Column(name = "added_at")
  private LocalDateTime addedAt;
}
