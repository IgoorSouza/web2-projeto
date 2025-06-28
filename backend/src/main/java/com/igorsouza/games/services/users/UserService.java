package com.igorsouza.games.services.users;

import com.igorsouza.games.dtos.auth.NewUser;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.User;

import java.util.List;

public interface UserService {
    List<User> getUsersWithVerifiedEmailAndEnabledNotifications();
    User getUserByEmail(String email) throws NotFoundException;
    User getAuthenticatedUser() throws UnauthorizedException;
    void createUser(NewUser newUser) throws ConflictException;
    void verifyUserEmail(User user);
    void updateAuthenticatedUser(UpdateUser user) throws UnauthorizedException, ConflictException;
    boolean toggleNotifications() throws UnauthorizedException;
    void changeUserPassword(ChangePassword passwords) throws BadRequestException, UnauthorizedException;
    void deleteAuthenticatedUser() throws UnauthorizedException;
}
