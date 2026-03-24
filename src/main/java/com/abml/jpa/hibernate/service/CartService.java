
package com.abml.jpa.hibernate.service;
import com.abml.jpa.hibernate.repository.ProductRepository;
import com.abml.jpa.hibernate.repository.CartItemRepository;
import com.abml.jpa.hibernate.repository.UserRepository;
import com.abml.jpa.hibernate.repository.OrderRepository;
import com.abml.jpa.hibernate.repository.OrderItemsRepository;
import com.abml.jpa.hibernate.model.CartItem;
import com.abml.jpa.hibernate.model.Users;
import com.abml.jpa.hibernate.model.Product;
import com.abml.jpa.hibernate.model.Orders;
import com.abml.jpa.hibernate.model.OrderItems;
import com.abml.jpa.hibernate.dto.CartItemDto;
//Notifications mediante websocket stomp
import org.springframework.messaging.simp.SimpMessagingTemplate;
//Enviar mensajes programados
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.resources.preference.Preference;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.exceptions.MPApiException;


import java.util.stream.Collectors;


@Service
public class CartService {

  @Autowired private CartItemRepository cartRepo;
  @Autowired private ProductRepository productRepo;
  @Autowired
private OrderRepository orderRepository;
    @Autowired
private UserRepository userRepository;

  public List<CartItem> getCart(Users user) {
    return cartRepo.findByUser(user);
}


  //metod que agrega el producto que nos retorno
  //la búsqueda en el frontend 
  //se reciben los datos del método addToCart
  //del controller provenientes del frontend 
  //y se le asignan a este metodo 
  public void addToCart(Users user, Long productId, int quantity) {
    //se busca y se guarda el producto por id 
    Product product = productRepo.findById(productId)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    //se envia los datos que deseamos
    //a los setters del model CartItem 
    CartItem item = new CartItem();
    item.setUser(user);
    item.setProduct(product);
    item.setQuantity(quantity);
    item.setAddedAt(LocalDateTime.now());
//se hace la persistencia a la tabla cart_items
    cartRepo.save(item);
  }

  //para comprar con el checkout de MP
public String comprarCarrito(Users user,
                                 List<CartItemDto> items,
                                 String name,
                                 String email,
                                 String phone,
                                 String address,
                                 String city,
                                 String postalCode,
                                 String shippingType,
                                 BigDecimal shippingCost,
                                 String shippingName) {

try {

  
        // Configurar credenciales de Mercado Pago
String accessToken = System.getenv("MERCADOPAGO_ACCESS_TOKEN");
MercadoPagoConfig.setAccessToken(accessToken);
        // Convertir los items del carrito a items de Mercado Pago
        List<PreferenceItemRequest> mpItems = items.stream()
        .map(i -> PreferenceItemRequest.builder()
                .title(getProductName(i.getProductId()))
                .quantity(i.getQuantity())
                .unitPrice(getProductPrice(i.getProductId())) // aquí va directo
                .currencyId("ARS")
                .build())
        .collect(Collectors.toList());


        // Agregar costo de envío como ítem extra si corresponde
        if (shippingCost != null && shippingCost.compareTo(BigDecimal.ZERO) > 0) {
            mpItems.add(PreferenceItemRequest.builder()
                    .title("Envío: " + shippingName)
                    .quantity(1)
                    .unitPrice(shippingCost)
                    .currencyId("ARS")
                    .build());
        }

        // Crear preferencia con datos del comprador
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(mpItems)
                .payer(PreferencePayerRequest.builder()
                        .name(name)
                        .email(email)
                        .build())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

            // Registrar la orden en la base de datos
            Orders order = new Orders();
            order.setUser(user);
            order.setLoginUsername(user.getUsername());
            order.setLoginEmail(user.getEmail());
            order.setMpPayerName(name);
            order.setMpPayerEmail(email);
            order.setPreferenceId(preference.getId());
            order.setExternalReference(preference.getExternalReference());
            order.setStatus("pendiente");
            order.setShippingType(shippingType);
            order.setShippingCost(shippingCost != null ? shippingCost.doubleValue() : 0.0);
            order.setShippingName(shippingName);
            order.setName(name);
            order.setEmail(email);
            order.setPhone(phone);
            order.setAddress(address);
            order.setCity(city);
            order.setPostalCode(postalCode);

            // Convertir items del carrito a OrderItems
List<OrderItems> orderItems = items.stream().map(i -> {
    // Buscar el producto en la base de datos
    Product product = productRepository.findById(i.getProductId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + i.getProductId()));

    OrderItems oi = new OrderItems();
    oi.setOrder(order); // vincular con la orden
    oi.setProduct(product); // vincular con el producto
    oi.setQuantity(i.getQuantity());

    // Precio unitario del producto
    oi.setPrice(product.getPrice());

    // Calcular monto total del ítem (precio × cantidad)
    oi.setAmount(product.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));

    // Guardar nombre del producto como texto plano
    oi.setProductName(product.getName());

    return oi;
}).collect(Collectors.toList());


