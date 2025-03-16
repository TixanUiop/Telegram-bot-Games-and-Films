package org.evgeny.DAO;

import lombok.SneakyThrows;
import org.evgeny.Model.StatusGameFinder;
import org.evgeny.Model.UserModel;
import org.evgeny.Util.GetConnection;
import org.evgeny.Util.GetProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
class UserDAOTest {

    private static final UserDAO dao = UserDAO.getInstance();

    @SneakyThrows
    @BeforeEach
    void setUp() {
        String SQL_DROP_BEFORE_CALL = """
                    DROP TABLE IF EXISTS game_requests;
                """;
        String SQL_CREATE_BEFORE_CALL = """
                    CREATE TABLE IF NOT EXISTS game_requests (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           game_id BIGINT NOT NULL,
                           game_name VARCHAR(255) NOT NULL,
                           target_price DECIMAL(10,2) NOT NULL,
                           status VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP
                    )
                """;

        try (Connection connection = GetConnection.get();
             Statement statement = connection.createStatement())
        {
            statement.execute(SQL_DROP_BEFORE_CALL);
            statement.execute(SQL_CREATE_BEFORE_CALL);
        }

    }


    @Test
    void create() {
        UserModel userModel = getUserModel();
        boolean result = dao.create(userModel);
        assertTrue(result);
    }

    @Test
    void findAllByUserId() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void find() {
    }

    @Test
    void getInstance() {
    }
    long id;
    BigInteger user_id;
    BigInteger game_id;
    String game_name;
    BigDecimal target_price;
    StatusGameFinder status;
    LocalDate created_at;

    private UserModel getUserModel() {
        return UserModel.builder()
                .user_id(BigInteger.valueOf(1))
                .game_id(BigInteger.valueOf(100))
                .game_name("test")
                .target_price(BigDecimal.valueOf(100))
                .status(StatusGameFinder.waiting)
                .created_at(LocalDate.now())
                .build();
    }
}