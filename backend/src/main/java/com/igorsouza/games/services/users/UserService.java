package com.igorsouza.games.services.users;

import com.igorsouza.games.dtos.auth.NewUser;
import com.igorsouza.games.dtos.searches.UserGameSearch;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.SetUserRoles;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.dtos.users.UserData;
import com.igorsouza.games.enums.GamePlatform;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserData> getAllUsers();
    List<User> getUsersWithVerifiedEmailAndEnabledNotifications();
    User getUserByEmail(String email) throws NotFoundException;
    User getUserById(UUID id) throws NotFoundException;
    User getAuthenticatedUser() throws UnauthorizedException;
    List<UserGameSearch> getAuthenticatedUserSearches() throws UnauthorizedException;
    User createUser(NewUser newUser) throws ConflictException;
    void verifyUserEmail(User user);
    void updateAuthenticatedUser(UpdateUser user) throws UnauthorizedException, ConflictException;
    boolean toggleNotifications() throws UnauthorizedException;
    void setUserRoles(SetUserRoles userRoles) throws BadRequestException, NotFoundException;
    void changeUserPassword(ChangePassword passwords) throws BadRequestException, UnauthorizedException;
    void deleteAuthenticatedUser() throws UnauthorizedException;
    void createSuperAdmin(NewUser superAdmin);
    void saveUserGameSearch(String gameName, GamePlatform platform) throws UnauthorizedException;
}
