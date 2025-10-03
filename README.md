# Backend API cho Dự án Web Bán Hàng

Đây là API backend cho một ứng dụng E-commerce hoàn chỉnh, được xây dựng bằng Java và Spring Boot. Hệ thống cung cấp đầy đủ các chức năng từ quản lý sản phẩm, xác thực người dùng, giỏ hàng, xử lý đơn hàng đến tích hợp thanh toán trực tuyến.

## ✨ Tính năng chính

*   **Quản lý Sản phẩm & Danh mục:** CRUD đầy đủ cho sản phẩm và danh mục, cùng với API tìm kiếm, lọc, phân trang và sắp xếp cực kỳ mạnh mẽ.
*   **Xác thực & Phân quyền:**
    *   Hệ thống đăng ký, đăng nhập an toàn sử dụng JWT.
    *   Cơ chế Access Token (ngắn hạn) và Refresh Token (dài hạn) được lưu trong `HttpOnly` cookie.
    *   Tăng cường bảo mật bằng cách "đóng dấu" địa chỉ IP vào token.
    *   Phân quyền rõ ràng giữa `USER` và `ADMIN`.
*   **Giỏ hàng hiệu năng cao:** Quản lý giỏ hàng của người dùng bằng Redis, đảm bảo tốc độ phản hồi nhanh chóng.
*   **Quản lý Đơn hàng:** Luồng xử lý từ lúc tạo đơn hàng, cập nhật tồn kho, đến việc quản lý và cập nhật trạng thái đơn hàng cho cả người dùng và admin.
*   **Tích hợp Thanh toán:** Tích hợp với cổng thanh toán PayPal (hỗ trợ cả tài khoản PayPal và thẻ Credit/Debit).
*   **Kiến trúc API hiện đại:**
    *   Sử dụng DTO và Java Record để tối ưu hóa dữ liệu truyền tải.
    *   Chuẩn hóa cấu trúc response cho tất cả các API, giúp frontend dễ dàng tích hợp.
    *   Xử lý lỗi tập trung và trả về thông báo lỗi có cấu trúc.

## 🛠️ Công nghệ sử dụng

*   **Backend:**
    *   Java 21
    *   Spring Boot 4.0.0-M3
    *   Spring Security, Spring Data JPA
*   **Database:**
    *   MySQL
    *   Redis (cho giỏ hàng)
*   **Build Tool:**
    *   Gradle
*   **Authentication:**
    *   JSON Web Token (JWT) - thư viện `jjwt`
*   **API:**
    *   RESTful API
    *   `spring-filter` để tạo query động
*   **Thanh toán:**
    *   PayPal REST APIs v2

## 🚀 Cài đặt và Khởi chạy

### Yêu cầu

