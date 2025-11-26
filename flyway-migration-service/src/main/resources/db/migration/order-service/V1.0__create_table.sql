
-- Table: cart
CREATE TABLE cart (
                      cart_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      user_id BIGINT NOT NULL,
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP
);

-- Table: cart_item
CREATE TABLE cart_item (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           cart_id UUID NOT NULL REFERENCES cart(cart_id) ON DELETE CASCADE,
                           product_id UUID NOT NULL,
                           quantity INT NOT NULL,
                           created_at TIMESTAMP,
                           updated_at TIMESTAMP
);

-- Table: orders
CREATE TABLE orders (
                        order_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id BIGINT NOT NULL,
                        total_amount NUMERIC(100,2) NOT NULL,
                        shipping_address TEXT,
                        status VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP
);

-- Table: order_items
CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                             product_id UUID NOT NULL,
                             sku_id UUID NOT NULL,
                             quantity INT NOT NULL,
                             price NUMERIC(100,2) NOT NULL,
                             subtotal NUMERIC(100,2) NOT NULL,
                             created_at TIMESTAMP
);

-- Table: payment
CREATE TABLE payment (
                         payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         order_id UUID NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE,
                         method VARCHAR(50),
                         status VARCHAR(50),
                         amount NUMERIC(100,2) NOT NULL,
                         paid_at TIMESTAMP
);

-- Enum-like constraints cho orders.status
ALTER TABLE orders
    ADD CONSTRAINT orders_status_check
        CHECK (status IN ('COMPLETED', 'PENDING', 'PROCESSING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED', 'RETURNED'));

-- Enum-like constraints cho payment.method
ALTER TABLE payment
    ADD CONSTRAINT payment_method_check
        CHECK (method IN ('COD','MOMO','ZALOPAY','VNPAY','CREDIT_CARD','PAYPAL','BANK_TRANSFER','CASH'));

-- Enum-like constraints cho payment.status
ALTER TABLE payment
    ADD CONSTRAINT payment_status_check
        CHECK (status IN ('PENDING','PAID','FAILED','REFUNDED','PROCESSING','CANCELLED'));
