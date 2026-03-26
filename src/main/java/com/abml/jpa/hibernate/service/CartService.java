
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
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;    
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
    // 1. Buscamos si el usuario ya tiene este producto específico en su carrito
    
    Optional<CartItem> itemExistente = cartRepo.findByUserAndProductId(user, productId);

    if (itemExistente.isPresent()) {
        // 2. SI YA EXISTE: Solo aumentamos la cantidad
        CartItem item = itemExistente.get();
        item.setQuantity(item.getQuantity() + quantity);
        item.setAddedAt(LocalDateTime.now()); // Actualizamos la fecha si quieres
        cartRepo.save(item);
        System.out.println("✅ Cantidad actualizada: " + item.getProduct().getName());
    } else {
        // 3. SI NO EXISTE: Recién ahí creamos el objeto nuevo (tu código original)
        Product product = productRepo.findById(productId)
          .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setAddedAt(LocalDateTime.now());
        cartRepo.save(item);
        System.out.println("🆕 Producto nuevo agregado al carrito");
    }
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
        

          
            // Registrar la orden en la base de datos
            Orders order = new Orders();
            order.setUser(user);
            order.setLoginUsername(user.getUsername());
            order.setLoginEmail(user.getEmail());
            order.setMpPayerName(name);
            order.setMpPayerEmail(email);
            
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
// 1. Primero creamos los OrderItems (donde ya buscas en la DB)
List<OrderItems> orderItems = items.stream().map(i -> {
    Product product = productRepo.findById(i.getProductId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + i.getProductId()));

    OrderItems oi = new OrderItems();
    oi.setOrder(order);
    oi.setProduct(product);
    oi.setQuantity(i.getQuantity());
    oi.setPrice(product.getPrice());
    oi.setAmount(product.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())));
    oi.setProductName(product.getName());
    return oi;
}).collect(Collectors.toList());

order.setItems(orderItems);


           
  
  
  // Calcular total
            BigDecimal total = orderItems.stream()
                    .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(shippingCost != null ? shippingCost : BigDecimal.ZERO);

            order.setTotal(total);
            order.setAmount(total);

            // Guardar orden con ítems (cascade = ALL se encarga de persistirlos)
  // y también para obtener su ID
        orderRepository.save(order);

  //  CREAR LOS ITEMS PARA MERCADO PAGO (Usando los datos reales de orderItems)
        List<PreferenceItemRequest> mpItems = orderItems.stream()
            .map(oi -> PreferenceItemRequest.builder()
                    .title(oi.getProductName())
                    .quantity(oi.getQuantity())
                    .unitPrice(oi.getPrice()) // AQUÍ ESTABA EL ERROR: Ahora usa el precio de la DB
                    .currencyId("ARS")
                    .build())
            .collect(Collectors.toList());

        // Sumar envío a Mercado Pago
        if (shippingCost != null && shippingCost.compareTo(BigDecimal.ZERO) > 0) {
            mpItems.add(PreferenceItemRequest.builder()
                    .title("Envío: " + shippingName)
                    .quantity(1)
                    .unitPrice(shippingCost)
                    .currencyId("ARS")
                    .build());
              }

                        // 1. Crear el objeto de Back URLs por separado
PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
    .success("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .failure("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .pending("https://4200-cs-582739288523-default.cs-us-east1-pkhd.cloudshell.dev/estado-compra")
    .build();

  
  // Crear preferencia con externalReference = ID de la orden
        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(mpItems)
                .payer(PreferencePayerRequest.builder()
                        .name(name)
                        .email(email)
                       .build())
                .externalReference(order.getId().toString()) // 🔹 clave
              .notificationUrl("https://portfoliowebbackendkoyeb-1-ulka.onrender.com/api/payments/webhook")
                .backUrls(backUrls) // <-- Aquí pasas el objeto ya construido
    .autoReturn("all")
          .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);




        // Actualizar la orden con el preferenceId y externalReference
        order.setPreferenceId(preference.getId());
        order.setExternalReference(preference.getExternalReference());
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
