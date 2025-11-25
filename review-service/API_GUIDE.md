# 📚 Hướng dẫn sử dụng Review Service APIs

## 📋 Mục lục
1. [Question APIs (Hỏi đáp - Q&A)](#question-apis)
2. [Product Review APIs (Đánh giá sản phẩm)](#product-review-apis)
3. [Answer APIs (Câu trả lời)](#answer-apis)
4. [Reaction APIs (Like/Dislike)](#reaction-apis)

---

## 🔐 Authentication
Tất cả các API đều yêu cầu JWT token trong header:
```
Authorization: Bearer <your_jwt_token>
```

---

## ⚠️ QUAN TRỌNG: UUID cho Product ID

### Thay đổi từ Long sang UUID
Tất cả `productId` trong Product Review APIs giờ sử dụng **UUID** thay vì **Long**.

**Trước (KHÔNG còn dùng):**
```json
{
  "productId": 100
}
```

**Sau (MỚI - BẮT BUỘC):**
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174000"
}
```

### Lợi ích của UUID
- ✅ Tính duy nhất toàn cục
- ✅ Bảo mật cao hơn
- ✅ Tương thích với product-service

### Xác minh sản phẩm
Khi tạo review, hệ thống sẽ **tự động kiểm tra** sản phẩm có tồn tại không bằng cách call qua `product-service`:

```
POST /api/product-reviews
→ Validate productId với product-service
→ Nếu sản phẩm không tồn tại → Trả về lỗi 404
→ Nếu OK → Tạo review
```

**Lưu ý:** Bạn PHẢI sử dụng UUID thật từ product-service, không thể tạo review cho sản phẩm không tồn tại.

---

## 1️⃣ Question APIs (Hỏi đáp - Q&A) {#question-apis}

### 1.1. Tạo câu hỏi mới
**Endpoint:** `POST /api/questions`

**Request Body:**
```json
{
  "title": "Laptop nào phù hợp cho lập trình và gaming?",
  "content": "Em đang tìm laptop có cấu hình mạnh, giá khoảng 20-25 triệu. Các bác tư vấn giúp em với!",
  "categoryId": 1,
  "tags": ["laptop", "gaming", "programming"]
}
```

**Response (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "Question created successfully",
  "data": {
    "id": 1,
    "userId": 123,
    "userName": "Nguyễn Văn A",
    "userAvatar": "https://i.pravatar.cc/150?u=123",
    "title": "Laptop nào phù hợp cho lập trình và gaming?",
    "content": "Em đang tìm laptop có cấu hình mạnh...",
    "category": {
      "id": 1,
      "name": "Laptop",
      "slug": "laptop",
      "icon": "💻"
    },
    "likeCount": 0,
    "viewCount": 0,
    "answerCount": 0,
    "status": "PENDING",
    "isVerified": false,
    "isFeatured": false,
    "createdAt": "2025-11-26T10:30:00",
    "updatedAt": "2025-11-26T10:30:00",
    "tags": [
      {"id": 1, "name": "laptop", "usageCount": 1},
      {"id": 2, "name": "gaming", "usageCount": 1}
    ]
  }
}
```

---

### 1.2. Lấy danh sách câu hỏi
**Endpoint:** `GET /api/questions?page=0&size=10`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Laptop nào phù hợp...",
        "content": "Em đang tìm laptop...",
        "category": {...},
        "answerCount": 5,
        "viewCount": 245,
        "likeCount": 12,
        "createdAt": "2025-11-26T10:30:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

---

### 1.3. Lấy chi tiết câu hỏi
**Endpoint:** `GET /api/questions/{id}`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "id": 1,
    "title": "Laptop nào phù hợp...",
    "content": "Em đang tìm laptop...",
    "answers": [
      {
        "id": 1,
        "userId": 456,
        "userName": "Chuyên gia Minh",
        "content": "Mình khuyên bạn nên chọn...",
        "likeCount": 8,
        "dislikeCount": 0,
        "isBestAnswer": true,
        "isVerified": true,
        "createdAt": "2025-11-26T11:00:00"
      }
    ],
    "answerCount": 5,
    "viewCount": 245,
    "likeCount": 12
  }
}
```

---

### 1.4. Tìm kiếm câu hỏi
**Endpoint:** `GET /api/questions/search?keyword=laptop&page=0&size=10`

---

### 1.5. Lấy câu hỏi theo category
**Endpoint:** `GET /api/questions/category/{categoryId}?page=0&size=10`

---

### 1.6. Lấy câu hỏi HOT
**Endpoint:** `GET /api/questions/hot?limit=10`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "id": 1,
      "title": "Laptop nào tốt nhất...",
      "viewCount": 1250,
      "likeCount": 45,
      "answerCount": 23
    }
  ]
}
```

---

### 1.7. Cập nhật câu hỏi
**Endpoint:** `PUT /api/questions/{id}`

**Request Body:**
```json
{
  "title": "Laptop nào phù hợp cho lập trình Python?",
  "content": "Em muốn mua laptop để học Python...",
  "tags": ["laptop", "python", "programming"]
}
```

---

### 1.8. Xóa câu hỏi
**Endpoint:** `DELETE /api/questions/{id}`

---

### 1.9. Cập nhật trạng thái (Admin only)
**Endpoint:** `PUT /api/questions/{id}/status`

**Request Body:**
```json
{
  "status": "APPROVED",
  "rejectionReason": null
}
```

**Status values:** `PENDING`, `APPROVED`, `REJECTED`, `CLOSED`

---

## 2️⃣ Product Review APIs (Đánh giá sản phẩm) {#product-review-apis}

### 2.1. Tạo đánh giá sản phẩm (với ảnh)
**Endpoint:** `POST /api/product-reviews`

**Content-Type:** `multipart/form-data`

**Form Data:**
```
review: {
  "productId": "123e4567-e89b-12d3-a456-426614174000",
  "rating": 5.0,
  "content": "Sản phẩm rất tốt, giao hàng nhanh!",
  "tags": ["chất lượng tốt", "giao hàng nhanh"]
}
images: [file1.jpg, file2.jpg, file3.jpg]
```

**⚠️ LƯU Ý:** `productId` phải là UUID hợp lệ của sản phẩm từ product-service

**Response (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "Product review created successfully",
  "data": {
    "id": 1,
    "userId": 123,
    "userName": "Nguyễn Văn B",
    "productId": "123e4567-e89b-12d3-a456-426614174000",
    "productName": "Laptop Dell XPS 15",
    "rating": 5.0,
    "content": "Sản phẩm rất tốt...",
    "images": [
      {
        "id": 1,
        "imageUrl": "https://storage.example.com/review/img1.jpg",
        "displayOrder": 0
      }
    ],
    "likeCount": 0,
    "viewCount": 0,
    "status": "PENDING",
    "createdAt": "2025-11-26T14:00:00"
  }
}
```

---

### 2.2. Tạo đánh giá (không ảnh - JSON)
**Endpoint:** `POST /api/product-reviews`

**Content-Type:** `application/json`

**Request Body:**
```json
{
  "productId": "123e4567-e89b-12d3-a456-426614174000",
  "rating": 4.5,
  "content": "Sản phẩm ổn, đáng giá tiền",
  "tags": ["tốt"]
}
```

**⚠️ LƯU Ý:** `productId` phải là UUID hợp lệ của sản phẩm

---

### 2.3. Lấy đánh giá của sản phẩm
**Endpoint:** `GET /api/product-reviews/product/{productId}?page=0&size=10`

**Example:** `GET /api/product-reviews/product/123e4567-e89b-12d3-a456-426614174000?page=0&size=10`

**⚠️ LƯU Ý:** `{productId}` phải là UUID của sản phẩm

**Response:**
```json
{
  "status": "SUCCESS",
  "data": {
    "content": [
      {
        "id": 1,
        "userName": "Nguyễn Văn B",
        "rating": 5.0,
        "content": "Sản phẩm rất tốt...",
        "images": [...],
        "likeCount": 15,
        "createdAt": "2025-11-26T14:00:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 5
  }
}
```

---

### 2.4. Lấy rating trung bình của sản phẩm
**Endpoint:** `GET /api/product-reviews/product/{productId}/average-rating`

**Example:** `GET /api/product-reviews/product/123e4567-e89b-12d3-a456-426614174000/average-rating`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": 4.5
}
```

---

### 2.5. Đếm số review của sản phẩm
**Endpoint:** `GET /api/product-reviews/product/{productId}/count`

**Example:** `GET /api/product-reviews/product/123e4567-e89b-12d3-a456-426614174000/count`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": 156
}
```

---

### 2.6. Lọc review theo rating
**Endpoint:** `GET /api/product-reviews/rating/{minRating}?page=0&size=10`

Ví dụ: `/api/product-reviews/rating/4.0` - Lấy review có rating >= 4.0

---

### 2.7. Lấy review HOT
**Endpoint:** `GET /api/product-reviews/hot?limit=10`

---

### 2.8. Cập nhật review (với ảnh mới)
**Endpoint:** `PUT /api/product-reviews/{id}`

**Content-Type:** `multipart/form-data`

**Form Data:**
```
review: {
  "rating": 4.0,
  "content": "Cập nhật đánh giá sau 1 tháng sử dụng..."
}
images: [new_image1.jpg, new_image2.jpg]
```

---

### 2.9. Xóa review
**Endpoint:** `DELETE /api/product-reviews/{id}`

---

## 3️⃣ Answer APIs (Câu trả lời) {#answer-apis}

### 3.1. Tạo câu trả lời
**Endpoint:** `POST /api/answers`

**Request Body:**
```json
{
  "questionId": 1,
  "content": "Mình khuyên bạn nên chọn Laptop Dell XPS hoặc Macbook Pro. Hai dòng này phù hợp cho cả lập trình và gaming nhẹ."
}
```

**Response (201 Created):**
```json
{
  "status": "SUCCESS",
  "message": "Answer created successfully",
  "data": {
    "id": 1,
    "reviewId": 1,
    "userId": 456,
    "userName": "Chuyên gia Minh",
    "content": "Mình khuyên bạn nên chọn...",
    "likeCount": 0,
    "dislikeCount": 0,
    "isBestAnswer": false,
    "isVerified": false,
    "isFromShop": false,
    "createdAt": "2025-11-26T15:00:00"
  }
}
```

---

### 3.2. Lấy tất cả câu trả lời của câu hỏi
**Endpoint:** `GET /api/answers/question/{questionId}`

**Response:**
```json
{
  "status": "SUCCESS",
  "data": [
    {
      "id": 1,
      "userName": "Chuyên gia Minh",
      "content": "Mình khuyên bạn...",
      "likeCount": 8,
      "dislikeCount": 0,
      "isBestAnswer": true,
      "isVerified": true,
      "createdAt": "2025-11-26T15:00:00"
    },
    {
      "id": 2,
      "userName": "User XYZ",
      "content": "Theo mình thì...",
      "likeCount": 3,
      "dislikeCount": 1,
      "isBestAnswer": false,
      "createdAt": "2025-11-26T15:30:00"
    }
  ]
}
```

---

### 3.3. Cập nhật câu trả lời
**Endpoint:** `PUT /api/answers/{id}`

**Request Body:**
```json
{
  "content": "Mình cập nhật lại: Nên chọn Laptop Dell XPS 15 với Core i7..."
}
```

---

### 3.4. Xóa câu trả lời
**Endpoint:** `DELETE /api/answers/{id}`

---

## 4️⃣ Reaction APIs (Like/Dislike) {#reaction-apis}

### 4.1. Thêm/Xóa reaction
**Endpoint:** `POST /api/reactions`

**Request Body:**

**Like câu hỏi:**
```json
{
  "targetType": "QUESTION",
  "targetId": 1,
  "type": "LIKE"
}
```

**Like đánh giá sản phẩm:**
```json
{
  "targetType": "PRODUCT_REVIEW",
  "targetId": 5,
  "type": "LIKE"
}
```

**Like câu trả lời:**
```json
{
  "targetType": "ANSWER",
  "targetId": 10,
  "type": "LIKE"
}
```

**Dislike câu trả lời:**
```json
{
  "targetType": "ANSWER",
  "targetId": 10,
  "type": "DISLIKE"
}
```

**Response:**
```json
{
  "status": "SUCCESS",
  "message": "Reaction added successfully",
  "data": null
}
```

**Lưu ý:**
- Nếu click lại reaction giống nhau → **Xóa reaction** (toggle)
- Nếu đổi reaction → **Cập nhật** reaction mới

**Target Types:**
- `QUESTION` - Reaction cho câu hỏi
- `PRODUCT_REVIEW` - Reaction cho đánh giá sản phẩm
- `ANSWER` - Reaction cho câu trả lời

**Reaction Types:**
- `LIKE` - Thích
- `DISLIKE` - Không thích (chỉ dùng cho Answer)
- `HELPFUL` - Hữu ích (tùy chọn)

---

## 📝 Ví dụ sử dụng với cURL

### Tạo câu hỏi
```bash
curl -X POST http://localhost:8084/api/questions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Laptop nào phù hợp cho lập trình?",
    "content": "Em đang tìm laptop...",
    "categoryId": 1,
    "tags": ["laptop", "programming"]
  }'
```

### Tạo đánh giá với ảnh
```bash
curl -X POST http://localhost:8084/api/product-reviews \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F 'review={
    "productId": "123e4567-e89b-12d3-a456-426614174000",
    "rating": 5.0,
    "content": "Sản phẩm tuyệt vời!"
  }' \
  -F 'images=@/path/to/image1.jpg' \
  -F 'images=@/path/to/image2.jpg'
