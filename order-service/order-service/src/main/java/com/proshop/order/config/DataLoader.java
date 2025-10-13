package com.proshop.order.config;

import com.proshop.order.entity.*;
import com.proshop.order.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository, // ← THÊM
            PaymentRepository paymentRepository) {

        return args -> {
            // ✅ Kiểm tra nếu database đã có dữ liệu thì không chạy
            if (cartRepository.count() > 0 || orderRepository.count() > 0) {
                System.out.println("⏭️ Database already has data. Skipping initialization...");
                return;
            }

            System.out.println("🚀 Starting database initialization...");

            // ✅ User sample
            long user1 = 1;
            long user2 = 2;

            // ✅ Product UUIDs giả lập (lấy từ ProductService)
            UUID laptop1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID laptop2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
            UUID cpu1    = UUID.fromString("33333333-3333-3333-3333-333333333333");
            UUID gpu1    = UUID.fromString("44444444-4444-4444-4444-444444444444");
            UUID ram1    = UUID.fromString("55555555-5555-5555-5555-555555555555");
            UUID ssd1    = UUID.fromString("66666666-6666-6666-6666-666666666666");

            // =======================
            // 1️⃣ CartService Data
            // =======================
            CartEntity cart1 = new CartEntity();
            cart1.setUserId(user1);
            cart1.setCreatedAt(LocalDateTime.now());
            cart1.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart1);

            CartEntity cart2 = new CartEntity();
            cart2.setUserId(user2);
            cart2.setCreatedAt(LocalDateTime.now());
            cart2.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart2);

            // CartItems
            CartItemEntity ci1 = new CartItemEntity();
            ci1.setCart(cart1);
            ci1.setProductId(laptop1);
            ci1.setQuantity(1);
            ci1.setCreatedAt(LocalDateTime.now());
            ci1.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(ci1);

            CartItemEntity ci2 = new CartItemEntity();
            ci2.setCart(cart1);
            ci2.setProductId(ram1);
            ci2.setQuantity(2);
            ci2.setCreatedAt(LocalDateTime.now());
            ci2.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(ci2);

            CartItemEntity ci3 = new CartItemEntity();
            ci3.setCart(cart2);
            ci3.setProductId(cpu1);
            ci3.setQuantity(1);
            ci3.setCreatedAt(LocalDateTime.now());
            ci3.setUpdatedAt(LocalDateTime.now());
            cartItemRepository.save(ci3);

            System.out.println("✅ Created 2 carts with 3 cart items");

            // =======================
            // 2️⃣ OrderService Data
            // =======================

            // Order 1: User1 mua Laptop + GPU
            OrderEntity order1 = OrderEntity.builder()
                    .userId(user1)
                    .totalAmount(new BigDecimal("25000000")) // 20M laptop + 5M GPU
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order1);

            // Order Items cho Order 1
            OrderItemEntity oi1 = OrderItemEntity.builder()
                    .order(order1)
                    .productId(laptop1)
                    .quantity(1)
                    .price(new BigDecimal("20000000"))
                    .subtotal(new BigDecimal("20000000")) // 1 * 20M
                    .createdAt(LocalDateTime.now())
                    .build();
            orderItemRepository.save(oi1);

            OrderItemEntity oi2 = OrderItemEntity.builder()
                    .order(order1)
                    .productId(gpu1)
                    .quantity(1)
                    .price(new BigDecimal("5000000"))
                    .subtotal(new BigDecimal("5000000")) // 1 * 5M
                    .createdAt(LocalDateTime.now())
                    .build();
            orderItemRepository.save(oi2);

            System.out.println("✅ Created Order 1 with 2 items (Laptop + GPU)");

            // Order 2: User2 mua CPU + RAM
            OrderEntity order2 = OrderEntity.builder()
                    .userId(user2)
                    .totalAmount(new BigDecimal("9000000")) // 6M CPU + 3M RAM
                    .status(OrderStatus.COMPLETED)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();
            orderRepository.save(order2);

            // Order Items cho Order 2
            OrderItemEntity oi3 = OrderItemEntity.builder()
                    .order(order2)
                    .productId(cpu1)
                    .quantity(1)
                    .price(new BigDecimal("6000000"))
                    .subtotal(new BigDecimal("6000000")) // 1 * 6M
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();
            orderItemRepository.save(oi3);

            OrderItemEntity oi4 = OrderItemEntity.builder()
                    .order(order2)
                    .productId(ram1)
                    .quantity(2)
                    .price(new BigDecimal("1500000"))
                    .subtotal(new BigDecimal("3000000")) // 2 * 1.5M
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();
            orderItemRepository.save(oi4);

            System.out.println("✅ Created Order 2 with 2 items (CPU + 2x RAM)");

            // Order 3: User1 mua nhiều sản phẩm
            OrderEntity order3 = OrderEntity.builder()
                    .userId(user1)
                    .totalAmount(new BigDecimal("35500000"))
                    .status(OrderStatus.COMPLETED)
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();
            orderRepository.save(order3);

            OrderItemEntity oi5 = OrderItemEntity.builder()
                    .order(order3)
                    .productId(laptop2)
                    .quantity(1)
                    .price(new BigDecimal("25000000"))
                    .subtotal(new BigDecimal("25000000"))
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();
            orderItemRepository.save(oi5);

            OrderItemEntity oi6 = OrderItemEntity.builder()
                    .order(order3)
                    .productId(ssd1)
                    .quantity(3)
                    .price(new BigDecimal("3500000"))
                    .subtotal(new BigDecimal("10500000")) // 3 * 3.5M
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();
            orderItemRepository.save(oi6);

            System.out.println("✅ Created Order 3 with 2 items (Laptop2 + 3x SSD)");

            // =======================
            // 3️⃣ PaymentService Data
            // =======================
            PaymentEntity payment1 = PaymentEntity.builder()
                    .order(order1)
                    .amount(new BigDecimal("25000000"))
                    .method(PaymentMethod.CREDIT_CARD)
                    .status(PaymentStatus.PENDING)
                    .paidAt(null)
                    .build();
            paymentRepository.save(payment1);

            PaymentEntity payment2 = PaymentEntity.builder()
                    .order(order2)
                    .amount(new BigDecimal("9000000"))
                    .method(PaymentMethod.CASH)
                    .status(PaymentStatus.PAID)
                    .paidAt(LocalDateTime.now().minusDays(1))
                    .build();
            paymentRepository.save(payment2);

            PaymentEntity payment3 = PaymentEntity.builder()
                    .order(order3)
                    .amount(new BigDecimal("35500000"))
                    .method(PaymentMethod.BANK_TRANSFER)
                    .status(PaymentStatus.PAID)
                    .paidAt(LocalDateTime.now().minusDays(3))
                    .build();
            paymentRepository.save(payment3);

            System.out.println("✅ Created 3 payments");

            System.out.println("=" .repeat(60));
            System.out.println("✅ Database initialization completed!");
            System.out.println("📊 Summary:");
            System.out.println("   - 2 Carts with 3 Cart Items");
            System.out.println("   - 3 Orders with 6 Order Items");
            System.out.println("   - 3 Payments");
            System.out.println("=" .repeat(60));
        };
    }
}