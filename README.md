# Bài tập 9009 — RBAC tại Product Service

Product Service dùng danh sách sản phẩm tĩnh, không dùng database. JWT được ký bởi Identity Service của [bài 9008](https://github.com/pgtrunghd/identity-service-homework-9008). Hai service phải dùng cùng `JWT_SECRET`.

## Chạy

`./gradlew bootRun` chạy Product Service ở cổng 8081. Đặt `JWT_SECRET` riêng trong môi trường triển khai. Không dùng secret mặc định trong production.

## API

- `GET /api/products`: yêu cầu Bearer Access Token hợp lệ; USER và ADMIN đều xem được.
- `DELETE /api/products/{id}`: yêu cầu Role ADMIN; USER nhận 403.

Đăng ký user bằng `POST /api/auth/register`, đăng nhập bằng `POST /api/auth/login` tại Identity Service, rồi truyền `accessToken` vào header `Authorization: Bearer <token>`. Token của bài 9008 chứa claim `roles` dạng danh sách, ví dụ `["USER"]`. Token thiếu, sai chữ ký, hết hạn hoặc claim không hợp lệ nhận 401.

Chạy `./gradlew test` để kiểm tra các trường hợp USER bị từ chối xóa, ADMIN xóa thành công, không có token, token sai chữ ký và token hết hạn.
