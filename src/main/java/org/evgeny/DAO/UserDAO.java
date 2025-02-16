package org.evgeny.DAO;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.Exception.ExceptionDAO;
import org.evgeny.Model.StatusGameFinder;
import org.evgeny.Model.UserModel;
import org.evgeny.Util.GetConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDAO implements IDAO<UserModel, Integer>{

    @Getter
    private static final UserDAO instance = new UserDAO();

    private static final String SQL_CREATE = """
        INSERT INTO game_requests (user_id, game_id, game_name, target_price, status, created_at) 
            VALUES 
            (?,?,?,?,?,?);
    """;


    @Override
    public boolean create(UserModel obj) {
        try (Connection connection = GetConnection.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_CREATE)) {

            preparedStatement.setObject(1, obj.getUser_id());
            preparedStatement.setObject(2, obj.getGame_id());
            preparedStatement.setObject(3, obj.getGame_name());
            preparedStatement.setObject(4, obj.getTarget_price());
            preparedStatement.setObject(5, obj.getStatus());
            preparedStatement.setObject(6, obj.getCreated_at());

           return preparedStatement.executeUpdate() > 0;

        }
        catch (SQLException e) {
            throw new ExceptionDAO("Ошибка добавления продукта Steam в базу данных");
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