```

**⚠️ Thay UUID bằng productId thật từ product-service**

### Like câu hỏi
```bash
curl -X POST http://localhost:8084/api/reactions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "targetType": "QUESTION",
    "targetId": 1,
    "type": "LIKE"
  }'
```

---

## 🎯 Use Cases phổ biến

### 1. User đặt câu hỏi về sản phẩm
```
1. POST /api/questions - Tạo câu hỏi
2. GET /api/questions/{id} - Xem câu hỏi và các câu trả lời
3. POST /api/answers - User khác trả lời
4. POST /api/reactions - Like câu trả lời hay
```

### 2. User đánh giá sản phẩm sau khi mua
```
1. POST /api/product-reviews - Tạo review với ảnh
2. GET /api/product-reviews/product/{id} - Xem tất cả review của sản phẩm
3. GET /api/product-reviews/product/{id}/average-rating - Xem rating TB
4. POST /api/reactions - Like review hữu ích
```

### 3. Hiển thị Q&A trên trang sản phẩm
```
1. GET /api/questions/hot - Lấy câu hỏi hot
2. GET /api/answers/question/{id} - Lấy câu trả lời
3. POST /api/reactions - User like câu hỏi/trả lời
```

---

## ⚠️ Error Codes

| Code | Message | Mô tả |
|------|---------|-------|
| 401 | UNAUTHORIZED | Thiếu hoặc sai JWT token |
| 403 | PERMISSION_DENIED | Không có quyền (vd: sửa câu hỏi của người khác) |
| 404 | ENTITY_NOT_EXISTS | Không tìm thấy resource |
| 400 | BAD_REQUEST | Dữ liệu không hợp lệ |
| 400 | FIELD_REQUIRED | Thiếu field bắt buộc |

---

## 🔒 Phân quyền

### User (đã đăng nhập)
- ✅ Tạo câu hỏi, review, câu trả lời
- ✅ Sửa/xóa câu hỏi, review, answer của mình
- ✅ Like/dislike
- ✅ Xem tất cả nội dung đã approved

### Admin
- ✅ Tất cả quyền của User
- ✅ Duyệt/từ chối câu hỏi, review (PUT /status)
- ✅ Xóa bất kỳ nội dung nào
- ✅ Đánh dấu featured, verified

---

## 📌 Lưu ý quan trọng

1. **Multipart vs JSON:**
   - Dùng `multipart/form-data` khi upload ảnh
   - Dùng `application/json` khi không có ảnh

2. **Pagination:**
   - Default: `page=0, size=10`
   - Max size: 100

3. **Status flow:**
   ```
   PENDING → APPROVED → CLOSED
           ↘ REJECTED → PENDING (có thể review lại)
   ```

4. **Rating:**
   - Giá trị: 1.0 - 5.0
   - Cho phép số thập phân (vd: 4.5)

5. **Image upload:**
   - Max 5 ảnh/review
   - Format: JPG, PNG, GIF
   - Max size: 5MB/ảnh

---

**🎉 Happy Coding!**