*   JDK 21 hoặc cao hơn
*   Gradle 8.x
*   MySQL Server
*   Redis Server
*   Một công cụ API client như [Postman](https://www.postman.com/)

### 1. Clone a repository

```bash
git clone https://your-repository-url.git
cd your-project-folder
```

### 2. Cấu hình môi trường

Tạo một file `application.properties` trong thư mục `src/main/resources/` và điền các thông tin cấu hình cần thiết.

**Template `application.properties`:**
```properties
# Server Port
server.port=8080

# MySQL Database Connection
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password # <-- THAY BẰNG MẬT KHẨU MYSQL CỦA BẠN

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Redis Connection
spring.data.redis.host=localhost
spring.data.redis.port=6379
# spring.data.redis.password=your_redis_password

# JWT Configuration
app.jwt.secret=ThisIsAReallyLongAndSecureSecretKeyForYourEcommerceApplicationDoNotShareIt
app.jwt.expiration-ms=900000 # 15 phút
app.jwt.refresh-expiration-ms=604800000 # 7 ngày

# PayPal Configuration
paypal.client-id=YOUR_PAYPAL_CLIENT_ID # <-- THAY BẰNG PAYPAL CLIENT ID CỦA BẠN
paypal.client-secret=YOUR_PAYPAL_CLIENT_SECRET # <-- THAY BẰNG PAYPAL CLIENT SECRET CỦA BẠN
paypal.mode=sandbox # Chuyển thành 'live' khi triển khai thật
```
**Lưu ý:** Đừng quên tạo một database tên là `ecommerce_db` trong MySQL.

### 3. Build và chạy ứng dụng

Dự án đã được cấu hình sẵn với một script `manage.sh` để dễ dàng quản lý.

**Cấp quyền thực thi cho script:**
```bash
chmod +x manage.sh
```

**Cài đặt/cập nhật các thư viện:**
```bash
./manage.sh update
```

**Khởi động server ở chế độ development:**
```bash
./manage.sh dev
```
Server sẽ khởi động tại `http://localhost:8080`. Khi khởi động lần đầu, một tài khoản admin mặc định sẽ được tạo với thông tin:
*   **Username:** `admin`
*   **Password:** `admin123`

---

## 📖 Mô tả các Endpoint

Base URL: `http://localhost:8080/api/v1`

### Authentication (`/auth`)

| Method & Path | Mô tả | Quyền | Request Body |
| :--- | :--- | :--- | :--- |
| `POST /register`| Đăng ký tài khoản người dùng mới. | Public | `{"username": "user", "password": "123"}` |
| `POST /login`| Đăng nhập và nhận `access_token`, `refresh_token` trong `HttpOnly` cookie. | Public | `{"username": "user", "password": "123"}` |
| `POST /refresh`| Dùng `refresh_token` từ cookie để lấy `access_token` mới. | Public | (Không có) |
| `POST /logout`| Đăng xuất, xóa token trên server và cookie ở client. | User | (Không có) |

### Products (`/products`)

| Method & Path | Mô tả | Quyền | Request Params |
| :--- | :--- | :--- | :--- |
| `GET /`| Lấy danh sách sản phẩm với khả năng lọc, sắp xếp, phân trang. | Public | `filter`, `page`, `size`, `sort` |
| `POST /`| Tạo sản phẩm mới. | Admin | `ProductRequestDTO` |
| `GET /{id}`| Lấy chi tiết sản phẩm và các sản phẩm liên quan. | Public | (Không có) |
| `PUT /{id}`| Cập nhật thông tin sản phẩm. | Admin | `ProductRequestDTO` |
| `DELETE /{id}`| Xóa sản phẩm. | Admin | (Không có) |
| `GET /latest`| Lấy danh sách sản phẩm mới nhất. | Public | `limit` (mặc định: 10) |
| `GET /featured`| Lấy danh sách sản phẩm nổi bật. | Public | `limit` (mặc định: 10) |
| `GET /suggestions`| Gợi ý sản phẩm cho ô tìm kiếm. | Public | `keyword`, `limit` |

**Cú pháp `filter` mạnh mẽ:**
*   `?filter=name:'áo' and category.id:1`
*   `?filter=price > 500000 and price < 1000000`
*   `?sort=price,desc`

### Categories (`/categories`)

| Method & Path | Mô tả | Quyền | Request Body |
| :--- | :--- | :--- | :--- |
| `GET /`| Lấy danh sách tất cả danh mục. | Public | (Không có) |
| `POST /`| Tạo danh mục mới. | Admin | `{"name": "New Category"}` |
| `GET /{id}`| Lấy chi tiết một danh mục. | Public | (Không có) |
| `PUT /{id}`| Cập nhật tên danh mục. | Admin | `{"name": "Updated Name"}` |
| `DELETE /{id}`| Xóa một danh mục. | Admin | (Không có) |
| `GET /{id}/products`| Lấy danh sách sản phẩm thuộc một danh mục (hỗ trợ filter, sort, page).| Public | `filter`, `page`, `size`, `sort` |

### Shopping Cart (`/cart`)

Tất cả các endpoint trong đây đều yêu cầu quyền `User`.

| Method & Path | Mô tả | Request Body |
| :--- | :--- | :--- |
| `GET /`| Xem nội dung giỏ hàng hiện tại. | (Không có) |
| `POST /`| Thêm sản phẩm mới hoặc cập nhật số lượng. | `{"productId": 1, "quantity": 2}` |
| `DELETE /items/{productId}`| Xóa một sản phẩm khỏi giỏ hàng. | (Không có) |
| `DELETE /`| Xóa toàn bộ giỏ hàng. | (Không có) |

### Orders (`/orders`)

| Method & Path | Mô tả | Quyền | Request Body |
| :--- | :--- | :--- | :--- |
| `POST /`| Tạo đơn hàng mới từ giỏ hàng hiện tại. | User | `OrderRequestDTO` |
| `GET /my-orders`| Lấy lịch sử đơn hàng của người dùng đang đăng nhập. | User | (Hỗ trợ phân trang) |
| `GET /{id}`| Lấy chi tiết một đơn hàng của người dùng. | User | (Không có) |

### Admin Orders (`/admin/orders`)

Tất cả các endpoint trong đây đều yêu cầu quyền `Admin`.

| Method & Path | Mô tả | Request Body/Params |
| :--- | :--- | :--- |
| `GET /`| Lấy danh sách tất cả đơn hàng (hỗ trợ filter, sort, page).| `filter`, `page`, `size`, `sort` |
| `GET /{id}`| Lấy chi tiết một đơn hàng bất kỳ. | (Không có) |
| `PATCH /{id}/status`| Cập nhật trạng thái đơn hàng. | `{"status": "PROCESSING"}` |

### Payments (`/payments`)

| Method & Path | Mô tả | Quyền | Request Body |
| :--- | :--- | :--- | :--- |
| `POST /paypal/create-order`| Tạo một đơn hàng trên server PayPal. | User | `{"orderId": 123}` |
| `POST /paypal/capture-order`| Xác nhận và ghi nhận thanh toán sau khi người dùng đồng ý. | User | `{"payPalOrderId": "ABC..."}` |