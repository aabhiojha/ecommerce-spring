package dev.abhishek.ecommerce.modules.auth;

public enum PermissionsEnum {

    // ===== USER =====
    USER_READ_AUTHORITY("user:read"),
    USER_EDIT_AUTHORITY("user:read,user:update"),
    USER_ADMIN_AUTHORITY("user:read,user:update,user:create,user:delete"),

    // ===== PRODUCT =====
    PRODUCT_READ_AUTHORITY("product:read"),
    PRODUCT_EDIT_AUTHORITY("product:read,product:update"),
    PRODUCT_ADMIN_AUTHORITY("product:read,product:update,product:create,product:delete"),

    // ===== CATEGORY =====
    CATEGORY_READ_AUTHORITY("category:read"),
    CATEGORY_EDIT_AUTHORITY("category:read,category:update"),
    CATEGORY_ADMIN_AUTHORITY("category:read,category:update,category:create,category:delete"),


    // ===== CART =====
    CART_READ_AUTHORITY("cart:read"),
    CART_EDIT_AUTHORITY("cart:read,cart:update"),
    CART_ADMIN_AUTHORITY("cart:read,cart:update,cart:delete"),

    // ===== ROLE LEVEL =====
    CUSTOMER("ROLE_CUSTOMER"),
    SELLER("ROLE_SELLER"),
    ADMIN("ROLE_ADMIN");
//        // ===== REVIEW =====
//        REVIEW_READ_AUTHORITY("review:read"),
//        REVIEW_EDIT_AUTHORITY("review:read,review:update"),
//        REVIEW_ADMIN_AUTHORITY("review:read,review:update,review:create,review:delete"),
//        // ===== ORDER =====
//        ORDER_READ_AUTHORITY("order:read"),
//        ORDER_EDIT_AUTHORITY("order:read,order:update"),
//        ORDER_ADMIN_AUTHORITY("order:read,order:update,order:create,order:delete"),
//        // ===== PAYMENT =====
//        PAYMENT_READ_AUTHORITY("payment:read"),
//        PAYMENT_REFUND_AUTHORITY("payment:read,payment:refund"),
//        PAYMENT_ADMIN_AUTHORITY("payment:read,payment:create,payment:refund,payment:delete"),

    private final String permissions;

    PermissionsEnum(String permissions) {
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }


}
