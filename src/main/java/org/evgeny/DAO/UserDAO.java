package org.evgeny.DAO;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.Exception.ExceptionDAO;
import org.evgeny.Model.StatusGameFinder;
import org.evgeny.Model.UserModel;
import org.evgeny.Util.GetConnection;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDAO implements IDAO<UserModel, Integer>{

    @Getter
    private static final UserDAO instance = new UserDAO();

    private static final String SQL_CREATE = """
        INSERT INTO game_requests (user_id, game_id, game_name, target_price, status, created_at) 
            VALUES 
            (?,?,?,?,?,?);
    """;

    private static final String SQL_FIND_ALL_BY_ID = """
        SELECT id, user_id, game_id, game_name, target_price, status, created_at
        FROM game_requests
        WHERE user_id = ?;
    """;

    private static final String SQL_DELETE_BY_ID = """
        DELETE FROM game_requests WHERE game_id = ?;
    """;

    @Override
    public boolean create(UserModel obj) {
        try (Connection connection = GetConnection.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE)) {

            preparedStatement.setObject(1, obj.getUser_id());
            preparedStatement.setObject(2, obj.getGame_id());
            preparedStatement.setObject(3, obj.getGame_name());
            preparedStatement.setObject(4, obj.getTarget_price());
            preparedStatement.setObject(5, obj.getStatus().name());
            preparedStatement.setObject(6, obj.getCreated_at());

           return preparedStatement.executeUpdate() > 0;

        }
        catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return false;
            }
            throw new ExceptionDAO("Ошибка добавления продукта Steam в базу данных");
        }
    }

    public Optional<List<UserModel>> findAllByUserId(BigInteger id) {
        try (Connection connection = GetConnection.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_FIND_ALL_BY_ID)) {

            List<UserModel> games = new ArrayList<>();

            preparedStatement.setObject(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                UserModel build = UserModel.builder()
                        .id(resultSet.getLong("id"))
                        .user_id(BigInteger.valueOf(resultSet.getInt("user_id")))
                        .game_id(BigInteger.valueOf(resultSet.getInt("game_id")))
                        .game_name(resultSet.getString("game_name"))
                        .target_price(resultSet.getBigDecimal("target_price"))
                        .status(StatusGameFinder.valueOf(resultSet.getObject("status", String.class)))
                        .created_at(resultSet.getObject("created_at", LocalDate.class))
                        .build();

                games.add(build);
            }
            return Optional.of(games);
        }
        catch (SQLException e) {
            //Найти ошибку, если нет ничего.
            throw new ExceptionDAO("Ошибка вывода данных");
        }
    }

    public boolean deleteById(BigInteger gameId) {
        try (Connection connection = GetConnection.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE_BY_ID)) {

            preparedStatement.setObject(1, gameId);

            return preparedStatement.executeUpdate() > 0;

        }
        catch (SQLException e) {
            if (e.getSQLState().equals("23503")) {
                return false;
            }
            throw new ExceptionDAO("Ошибка удаления записи из базы данных");
        }
    }

    @Override
    public boolean update(UserModel obj) {
        return false;
    }

    @Override
    public boolean delete(UserModel obj) {
        return false;
    }

    @Override
    public UserModel find(Integer id) {
        return null;
    }
}