            // Calcular total
            BigDecimal total = orderItems.stream()
                    .map(oi -> oi.getUnitPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(shippingCost != null ? shippingCost : BigDecimal.ZERO);

            order.setTotal(total);
            order.setAmount(total);

            orderRepository.save(order);

            // Devolver la URL de checkout de MP 
            return preference.getInitPoint();
  
  /*
  se captura la excepción del SDK de MP (MPException) 
  y la convierte en RuntimeException.
  */
    
  } catch (MPException | MPApiException e) {
        // Manejo de error: loguear y devolver algo apropiado
        throw new RuntimeException("Error al crear preferencia en Mercado Pago", e);
}
}

    private String getProductName(Long productId) {
        // Recuperar nombre del producto desde DB
        return "Producto " + productId;
    }

    private BigDecimal getProductPrice(Long productId) {
    // Recuperar precio del producto desde DB
    return BigDecimal.valueOf(100.0); // ejemplo
}

  
  // /increase → botón +
    public void increaseFromCart(Users user, Long productId) {
        CartItem item = cartRepo.findByUser(user).stream()
            .filter(i -> i.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        item.setQuantity(item.getQuantity() + 1);
        cartRepo.save(item);
    }

    // /decrease → botón −
public void decreaseFromCart(Users user, Long productId) {
    CartItem item = cartRepo.findByUser(user).stream()
        .filter(i -> i.getProduct().getId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Item no encontrado"));

    if (item.getQuantity() > 1) {
        item.setQuantity(item.getQuantity() - 1);
        cartRepo.save(item);
    } else {
        // si llega a 0, lo eliminamos del carrito
        cartRepo.delete(item);
    }
}

    // /remove/{id}
    public void removeFromCart(Users user, Long cartItemId) {
        CartItem item = cartRepo.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Item no encontrado"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No autorizado");
        }

        cartRepo.delete(item);
    }

    // /clear
    public void clearCart(Users user) {
        List<CartItem> items = cartRepo.findByUser(user);
        cartRepo.deleteAll(items);
    }


  @Autowired private SimpMessagingTemplate template;
  // cuando haya pasado 24 sin actividad en el carrito 
//Se ejecuta el Scheduled cada hora 
  //para recordarle al usuario que tiene productos en su carrito
@Scheduled(fixedRate = 3600000) 
  //se ejecuta cada 10 segundos para probar
 // @Scheduled(fixedRate = 10000) 
  //se ejecuta cada 1 minuto para probar
  //@Scheduled(fixedRate = 60000)
  public void enviarRecordatorios() { 
    // Carritos con items agregados hace más de 24 horas
   LocalDateTime limite = LocalDateTime.now().minusHours(24);
   //LocalDateTime limite = LocalDateTime.now().minusMinutes(1);
    //pruebas inmediatas
    //LocalDateTime limite = LocalDateTime.now().minusSeconds(30);

    List<CartItem> abandonados = cartRepo.findByAddedAtBefore(limite); 
    for (CartItem item : abandonados) { 
      //item.getProduct().getName() llamado desde CartItem por relación entre entidad  CartItem  y product     
      String mensaje = "⏰ Recordatorio: aún tienes " + item.getQuantity() + " unidad(es) de '" + item.getProduct().getName() + "' en tu carrito";
      template.convertAndSend("/topic/notificaciones", mensaje); 
    }
  }

}
