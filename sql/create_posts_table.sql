CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY, 
    is_admin BOOLEAN DEFAULT FALSE,
    name VARCHAR(50) NOT NULL,
    contents TEXT NOT NULL,
    views INTEGER DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_id INTEGER,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
);

CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_category_id ON posts(category_id);