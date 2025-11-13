package com.proshop.review_service.config;

import com.proshop.review_service.entity.*;
import com.proshop.review_service.repository.*;
import com.proshop.review_service.util.enums.ReactionTargetType;
import com.proshop.review_service.util.enums.ReactionType;
import com.proshop.review_service.util.enums.ReviewStatus;
import com.proshop.review_service.util.enums.ReviewType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;


@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            ReviewRepository reviewRepository,
            AnswerRepository answerRepository,
            ReviewCategoryRepository categoryRepository,
            TagRepository tagRepository,
            ReviewImageRepository imageRepository,
            ReviewReactionRepository reactionRepository) {

        return args -> {
            // ✅ Kiểm tra nếu database đã có dữ liệu thì không chạy
            if (categoryRepository.count() > 0 || reviewRepository.count() > 0) {
                System.out.println("⏭️ Database already has data. Skipping initialization...");
                return;
            }

            System.out.println("🚀 Starting review database initialization...");

            // ====================================
            // 1️⃣ TẠO CATEGORIES
            // ====================================
            ReviewCategoryEntity laptopCat = ReviewCategoryEntity.builder()
                    .name("Laptop")
                    .slug("laptop")
                    .description("Câu hỏi và thảo luận về laptop")
                    .icon("💻")
                    .isActive(true)
                    .displayOrder(1)
                    .build();
            categoryRepository.save(laptopCat);

            ReviewCategoryEntity pcCat = ReviewCategoryEntity.builder()
                    .name("PC Desktop")
                    .slug("pc-desktop")
                    .description("Câu hỏi về máy tính để bàn")
                    .icon("🖥️")
                    .isActive(true)
                    .displayOrder(2)
                    .build();
            categoryRepository.save(pcCat);

            ReviewCategoryEntity gamingCat = ReviewCategoryEntity.builder()
                    .name("Gaming")
                    .slug("gaming")
                    .description("Thiết bị gaming và phụ kiện")
                    .icon("🎮")
                    .isActive(true)
                    .displayOrder(3)
                    .build();
            categoryRepository.save(gamingCat);

            ReviewCategoryEntity phoneCat = ReviewCategoryEntity.builder()
                    .name("Smartphone")
                    .slug("smartphone")
                    .description("Điện thoại di động")
                    .icon("📱")
                    .isActive(true)
                    .displayOrder(4)
                    .build();
            categoryRepository.save(phoneCat);

            System.out.println("✅ Created 4 categories");

            // ====================================
            // 2️⃣ TẠO TAGS
            // ====================================
            TagEntity tagLaptop = TagEntity.builder().name("Laptop").slug("laptop").usageCount(0).build();
            TagEntity tagGaming = TagEntity.builder().name("Gaming").slug("gaming").usageCount(0).build();
            TagEntity tagHocTap = TagEntity.builder().name("Học tập").slug("hoc-tap").usageCount(0).build();
            TagEntity tagVanPhong = TagEntity.builder().name("Văn phòng").slug("van-phong").usageCount(0).build();
            TagEntity tag1520Trieu = TagEntity.builder().name("15-20 triệu").slug("15-20-trieu").usageCount(0).build();
            TagEntity tag2030Trieu = TagEntity.builder().name("20-30 triệu").slug("20-30-trieu").usageCount(0).build();
            TagEntity tagDell = TagEntity.builder().name("Dell").slug("dell").usageCount(0).build();
            TagEntity tagHP = TagEntity.builder().name("HP").slug("hp").usageCount(0).build();
            TagEntity tagAsus = TagEntity.builder().name("ASUS").slug("asus").usageCount(0).build();
            TagEntity tagRTX = TagEntity.builder().name("RTX").slug("rtx").usageCount(0).build();

            tagRepository.saveAll(Arrays.asList(
                    tagLaptop, tagGaming, tagHocTap, tagVanPhong,
                    tag1520Trieu, tag2030Trieu, tagDell, tagHP, tagAsus, tagRTX
            ));

            System.out.println("✅ Created 10 tags");

            // ====================================
            // 3️⃣ TẠO Q&A REVIEWS (Hỏi đáp)
            // ====================================

            // Q&A 1: Laptop học tập văn phòng
            ReviewEntity qa1 = ReviewEntity.builder()
                    .type(ReviewType.QA)
                    .title("Laptop nào phù hợp cho học tập và làm việc văn phòng?")
                    .content("Mình đang tìm mua laptop để học tập và làm việc văn phòng (Word, Excel, PowerPoint, duyệt web). " +
                            "Ngân sách khoảng 15-20 triệu. Các bạn có thể tư vấn giúp mình không? " +
                            "Mình cần pin tốt và màn hình không quá nhỏ. Cảm ơn các bạn!")
                    .userId(1L)
                    .userName("Nguyễn Văn Dũng")
                    .userAvatar("https://i.pravatar.cc/150?u=user1")
                    .category(laptopCat)
                    .productId(0L)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(245)
                    .likeCount(12)
                    .answerCount(0) // Sẽ cập nhật sau
                    .isFeatured(true)
                    .build();
            reviewRepository.save(qa1);

            // Update tags
            qa1.setTags(Arrays.asList(tagLaptop, tagHocTap, tagVanPhong, tag1520Trieu));
            reviewRepository.save(qa1);

            // AnswerEntitys cho QA1
            AnswerEntity ans1_1 = AnswerEntity.builder()
                    .review(qa1)
                    .userId(2L)
                    .userName("Trần Thị Bình")
                    .userAvatar("https://i.pravatar.cc/150?u=user2")
                    .content("Mình recommend Dell Inspiron 15 với CPU Intel i5 gen 12, RAM 8GB (có thể nâng cấp), SSD 256GB. " +
                            "Pin khoảng 8-10 tiếng, màn hình 15.6 inch Full HD. Giá khoảng 17-18 triệu. " +
                            "Rất phù hợp cho học tập và văn phòng.")
                    .likeCount(12)
                    .dislikeCount(0)
                    .isBestAnswer(true)
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .build();
            answerRepository.save(ans1_1);

            AnswerEntity ans1_2 = AnswerEntity.builder()
                    .review(qa1)
                    .userId(3L)
                    .userName("Lê Văn Trường Sơn")
                    .userAvatar("https://i.pravatar.cc/150?u=user3")
                    .content("HP Pavilion 15 cũng là lựa chọn tốt trong tầm giá này. Có cả phiên bản AMD Ryzen 5 và Intel i5, " +
                            "mình nghĩ AMD sẽ tiết kiệm điện hơn. Nên chọn phiên bản có SSD để máy chạy nhanh hơn.")
                    .likeCount(8)
                    .dislikeCount(1)
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build();
            answerRepository.save(ans1_2);

            // Update answer count
            qa1.setAnswerCount(2);
            reviewRepository.save(qa1);

            System.out.println("✅ Created Q&A 1 with 2 answers");

            // Q&A 2: Laptop gaming
            ReviewEntity qa2 = ReviewEntity.builder()
                    .type(ReviewType.QA)
                    .title("Laptop gaming tầm 30 triệu nên chọn gì?")
                    .content("Mình muốn mua laptop gaming để chơi game AAA và render video. Tầm giá 25-30 triệu. " +
                            "Quan tâm đến card đồ họa và tản nhiệt. Các bạn có gợi ý gì không?")
                    .userId(4L)
                    .userName("Phạm Minh Quân")
                    .userAvatar("https://i.pravatar.cc/150?u=user4")
                    .category(gamingCat)
                    .productId(0L)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(189)
                    .likeCount(15)
                    .answerCount(0)
                    .isFeatured(false)
                    .build();
            reviewRepository.save(qa2);

            qa2.setTags(Arrays.asList(tagGaming, tagLaptop, tag2030Trieu, tagAsus, tagRTX));
            reviewRepository.save(qa2);

            AnswerEntity ans2_1 = AnswerEntity.builder()
                    .review(qa2)
                    .userId(5L)
                    .userName("Nguyễn Gaming Pro")
                    .userAvatar("https://i.pravatar.cc/150?u=user5")
                    .content("ASUS TUF Gaming A15 với RTX 4060 là lựa chọn tốt. CPU Ryzen 7, RAM 16GB, màn hình 144Hz. " +
                            "Tản nhiệt khá ổn, chơi game mượt mà. Giá khoảng 28-29 triệu.")
                    .likeCount(18)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now().minusHours(12))
                    .build();
            answerRepository.save(ans2_1);

            qa2.setAnswerCount(1);
            reviewRepository.save(qa2);

            System.out.println("✅ Created Q&A 2 with 1 answer");

            // Q&A 3: Build PC
            ReviewEntity qa3 = ReviewEntity.builder()
                    .type(ReviewType.QA)
                    .title("Build PC gaming 40 triệu có nên không?")
                    .content("Mình đang cân nhắc giữa mua laptop gaming 30 triệu vs build PC gaming 40 triệu. " +
                            "PC sẽ mạnh hơn nhiều đúng không? Nhưng không tiện bằng laptop. Các bạn có kinh nghiệm gì không?")
                    .userId(6L)
                    .userName("Hoàng Đức Anh")
                    .userAvatar("https://i.pravatar.cc/150?u=user6")
                    .category(pcCat)
                    .productId(0L)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(156)
                    .likeCount(9)
                    .answerCount(3)
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build();
            reviewRepository.save(qa3);

            System.out.println("✅ Created Q&A 3");

            // ====================================
            // 4️⃣ TẠO PRODUCT REVIEWS (Đánh giá sản phẩm)
            // ====================================

            // Product IDs giả lập (UUID từ product-service)
            Long laptop1Id = 1L; // Dell Inspiron 15
            Long laptop2Id = 2L; // ASUS TUF Gaming
            Long laptop3Id = 3L; // HP Pavilion 15

            // Review 1: Dell Inspiron 15 - 5 sao
            ReviewEntity review1 = ReviewEntity.builder()
                    .type(ReviewType.PRODUCT_REVIEW)
                    .title(null) // Product review không cần title
                    .content("Laptop rất tốt cho công việc văn phòng! Pin trâu, màn hình sáng, bàn phím gõ êm. " +
                            "Mình dùng được 2 tháng rồi, rất hài lòng. Thiết kế đẹp, nhẹ, dễ mang theo. " +
                            "Xử lý Word, Excel, PowerPoint mượt mà. Xem phim cũng ổn.")
                    .userId(7L)
                    .userName("Trần Văn Hùng")
                    .userAvatar("https://i.pravatar.cc/150?u=user7")
                    .productId(laptop1Id)
                    .productName("Dell Inspiron 15")
                    .rating(5.0)
                    .category(null) // Product review không cần category
                    .status(ReviewStatus.APPROVED)
                    .viewCount(45)
                    .likeCount(8)
                    .isVerified(true) // Đã mua hàng
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .build();
            reviewRepository.save(review1);

            // Thêm ảnh thực tế
            ReviewImageEntity img1_1 = ReviewImageEntity.builder()
                    .review(review1)
                    .imageUrl("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500")
                    .displayOrder(1)
                    .build();
            ReviewImageEntity img1_2 = ReviewImageEntity.builder()
                    .review(review1)
                    .imageUrl("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500")
                    .displayOrder(2)
                    .build();
            imageRepository.saveAll(Arrays.asList(img1_1, img1_2));

            System.out.println("✅ Created Product Review 1 (5 stars) with 2 images");

            // Review 2: Dell Inspiron 15 - 4 sao
            ReviewEntity review2 = ReviewEntity.builder()
                    .type(ReviewType.PRODUCT_REVIEW)
                    .content("Laptop ổn cho giá tiền. Pin khá tốt khoảng 7-8 tiếng. " +
                            "Nhưng RAM 8GB hơi ít, nên nâng cấp lên 16GB sẽ tốt hơn. " +
                            "Màn hình hơi lóa ngoài nắng. Tổng thể thì OK.")
                    .userId(8L)
                    .userName("Lê Thị Mai")
                    .userAvatar("https://i.pravatar.cc/150?u=user8")
                    .productId(laptop1Id)
                    .productName("Dell Inspiron 15")
                    .rating(4.0)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(32)
                    .likeCount(5)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now().minusDays(7))
                    .build();
            reviewRepository.save(review2);

            ReviewImageEntity img2_1 = ReviewImageEntity.builder()
                    .review(review2)
                    .imageUrl("https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=500")
                    .displayOrder(1)
                    .build();
            imageRepository.save(img2_1);

            System.out.println("✅ Created Product Review 2 (4 stars) with 1 image");

            // Review 3: ASUS TUF Gaming - 5 sao
            ReviewEntity review3 = ReviewEntity.builder()
                    .type(ReviewType.PRODUCT_REVIEW)
                    .content("Laptop gaming đỉnh! Chơi game mượt 144fps, tản nhiệt tốt. " +
                            "RTX 4060 xử lý mọi game hiện tại ở setting cao. " +
                            "Màn hình 144Hz rất mượt mà. Bàn phím RGB đẹp. " +
                            "Đáng tiền!")
                    .userId(9L)
                    .userName("Nguyễn Gaming VN")
                    .userAvatar("https://i.pravatar.cc/150?u=user9")
                    .productId(laptop2Id)
                    .productName("ASUS TUF Gaming A15")
                    .rating(5.0)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(98)
                    .likeCount(24)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .build();
            reviewRepository.save(review3);

            ReviewImageEntity img3_1 = ReviewImageEntity.builder()
                    .review(review3)
                    .imageUrl("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=500")
                    .displayOrder(1)
                    .build();
            ReviewImageEntity img3_2 = ReviewImageEntity.builder()
                    .review(review3)
                    .imageUrl("https://images.unsplash.com/photo-1625695415487-07d22f0c86e5?w=500")
                    .displayOrder(2)
                    .build();
            ReviewImageEntity img3_3 = ReviewImageEntity.builder()
                    .review(review3)
                    .imageUrl("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=500")
                    .displayOrder(3)
                    .build();
            imageRepository.saveAll(Arrays.asList(img3_1, img3_2, img3_3));

            System.out.println("✅ Created Product Review 3 (5 stars) with 3 images");

            // Review 4: ASUS TUF Gaming - 3 sao
            ReviewEntity review4 = ReviewEntity.builder()
                    .type(ReviewType.PRODUCT_REVIEW)
                    .content("Laptop mạnh nhưng nặng quá, 2.5kg. Pin yếu chỉ khoảng 3-4 tiếng. " +
                            "Fan ồn khi chơi game. Giá hơi cao so với cấu hình. " +
                            "Nếu chỉ ngồi một chỗ chơi game thì OK.")
                    .userId(10L)
                    .userName("Phạm Văn Khánh")
                    .userAvatar("https://i.pravatar.cc/150?u=user10")
                    .productId(laptop2Id)
                    .productName("ASUS TUF Gaming A15")
                    .rating(3.0)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(67)
                    .likeCount(6)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now().minusDays(8))
                    .build();
            reviewRepository.save(review4);

            System.out.println("✅ Created Product Review 4 (3 stars)");

            // Review 5: HP Pavilion 15 - 4 sao
            ReviewEntity review5 = ReviewEntity.builder()
                    .type(ReviewType.PRODUCT_REVIEW)
                    .content("Laptop văn phòng tốt trong tầm giá. Pin ổn, thiết kế đẹp. " +
                            "AMD Ryzen 5 xử lý nhanh. Chỉ tiếc là không có đèn bàn phím. " +
                            "Cân nhắc cho học sinh, sinh viên.")
                    .userId(11L)
                    .userName("Đỗ Thị Lan")
                    .userAvatar("https://i.pravatar.cc/150?u=user11")
                    .productId(laptop3Id)
                    .productName("HP Pavilion 15")
                    .rating(4.0)
                    .status(ReviewStatus.APPROVED)
                    .viewCount(54)
                    .likeCount(7)
                    .isVerified(true)
                    .createdAt(LocalDateTime.now().minusDays(4))
                    .build();
            reviewRepository.save(review5);

            ReviewImageEntity img5_1 = ReviewImageEntity.builder()
                    .review(review5)
                    .imageUrl("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500")
                    .displayOrder(1)
                    .build();
            imageRepository.save(img5_1);

            System.out.println("✅ Created Product Review 5 (4 stars) with 1 image");

            // ====================================
            // 5️⃣ TẠO REACTIONS
            // ====================================

            // Một vài reactions mẫu
            ReviewReactionEntity reaction1 = ReviewReactionEntity.builder()
                    .targetType(ReactionTargetType.REVIEW)
                    .targetId(qa1.getId())
                    .userId(2L)
                    .type(ReactionType.LIKE)
                    .build();
            reactionRepository.save(reaction1);

            ReviewReactionEntity reaction2 = ReviewReactionEntity.builder()
                    .targetType(ReactionTargetType.ANSWER)
                    .targetId(ans1_1.getId())
                    .userId(1L)
                    .type(ReactionType.LIKE)
                    .build();
            reactionRepository.save(reaction2);

            System.out.println("✅ Created 2 sample reactions");

            // ====================================
            // SUMMARY
            // ====================================
            System.out.println("=".repeat(60));
            System.out.println("✅ Review database initialization completed!");
            System.out.println("📊 Summary:");
            System.out.println("   - 4 Categories (Laptop, PC, Gaming, Smartphone)");
            System.out.println("   - 10 TagEntitys");
            System.out.println("   - 3 Q&A Reviews with 4 AnswerEntitys");
            System.out.println("   - 5 Product Reviews with 8 Images");
            System.out.println("   - 2 Sample Reactions");
            System.out.println("=" .repeat(60));
        };
    }
}
