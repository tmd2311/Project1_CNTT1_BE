package com.proshop.exceptionlib.enums;

import org.springframework.http.HttpStatus;

public enum ResErrorCode {

    // ===== PERMISSION / AUTH =====
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "PMS01", "Quyền bị từ chối", "Permission Denied"),
    PERMISSION_DENIED_REMOVE_ACCOUNT(HttpStatus.UNAUTHORIZED, "PMS02",
        "Liên hệ với người quản lý để thực hiện chức năng này", "Permission Denied"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "PMS03",
        "Thông tin xác thực bị thiếu hoặc không chính xác", "Credentials are missing or incorrect"),
    INVALID_USER_PASS(HttpStatus.UNAUTHORIZED, "PMS04",
        "Mật khẩu không đúng", "The password is incorrect"),
    USER_INACTIVE(HttpStatus.UNAUTHORIZED, "PMS05",
        "Tài khoản đã tạm khóa, vui lòng đăng nhập lại", "The account is temporarily locked"),

    // ===== GENERAL ERROR =====
    GENERAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GEN01", "Thất bại",
        "An error occurred, please try again in a few minutes"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "GEN02", "Truyền sai tham số", "Passing wrong parameters"),
    ENTITY_NOT_EXISTS(HttpStatus.NOT_FOUND, "GEN03", "Thực thể không tồn tại",
        "Entity does not exist"),
    ENTITY_EXISTED(HttpStatus.BAD_REQUEST, "GEN04", "Thực thể đã tồn tại", "Entity already exists"),
    MISS_PARAM(HttpStatus.BAD_REQUEST, "GEN05", "Truyền thiếu tham số", "Missing parameters"),
    ENTITY_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "GEN06", "Thực thể dừng hoạt động", "Entity not active"),
    DATE_NOT_VALID(HttpStatus.BAD_REQUEST, "GEN07", "Thời gian không hợp lệ", "Invalid input time"),
    FIELD_REQUIRED(HttpStatus.BAD_REQUEST, "GN004", "Trường bắt buộc không được để trống", "Required field is missing"),

    // ===== TOKEN =====
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "TOK01",
        "Không được đặt mật khẩu giống mật khẩu cũ", "Cannot reuse old password"),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOK02", "Mã truy cập hết hạn", "Expired access code"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "TOK03", "Không tìm thấy mã truy cập", "Access code not found"),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "TOK04", "Mã truy cập không hợp lệ", "Invalid access code"),
    TOKEN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "TOK05",
        "Thu hồi token thất bại", "Failed to revoke token"),

    // ===== OTP =====
    CANT_SEND_OTP(HttpStatus.SERVICE_UNAVAILABLE, "OTP01", "Lỗi khi gửi OTP", "Error sending OTP"),
    OTP_REFERENCE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "OTP02", "Không tìm thấy mã tham chiếu OTP",
        "OTP reference not found"),
    ERROR_SEND_OTP(HttpStatus.BAD_REQUEST, "OTP03", "Gửi OTP không thành công",
        "Send OTP failed"),
    OTP_REFERENCE_ID_IS_REQUIRED(HttpStatus.BAD_REQUEST, "OTP04", "Thiếu mã tham chiếu OTP",
        "OTP reference is required"),
    MAXIMUM_OTP_GENERATION_REACHED(HttpStatus.BAD_REQUEST, "OTP05", "Đã đạt giới hạn gửi OTP",
        "Max OTP generation reached"),
    CANT_INSTANT_REGENERATE_OTP_AFTER_CREATE(HttpStatus.BAD_REQUEST, "OTP06",
        "Vui lòng không gửi lại quá nhanh", "Too many OTP requests"),
    OTP_USED(HttpStatus.BAD_REQUEST, "OTP07", "Mã OTP đã được sử dụng", "OTP already used"),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, "OTP08", "Mã OTP đã hết hạn", "OTP expired"),
    INVALID_OTP_REGISTER_REQUEST(HttpStatus.BAD_REQUEST, "OTP09", "Yêu cầu số điện thoại khi đăng ký",
        "Phone number required when registering"),
    INVALID_OTP_TYPE(HttpStatus.BAD_REQUEST, "OTP10", "Kiểu OTP không hợp lệ", "Invalid OTP type"),
    INVALID_MOBILE_NUMBER_FORMAT(HttpStatus.BAD_REQUEST, "OTP11", "Định dạng số điện thoại không hợp lệ",
        "Invalid phone number format"),
    OTP_VERIFICATION_FAIL(HttpStatus.BAD_REQUEST, "OTP12", "Xác minh OTP thất bại",
        "OTP verification failed"),
    MOBILE_REGISTERED(HttpStatus.BAD_REQUEST, "OTP13", "Số điện thoại đã được đăng ký",
        "Phone number already registered"),
    EMAIL_REGISTERED(HttpStatus.BAD_REQUEST, "OTP14", "Email đã được đăng ký",
        "Email already registered"),
    MOBILE_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "OTP15", "Số điện thoại chưa được đăng ký",
        "Phone number not registered"),
    EMAIL_NOT_REGISTERED(HttpStatus.BAD_REQUEST, "OTP16", "Email chưa được đăng ký",
        "Email not registered"),
    MAXIMUM_OTP_VERIFICATION_REACHED(HttpStatus.BAD_REQUEST, "OTP17", "Đã đạt giới hạn xác minh OTP",
        "Max OTP verification attempts reached"),

    // ===== ACCOUNT =====
    ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN, "ACC01", "Tài khoản đã bị khóa", "Account blocked"),
    ACCOUNT_DELETED(HttpStatus.BAD_REQUEST, "ACC02", "Tài khoản đã bị xóa", "Account deleted"),
    MOBILE_BLOCKED(HttpStatus.FORBIDDEN, "ACC03",
        "Số điện thoại bị chặn do yêu cầu quá nhiều OTP", "Mobile blocked"),

    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACC04", "Mật khẩu không hợp lệ", "Invalid password"),
    PASS_NOT_EQUAL(HttpStatus.BAD_REQUEST, "ACC05", "Mật khẩu mới trùng mật khẩu cũ",
        "New password must differ from old password"),
    OLD_PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "ACC06", "Mật khẩu cũ không đúng",
        "Old password is incorrect"),

    // ===== USER =====
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USR01",
        "Không tìm thấy người dùng", "User not found"),
    USER_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "USR02",
        "Người dùng không hoạt động", "User not active"),
    USER_DELETED(HttpStatus.BAD_REQUEST, "USR03",
        "Người dùng đã bị xóa", "User deleted"),

    // ===== DATA FORMAT =====
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "FMT01", "Định dạng email không hợp lệ",
        "Invalid email format"),
    INVALID_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "FMT02", "Định dạng ảnh không hợp lệ",
        "Invalid image type"),
    INVALID_DATA_FORMAT(HttpStatus.BAD_REQUEST, "FMT03", "Định dạng dữ liệu không hợp lệ",
        "Invalid data format"),

    // ===== SECURITY =====
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "SEC01",
        "Password không đáp ứng yêu cầu bảo mật", "Weak Password"),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "SEC02",
        "Mật khẩu mới phải khác mật khẩu cũ", "Password Same As Old"),
    ACCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SEC03",
        "Tài khoản đã tồn tại", "Account"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SEC04",
        "Email đã tồn tại", "Email"),

    // --- Product ---
    PRODUCT_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "P001", "Tên sản phẩm không được để trống", "Product name is required"),
    PRODUCT_DESCRIPTION_REQUIRED(HttpStatus.BAD_REQUEST, "P002", "Mô tả sản phẩm không được để trống", "Product description is required"),
    PRODUCT_SPECS_REQUIRED(HttpStatus.BAD_REQUEST, "P003", "Thông số kỹ thuật phải có ít nhất 1 mục", "Product specs is required"),
    PRODUCT_MULTIPLE_PRIMARY_IMAGES(HttpStatus.BAD_REQUEST, "P004","Chỉ được phép có một ảnh chính cho sản phẩm", "Multiple primary images not allowed for product"),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "P005","Ảnh sản phẩm không tồn tại", "Image not found"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P006", "Không tìm thấy sản phẩm", "Product not found"),
    // --- Brand ---
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "P004", "Không tìm thấy thương hiệu", "Brand not found"),
    BRAND_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "B001", "Tên thương hiệu không được để trống", "Brand name is required"),
    BRAND_SLUG_REQUIRED(HttpStatus.BAD_REQUEST, "B002", "Slug thương hiệu không được để trống", "Brand slug is required"),
    BRAND_ALREADY_EXISTS(HttpStatus.CONFLICT, "B004", "Thương hiệu đã tồn tại", "Brand already exists"),
    // --- Category ---
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "P005", "Không tìm thấy danh mục", "Category not found"),
    CATEGORY_PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "C006", "Không tìm thấy danh mục cha", "Parent category not found"),
    CATEGORY_CIRCULAR_REFERENCE(HttpStatus.BAD_REQUEST, "C007", "Không thể tạo tham chiếu vòng lặp", "Cannot create circular reference"),
    CATEGORY_NAME_TOO_SHORT(HttpStatus.BAD_REQUEST, "C008", "Tên danh mục quá ngắn", "Category name too short"),
    CATEGORY_SLUG_TOO_SHORT(HttpStatus.BAD_REQUEST, "C009", "Slug danh mục quá ngắn", "Category slug too short"),
    CATEGORY_SLUG_ALREADY_EXISTS(HttpStatus.CONFLICT, "C010", "Slug danh mục đã tồn tại", "Category slug already exists"),
    CATEGORY_SLUG_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "C011", "Định dạng slug danh mục không hợp lệ", "Category slug format invalid"),
    CATEGORY_MAX_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "C012", "Vượt quá độ sâu phân cấp tối đa", "Maximum hierarchy depth exceeded"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.BAD_REQUEST, "C013", "Không thể xóa danh mục đang chứa sản phẩm", "Cannot delete category that contains products"),
    CATEGORY_HAS_CHILDREN(HttpStatus.BAD_REQUEST, "C014", "Không thể xóa danh mục đang có danh mục con", "Cannot delete category that has subcategories"),
    CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "C015", "Tên danh mục không được để trống", "Category name is required"),
    CATEGORY_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST,"C016","Không tìm thấy ảnh của danh mục", "Category image not found"),
    CATEGORY_MULTIPLE_PRIMARY_IMAGES(HttpStatus.BAD_REQUEST,"C017","Chỉ được phép có 1 ảnh chính cho danh mục","Only one primary image is allowed per category"),

    // ===== ORDER =====
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORD01", "Không tìm thấy đơn hàng", "Order not found"),
    ORDER_ITEMS_REQUIRED(HttpStatus.BAD_REQUEST, "ORD02", "Đơn hàng phải có ít nhất 1 sản phẩm", "Order must have at least 1 item"),
    ORDER_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "ORD03", "Số lượng sản phẩm phải lớn hơn 0", "Product quantity must be greater than 0"),
    ORDER_INVALID_PRICE(HttpStatus.BAD_REQUEST, "ORD04", "Giá sản phẩm không hợp lệ", "Invalid product price"),
    ORDER_INVALID_TOTAL(HttpStatus.BAD_REQUEST, "ORD05", "Tổng tiền đơn hàng phải lớn hơn 0", "Order total must be greater than 0"),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "ORD06", "Bạn không có quyền truy cập đơn hàng này", "Access denied to this order"),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "ORD07", "Chỉ có thể hủy đơn hàng đang chờ xử lý", "Can only cancel pending orders"),
    ORDER_CALCULATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ORD08", "Lỗi khi tính tổng tiền", "Error calculating order total"),
    ORDER_CREATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ORD09", "Lỗi khi tạo đơn hàng", "Error creating order"),

    // ===== CART =====
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART01", "Không tìm thấy người giỏ hàng", "Cart not found"),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "CART02", "Không tìm thấy sản phẩm trong giỏ", "Cart item not found"),
    CART_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "CART03", "Số lượng phải lớn hơn 0", "Quantity must be greater than 0"),

    // ===== PRODUCT CLIENT =====
    PRODUCT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "PRD01", "Không thể kết nối Product Service", "Product service unavailable"),
    PRODUCT_VALIDATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "PRD02", "Lỗi khi kiểm tra sản phẩm", "Error validating product");


    private final HttpStatus status;
    private final String code;
    private final String message;
    private final String label;

    ResErrorCode(HttpStatus status, String code, String message, String label) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.label = label;
    }

    public String code() {
        return code;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }

    public String label() {
        return label;
    }
}
