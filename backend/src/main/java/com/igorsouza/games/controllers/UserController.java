package com.igorsouza.games.controllers;

import com.igorsouza.games.dtos.searches.UserGameSearch;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.SetUserRoles;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.dtos.users.UserData;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.NotFoundException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.services.users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<UserData>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/searches")
    public ResponseEntity<List<UserGameSearch>> getUserSearches() throws UnauthorizedException {
        return ResponseEntity.ok(userService.getAuthenticatedUserSearches());
    }

    @PutMapping
    public ResponseEntity<String> updateUser(@RequestBody UpdateUser user) throws UnauthorizedException, ConflictException {
        userService.updateAuthenticatedUser(user);
        return ResponseEntity.ok("User successfully updated.");
    }

    @PutMapping("/toggle-notifications")
    public ResponseEntity<String> toggleNotifications() throws UnauthorizedException {
        boolean notificationsEnabled = userService.toggleNotifications();
        return ResponseEntity.ok("Notifications successfully " + (notificationsEnabled ? "enabled" : "disabled") + ".");
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePassword passwords)
            throws BadRequestException, UnauthorizedException {
        userService.changeUserPassword(passwords);
        return ResponseEntity.ok("Password successfully changed.");
    }

    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @PutMapping("/roles")
    public ResponseEntity<String> changeUserRole(@RequestBody SetUserRoles userRoles)
            throws BadRequestException, NotFoundException {
        userService.setUserRoles(userRoles);
        return ResponseEntity.ok("User roles successfully set.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUser() throws UnauthorizedException {
        userService.deleteAuthenticatedUser();
        return ResponseEntity.ok("User successfully deleted.");
    }
}
