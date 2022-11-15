package ru.job4j.grabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.grabber.utils.Post;
import ru.job4j.grabber.utils.Store;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class.getName());
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver"));
            String url = cfg.getProperty("url");
            String login = cfg.getProperty("login");
            String password = cfg.getProperty("password");
            cnn = DriverManager.getConnection(url, login, password);
        } catch (Exception e) {
            LOG.error("Configuration error", e);
        }
    }

    @Override
    public void save(Post post) {
        Timestamp timestampFromLDT = Timestamp.valueOf(post.getCreated());
        try (PreparedStatement ps = cnn.prepareStatement("INSERT INTO post"
                + "(name, text, link, created) values(?, ?, ?, ?)"
                + "ON CONFLICT (link) DO NOTHING")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, timestampFromLDT);
            ps.execute();
        } catch (SQLException e) {
            LOG.error("Exception in data retention", e);
        }

    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    postList.add(createPost(resultSet));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in getting a list", e);
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cnn.prepareStatement("select * from post where id = ?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    post = createPost(resultSet);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception in getting an object by id", e);
        }
        return post;
    }

    private Post createPost(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("name");
        String link = rs.getString("link");
        String description = rs.getString("text");
        LocalDateTime ldt = rs.getTimestamp("created").toLocalDateTime();
        return new Post(id, title, link, description, ldt);
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
