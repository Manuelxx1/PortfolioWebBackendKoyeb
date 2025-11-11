@Entity
@Table(name = "products")
public class Product {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private BigDecimal price;
  private Integer stock;

  @Column(name = "image_url")
  private String imageUrl;

  private String category;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
