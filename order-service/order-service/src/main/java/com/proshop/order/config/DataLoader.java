package com.proshop.order.config;

import com.proshop.order.entity.*;
import com.proshop.order.repository.CartItemRepository;
import com.proshop.order.repository.CartRepository;
import com.proshop.order.repository.OrderRepository;
import com.proshop.order.repository.PaymentRepository;
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
            PaymentRepository paymentRepository) {

        return args -> {
            // ✅ Kiểm tra nếu database đã có dữ liệu thì không chạy
            if (cartRepository.count() > 0 || orderRepository.count() > 0) {
                System.out.println("⏭️ Database already has data. Skipping initialization...");
                return;
            }

            System.out.println("🚀 Starting database initialization...");

            // ✅ User sample
            //UUID user1 = UUID.randomUUID();
            //UUID user2 = UUID.randomUUID();
            //UUID user3 = UUID.randomUUID();

            long user1 = 1;
            long user2 = 2;

            // ✅ Product UUIDs giả lập (lấy từ ProductService)
            UUID laptop1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
            UUID laptop2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
            UUID cpu1    = UUID.fromString("33333333-3333-3333-3333-333333333333");
            UUID gpu1    = UUID.fromString("44444444-4444-4444-4444-444444444444");
            UUID ram1    = UUID.fromString("55555555-5555-5555-5555-555555555555");
            UUID ssd1    = UUID.fromString("66666666-6666-6666-6666-666666666666");

            List<UUID> products = Arrays.asList(laptop1, laptop2, cpu1, gpu1, ram1, ssd1);

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

            // =======================
            // 2️⃣ OrderService Data
            // =======================
            OrderEntity order1 = OrderEntity.builder()
                    .userId(user1)
                    .totalAmount(new BigDecimal("25000000"))
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order1); // ✅ LƯU ORDER TRƯỚC

            OrderEntity order2 = OrderEntity.builder()
                    .userId(user2)
                    .totalAmount(new BigDecimal("15000000"))
                    .status(OrderStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();
            orderRepository.save(order2); // ✅ LƯU ORDER TRƯỚC

            // =======================
            // 3️⃣ PaymentService Data
            // =======================
            PaymentEntity payment1 = PaymentEntity.builder()
                    .order(order1)
                    .amount(new BigDecimal("25000000")) // ✅ Thêm amount
                    .method(PaymentMethod.CREDIT_CARD)
                    .status(PaymentStatus.PENDING)
                    .paidAt(null)
                    .build();
            paymentRepository.save(payment1);

            PaymentEntity payment2 = PaymentEntity.builder()
                    .order(order2)
                    .amount(new BigDecimal("15000000")) // ✅ Thêm amount
                    .method(PaymentMethod.CASH)
                    .status(PaymentStatus.PAID)
                    .paidAt(LocalDateTime.now())
                    .build();
            paymentRepository.save(payment2);

            System.out.println("✅ Sample data inserted for Cart, Order, Payment");
        };
    }
}