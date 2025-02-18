package org.evgeny.Service;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.DAO.UserDAO;
import org.evgeny.Model.UserModel;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGameService {
    @Getter
    private static final UserGameService instance = new UserGameService();

    public final UserDAO userDAO = UserDAO.getInstance();

    public boolean addGame(UserModel userModel) {
        return userDAO.create(userModel);
    }

    public Optional<List<UserModel>> getAllUsersGames(BigInteger id) {
        return userDAO.findAllByUserId(id);
    }

    public boolean getDeleteById(BigInteger id) {
        return userDAO.deleteById(id);
    }


}
