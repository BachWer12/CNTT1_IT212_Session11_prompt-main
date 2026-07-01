# Hệ Thống Quản Lý Ứng Viên Nam Vương (Candidate Management System) - Thiết Kế Chi Tiết

Tài liệu này mô tả chi tiết thiết kế hệ thống, cơ sở dữ liệu, các điểm cuối API và các trường hợp nghiệp vụ cho phân hệ Quản lý Ứng viên (Candidate/Contestant) tham dự cuộc thi Nam vương.

---

## 1. Kiến Trúc Tổng Quan (System Architecture)

Hệ thống được thiết kế theo mô hình phân tầng tiêu chuẩn của Spring Boot:
- **Presentation Layer (`CandidateController`)**: Tiếp nhận các yêu cầu HTTP, xử lý xác thực dữ liệu đầu vào (Validation) và trả về phản hồi JSON.
- **Service Layer (`CandidateService`)**: Xử lý logic nghiệp vụ, chuyển đổi dữ liệu giữa thực thể cơ sở dữ liệu (Entity) và dữ liệu truyền tải (DTO).
- **Data Access Layer (`CandidateRepository`)**: Tương tác với cơ sở dữ liệu H2 (hoặc MySQL/PostgreSQL) sử dụng Spring Data JPA.
- **Exception Layer (`GlobalExceptionHandler`)**: Bắt các lỗi hệ thống hoặc nghiệp vụ và định dạng lại cấu trúc lỗi trả về cho client.

---

## 2. Thiết Kế Cơ Sở Dữ Liệu (Database Schema)

Bảng dữ liệu lưu trữ thông tin ứng viên:

```sql
create table candidate (
    id bigint not null auto_increment,
    full_name varchar(100) not null,
    age integer not null,
    height double not null,
    weight double not null,
    nationality varchar(50) not null,
    bio varchar(1000) default null,
    photo_url varchar(255) default null,
    score double default null,
    status varchar(30) not null,
    primary key (id)
) engine=innodb;
```

> [!NOTE]
> Tất cả các từ khóa SQL và kiểu dữ liệu ở trên đều được viết ở dạng **chữ thường (lowercase)** tuân theo quy tắc thiết kế hệ thống.

---

## 3. Đặc Tả Dữ Liệu & Ràng Buộc Validation

Khi nhận yêu cầu tạo mới hoặc cập nhật thông tin ứng viên, hệ thống sẽ thực hiện kiểm tra các ràng buộc sau:

| Trường Dữ Liệu | Kiểu Dữ Liệu | Ràng Buộc Validation | Mô Tả |
| :--- | :--- | :--- | :--- |
| `fullName` | String | Không trống (`@NotBlank`), tối đa 100 ký tự | Họ và tên đầy đủ |
| `age` | Integer | `@NotNull`, từ 18 (`@Min`) đến 60 (`@Max`) | Tuổi của ứng viên |
| `height` | Double | `@NotNull`, tối thiểu 150.0 cm (`@DecimalMin`) | Chiều cao (cm) |
| `weight` | Double | `@NotNull`, tối thiểu 45.0 kg (`@DecimalMin`) | Cân nặng (kg) |
| `nationality` | String | Không trống (`@NotBlank`), tối đa 50 ký tự | Quốc tịch |
| `bio` | String | Tối đa 1000 ký tự | Tiểu sử tóm tắt |
| `photoUrl` | String | Không có | Đường dẫn ảnh ứng viên |
| `score` | Double | Tối thiểu 0 (`@Min`), tối đa 100 (`@Max`) | Điểm số đánh giá hoặc số phiếu bầu |
| `status` | Enum | Không null (`@NotNull`), giá trị: `ACTIVE`, `RUNNER_UP`, `WINNER`, `ELIMINATED` | Trạng thái thi đấu |

---

## 4. Đặc Tả Các Điểm Cuối REST API (API Specifications)

### 4.1. Lấy Danh Sách Tất Cả Ứng Viên
- **Method**: `GET`
- **Endpoint**: `/api/candidates`
- **Mô tả**: Trả về toàn bộ danh sách ứng viên trong hệ thống.
- **Phản hồi Thành Công (`200 OK`)**:
  ```json
  [
    {
      "id": 1,
      "fullName": "Nguyen Van Nam",
      "age": 24,
      "height": 185.0,
      "weight": 78.0,
      "nationality": "Vietnam",
      "bio": "Mister Vietnam 2024 winner...",
      "photoUrl": "https://example.com/nam.jpg",
      "score": 95.5,
      "status": "WINNER"
    }
  ]
  ```

