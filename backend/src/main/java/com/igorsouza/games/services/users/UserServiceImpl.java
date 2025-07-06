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
import com.igorsouza.games.models.Role;
import com.igorsouza.games.models.User;
import com.igorsouza.games.repositories.UserRepository;
import com.igorsouza.games.services.roles.RoleService;
import com.igorsouza.games.services.search.UserGameSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final RoleService roleService;
    private final UserGameSearchService userGameSearchService;

    @Override
    public List<UserData> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserData(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.isEmailVerified(),
                        user.getRoles().stream().map(Role::getName).toList())
                ).toList();
    }

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
    public User getUserById(UUID id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User not found."));
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
    public List<UserGameSearch> getAuthenticatedUserSearches() throws UnauthorizedException {
        User authenticatedUser = getAuthenticatedUser();
        List<com.igorsouza.games.models.UserGameSearch> userSearches =
                userGameSearchService.getUserSearches(authenticatedUser.getId());

        return userSearches.stream().map(search ->
            new UserGameSearch(search.getGameName(), search.getPlatform(), search.getCreatedAt())
        ).toList();
    }

    @Override
    public User createUser(NewUser newUser) throws ConflictException {
        Optional<User> existingUser = userRepository.findByEmail(newUser.getEmail());

        if (existingUser.isPresent()) {
            throw new ConflictException("Email already exists.");
        }

        try {
            Role userRole = roleService.getRoleByName("USER");
            User user = User.builder()
                    .name(newUser.getName())
                    .email(newUser.getEmail())
                    .password(passwordEncoder.encode(newUser.getPassword()))
                    .roles(List.of(userRole))
                    .build();

            return userRepository.save(user);
        } catch (NotFoundException e) {
            throw new RuntimeException("User role does not exist.");
        }
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
    public void setUserRoles(SetUserRoles userRoles) throws BadRequestException, NotFoundException {
        if (userRoles.getRoles().stream().anyMatch(role -> role.equals("SUPER_ADMIN"))) {
            throw new BadRequestException("Cannot assign SUPER_ADMIN role directly.");
        }

        User user = getUserById(userRoles.getUserId());
        boolean userIsSuperAdmin = user.getAuthorities().stream().anyMatch(role -> role.getName().equals("SUPER_ADMIN"));

        if (userIsSuperAdmin) {
            throw new BadRequestException("Cannot change roles of a SUPER_ADMIN user.");
        }

        List<Role> roles = new ArrayList<>();

        for (String roleName : userRoles.getRoles()) {
            Role role = roleService.getRoleByName(roleName);
            roles.add(role);
        }

        user.setRoles(roles);
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

    @Override
    public void createSuperAdmin(NewUser superAdmin) {
        try {
            User createdUser = createUser(superAdmin);
            Role superAdminRole = roleService.getRoleByName("SUPER_ADMIN");
            Role userRole = roleService.getRoleByName("USER");

            createdUser.setRoles(List.of(superAdminRole, userRole));
            userRepository.save(createdUser);
        } catch (ConflictException e) {
            log.info("User with email {} already exists. Skipping creation.", superAdmin.getEmail());
        } catch (NotFoundException e) {
            log.error("Admin role does not exist. Cannot assign role to user.");
        }
    }

    public void saveUserGameSearch(String gameName, GamePlatform platform) throws UnauthorizedException {
        userGameSearchService.saveSearch(gameName, platform, getAuthenticatedUser());
    }

    public UUID getAuthenticatedUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
