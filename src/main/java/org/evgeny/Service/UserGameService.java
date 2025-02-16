package org.evgeny.Service;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.evgeny.DAO.UserDAO;
import org.evgeny.Model.UserModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserGameService {
    @Getter
    private static final UserGameService instance = new UserGameService();

    public final UserDAO userDAO = UserDAO.getInstance();

    public boolean addGame(UserModel userModel) {
        return userDAO.create(userModel);
    }


}
