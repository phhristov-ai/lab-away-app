-- Canonical initial schema for lab-away.
-- Intended for a new, empty MySQL 8 database.
-- UUID values for BINARY(16) columns must be stored consistently by the application.

CREATE TABLE categories (
    id BINARY(16) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE products (
    id BINARY(16) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    stock INT NULL,
    price DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    banner_url VARCHAR(512) NULL,
    banner_type VARCHAR(20) NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_products_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_products_active ON products (active);

CREATE TABLE blogs (
    id BINARY(16) NOT NULL,
    slug VARCHAR(255) NOT NULL,
    author VARCHAR(255) NULL,
    image_url_small VARCHAR(255) NULL,
    image_url_medium VARCHAR(255) NULL,
    image_url_large VARCHAR(255) NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uniq_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE category_translations (
    id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    language VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_category_translations_category_language UNIQUE (category_id, language),
    CONSTRAINT fk_category_translations_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE product_translations (
    id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    language VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uniq_product_lang UNIQUE (product_id, language),
    CONSTRAINT fk_product_translations_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE blog_translations (
    id BINARY(16) NOT NULL,
    blog_id BINARY(16) NOT NULL,
    language VARCHAR(255) NOT NULL,
    reading_time INT NULL,
    title VARCHAR(255) NULL,
    content TEXT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_blog_translations_blog_language UNIQUE (blog_id, language),
    CONSTRAINT fk_blog_translations_blog
        FOREIGN KEY (blog_id) REFERENCES blogs (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE product_images (
    id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    image_url_small VARCHAR(255) NOT NULL,
    image_url_medium VARCHAR(255) NULL,
    image_url_large VARCHAR(255) NULL,
    position INT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_product_images_product ON product_images (product_id);

CREATE TABLE product_categories (
    product_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    PRIMARY KEY (product_id, category_id),
    CONSTRAINT fk_product_categories_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE,
    CONSTRAINT fk_product_categories_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE blog_categories (
    blog_id BINARY(16) NOT NULL,
    category_id BINARY(16) NOT NULL,
    PRIMARY KEY (blog_id, category_id),
    CONSTRAINT fk_blog_categories_blog
        FOREIGN KEY (blog_id) REFERENCES blogs (id) ON DELETE CASCADE,
    CONSTRAINT fk_blog_categories_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE orders (
    id BINARY(16) NOT NULL,
    order_number VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NULL,
    shipping_first_name VARCHAR(255) NOT NULL,
    shipping_last_name VARCHAR(255) NOT NULL,
    shipping_country VARCHAR(255) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    shipping_city VARCHAR(255) NOT NULL,
    shipping_post_code VARCHAR(255) NOT NULL,
    shipping_phone VARCHAR(255) NOT NULL,
    billing_first_name VARCHAR(255) NOT NULL,
    billing_last_name VARCHAR(255) NOT NULL,
    billing_country VARCHAR(255) NOT NULL,
    billing_address VARCHAR(255) NOT NULL,
    billing_city VARCHAR(255) NOT NULL,
    billing_post_code VARCHAR(255) NOT NULL,
    billing_phone VARCHAR(255) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    status VARCHAR(255) NULL,
    stripe_session_id VARCHAR(255) NULL,
    paypal_order_id VARCHAR(255) NULL,
    paypal_capture_id VARCHAR(255) NULL,
    payment_provider VARCHAR(255) NOT NULL,
    language VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_orders_order_number UNIQUE (order_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE order_items (
    id BINARY(16) NOT NULL,
    order_id BINARY(16) NOT NULL,
    product_id BINARY(16) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE admin_users (
    id BINARY(16) NOT NULL,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_admin_users_username UNIQUE (username),
    CONSTRAINT uq_admin_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE newsletter_subscribers (
    id BINARY(16) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subscribed_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uq_newsletter_subscribers_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- Preview views. Create these only after all referenced tables exist.

CREATE OR REPLACE VIEW product_preview_view AS
SELECT
    p.id AS id,
    pt.language AS language,
    pt.name AS name,
    p.slug AS slug,
    p.price AS price,
    p.active AS active,
    (
        SELECT pi.image_url_small
        FROM product_images pi
        WHERE pi.product_id = p.id
        ORDER BY pi.position, pi.id
        LIMIT 1
    ) AS imageUrlSmall,
    (
        SELECT pi.image_url_medium
        FROM product_images pi
        WHERE pi.product_id = p.id
        ORDER BY pi.position, pi.id
        LIMIT 1
    ) AS imageUrlMedium,
    (
        SELECT pi.image_url_large
        FROM product_images pi
        WHERE pi.product_id = p.id
        ORDER BY pi.position, pi.id
        LIMIT 1
    ) AS imageUrlLarge,
    (
        SELECT CONCAT(
            '[',
            GROUP_CONCAT(
                DISTINCT CONCAT(
                    '{"name":"',
                    REPLACE(REPLACE(REPLACE(ct.name, '\\', '\\\\'), '"', '\\"'), CHAR(10), '\\n'),
                    '","slug":"',
                    REPLACE(REPLACE(REPLACE(c.slug, '\\', '\\\\'), '"', '\\"'), CHAR(10), '\\n'),
                    '"}'
                )
                ORDER BY ct.name ASC
                SEPARATOR ','
            ),
            ']'
        )
        FROM product_categories pc
        JOIN categories c ON c.id = pc.category_id
        JOIN category_translations ct
          ON ct.category_id = c.id
         AND ct.language = pt.language
        WHERE pc.product_id = p.id
    ) AS categories
FROM products p
JOIN product_translations pt ON pt.product_id = p.id;

CREATE OR REPLACE VIEW blog_preview_view AS
SELECT
    b.slug AS slug,
    b.author AS author,
    b.image_url_small AS imageUrlSmall,
    b.image_url_medium AS imageUrlMedium,
    b.image_url_large AS imageUrlLarge,
    bt.title AS title,
    bt.content AS excerpt,
    bt.reading_time AS readingTime,
    bt.language AS language,
    b.created_at AS createdAt,
    b.updated_at AS updatedAt,
    (
        SELECT CONCAT(
            '[',
            GROUP_CONCAT(
                DISTINCT CONCAT(
                    '{"name":"',
                    REPLACE(REPLACE(REPLACE(ct.name, '\\', '\\\\'), '"', '\\"'), CHAR(10), '\\n'),
                    '","slug":"',
                    REPLACE(REPLACE(REPLACE(c.slug, '\\', '\\\\'), '"', '\\"'), CHAR(10), '\\n'),
                    '"}'
                )
                ORDER BY ct.name ASC
                SEPARATOR ','
            ),
            ']'
        )
        FROM blog_categories bc
        JOIN categories c ON c.id = bc.category_id
        JOIN category_translations ct
          ON ct.category_id = c.id
         AND ct.language = bt.language
        WHERE bc.blog_id = b.id
    ) AS categories
FROM blogs b
JOIN blog_translations bt ON bt.blog_id = b.id;
