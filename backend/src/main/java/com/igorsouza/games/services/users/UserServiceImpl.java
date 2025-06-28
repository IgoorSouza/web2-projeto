package com.igorsouza.games.services.users;

import com.igorsouza.games.dtos.auth.NewUser;
import com.igorsouza.games.dtos.users.ChangePassword;
import com.igorsouza.games.dtos.users.UpdateUser;
import com.igorsouza.games.exceptions.BadRequestException;
import com.igorsouza.games.exceptions.ConflictException;
import com.igorsouza.games.exceptions.UnauthorizedException;
import com.igorsouza.games.models.User;
import com.igorsouza.games.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getUsersWithVerifiedEmailAndEnabledNotifications() {
        return userRepository.findAllByEmailVerifiedTrueAndNotificationsEnabledTrue();
    }

    @Override
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found."));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByEmail(username);
    }

    @Override
    public User getAuthenticatedUser() throws UnauthorizedException {
        Optional<User> user = userRepository.findById(getAuthenticatedUserId());

        if (user.isEmpty()) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        return user.get();
    }

    @Override
    public void createUser(NewUser newUser) throws ConflictException {
        Optional<User> existingUser = userRepository.findByEmail(newUser.getEmail());

        if (existingUser.isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        User user = User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Override
    public void updateAuthenticatedUser(UpdateUser user) throws UnauthorizedException, ConflictException {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent() && !existingUser.get().getId().equals(getAuthenticatedUserId())) {
            throw new ConflictException("User already exists.");
        }

        User authenticatedUser = getAuthenticatedUser();
        boolean emailChanged = !authenticatedUser.getEmail().equals(user.getEmail());

        if (emailChanged) {
            authenticatedUser.setEmailVerified(false);
            authenticatedUser.setNotificationsEnabled(false);
        }

        authenticatedUser.setName(user.getName());
        authenticatedUser.setEmail(user.getEmail());
        userRepository.save(authenticatedUser);
    }

    @Override
    public boolean toggleNotifications() throws UnauthorizedException {
        User user = getAuthenticatedUser();

        if (!user.isEmailVerified()) {
            throw new UnauthorizedException("You must verify your email before enabling notifications.");
        }

        user.setNotificationsEnabled(!user.isNotificationsEnabled());
        userRepository.save(user);

        return user.isNotificationsEnabled();
    }

    @Override
    public void changeUserPassword(ChangePassword passwords) throws BadRequestException, UnauthorizedException {
        User user = getAuthenticatedUser();

        if (!passwordEncoder.matches(passwords.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect.");
        }

        if (passwords.getCurrentPassword().equals(passwords.getNewPassword())) {
            throw new BadRequestException("The new password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteAuthenticatedUser() throws UnauthorizedException {
        User user = getAuthenticatedUser();
        userRepository.delete(user);
    }

    @Override
    public void verifyUserEmail(User user) {
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public UUID getAuthenticatedUserId() {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return UUID.fromString(userId);
    }
}
