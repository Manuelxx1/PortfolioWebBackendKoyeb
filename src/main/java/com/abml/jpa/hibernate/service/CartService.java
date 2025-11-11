@Service
public class CartService {

  @Autowired private CartItemRepository cartRepo;
  @Autowired private ProductRepository productRepo;

  public List<CartItem> getCart(User user) {
    return cartRepo.findByUser(user);
  }

  public void addToCart(User user, Long productId, int quantity) {
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    CartItem item = new CartItem();
    item.setUser(user);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setAddedAt(LocalDateTime.now());

    cartRepo.save(item);
  }

  public void removeFromCart(User user, Long cartItemId) {
    CartItem item = cartRepo.findById(cartItemId)
      .orElseThrow(() -> new RuntimeException("Item no encontrado"));

    if (!item.getUser().getId().equals(user.getId())) {
      throw new RuntimeException("No autorizado");
    }

    cartRepo.delete(item);
  }
}
