package ru.job4j.grabber;

import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {
    private Connection connection;

    public PsqlStore(Properties config) throws SQLException {
        try {
            Class.forName(config.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        connection = DriverManager.getConnection(
                config.getProperty("jdbc.url"),
                config.getProperty("jdbc.username"),
                config.getProperty("jdbc.password"));
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO post (title, link, text , created) VALUES (?, ?, ?, ?) ON CONFLICT(link) DO NOTHING")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT * FROM post")) {
            while (resultSet.next()) {
                posts.add(new Post(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("link"),
                        resultSet.getString("text"),
                        resultSet.getTimestamp("created").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM post WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("title"),
                            resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Properties config;
        try (InputStream input = PsqlStore.class.getClassLoader().getResourceAsStream("app.properties")) {
            config = new Properties();
            config.load(input);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        HabrCareerParse parser = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> posts = parser.list("https://career.habr.com");
        try {
            PsqlStore store = new PsqlStore(config);
            posts.forEach(store::save);
            store.getAll().forEach(System.out::println);
            System.out.println(store.findById(5));
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}