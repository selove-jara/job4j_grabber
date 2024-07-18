CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
	link TEXT NOT NULL UNIQUE,
    text TEXT,
 created TIMESTAMP
);