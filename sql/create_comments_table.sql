CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    contents VARCHAR(300) NOT NULL,
    user_id BIGINT,
    sub_comment_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_postId FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_userId FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_sub_comment_id FOREIGN KEY (sub_comment_id) REFERENCES comments(id) ON DELETE SET NULL
);