### 4.2. Lấy Chi Tiết Một Ứng Viên
- **Method**: `GET`
- **Endpoint**: `/api/candidates/{id}`
- **Phản hồi Thành Công (`200 OK`)** hoặc Lỗi (`404 Not Found`).

### 4.3. Tạo Mới Ứng Viên
- **Method**: `POST`
- **Endpoint**: `/api/candidates`
- **Thân Yêu Cầu (Request Body)**:
  ```json
  {
    "fullName": "Alex Chen",
    "age": 25,
    "height": 183.5,
    "weight": 76.0,
    "nationality": "Canada",
    "bio": "Actor and model",
    "photoUrl": "https://example.com/alex.jpg",
    "score": 80.0,
    "status": "ACTIVE"
  }
  ```
- **Phản hồi Thành Công (`201 Created`)** hoặc Lỗi (`400 Bad Request` nếu không vượt qua Validation).

### 4.4. Cập Nhật Toàn Bộ Ứng Viên
- **Method**: `PUT`
- **Endpoint**: `/api/candidates/{id}`
- **Phản hồi Thành Công (`200 OK`)** hoặc Lỗi (`404 Not Found` / `400 Bad Request`).

### 4.5. Xóa Ứng Viên
- **Method**: `DELETE`
- **Endpoint**: `/api/candidates/{id}`
- **Phản hồi Thành Công (`204 No Content`)**.

### 4.6. Tìm Kiếm & Lọc Nâng Cao (Phân Trang & Sắp Xếp)
- **Method**: `GET`
- **Endpoint**: `/api/candidates/search`
- **Tham Số Query (Query Parameters)**:
  - `name`: Từ khóa tìm kiếm theo tên (không phân biệt chữ hoa/thường)
  - `nationality`: Quốc tịch lọc
  - `status`: Trạng thái ứng viên (`ACTIVE`, `WINNER`, v.v.)
  - `minHeight` / `maxHeight`: Khoảng chiều cao lọc
  - `page`: Số trang (mặc định: `0`)
  - `size`: Kích thước trang (mặc định: `10`)
  - `sortBy`: Trường sắp xếp (mặc định: `fullName`)
  - `direction`: Hướng sắp xếp (`asc` hoặc `desc`)
- **Phản hồi Thành Công (`200 OK`)**: Trả về dữ liệu phân trang Spring standard.

### 4.7. Bình Chọn Cho Ứng Viên
- **Method**: `POST`
- **Endpoint**: `/api/candidates/{id}/vote`
- **Tham Số Query**: `points` (Double, mặc định là `1.0`)
- **Mô tả**: Tăng điểm số (`score`) của ứng viên thêm số điểm chỉ định.
- **Phản hồi Thành Công (`200 OK`)**: Trả về đối tượng ứng viên sau khi được cộng điểm.

### 4.8. Cập Nhật Trạng Thái Thi Đấu
- **Method**: `PATCH`
- **Endpoint**: `/api/candidates/{id}/status`
- **Thân Yêu Cầu**:
  ```json
  {
    "status": "ELIMINATED"
  }
  ```
- **Phản hồi Thành Công (`200 OK`)**.

### 4.9. Thống Kê Tổng Quan (Dashboard Statistics)
- **Method**: `GET`
- **Endpoint**: `/api/candidates/stats`
- **Mô tả**: Trả về các chỉ số thống kê của cuộc thi.
- **Phản hồi Thành Công (`200 OK`)**:
  ```json
  {
    "totalCandidates": 6,
    "averageAge": 25.1,
    "averageHeight": 184.2,
    "averageWeight": 77.9,
    "maxScore": 97.0
  }
  ```

---

## 5. Thiết Kế Quản Lý Lỗi (Error Handling)

Tất cả các lỗi ngoại lệ (như 404 hoặc lỗi xác thực 400) sẽ trả về dạng JSON đồng nhất:
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-07-01T16:10:00",
  "errors": {
    "age": "Candidate must be at least 18 years old",
    "fullName": "Full name is required"
  }
}
```
