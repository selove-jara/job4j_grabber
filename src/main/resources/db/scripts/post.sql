CREATE TABLE post (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    text TEXT,
    link TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP
);