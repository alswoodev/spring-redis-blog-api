CREATE TABLE files (
    id SERIAL PRIMARY KEY,
    path VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(45) NOT NULL,
    extension VARCHAR(20) NOT NULL,
    post_id INTEGER NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE INDEX idx_files_post_id ON files(post_id);
