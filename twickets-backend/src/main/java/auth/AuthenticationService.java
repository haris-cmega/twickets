package auth;

import dto.RefreshTokenRequestDto;
import entity.Role;
import entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;
import service.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        LocalDateTime refreshTokenExpiry = jwtService.getExpirationDateFromToken(refreshToken);

        // Set refresh token in user entity
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);

        repository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry(jwtService.getExpirationDateFromToken(accessToken))
                .build();
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        LocalDateTime refreshTokenExpiry = jwtService.getExpirationDateFromToken(refreshToken);

        // Update refresh token in database
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        repository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiry(jwtService.getExpirationDateFromToken(accessToken))
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequestDto request) {
        // Find user by refresh token
        User user = repository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Validate the refresh token
        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        LocalDateTime refreshTokenExpiry = jwtService.getExpirationDateFromToken(newRefreshToken);

        // Update refresh token in database
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(refreshTokenExpiry);
        repository.save(user);

        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .accessTokenExpiry(jwtService.getExpirationDateFromToken(newAccessToken))
                .build();
    }

    public void logout(String refreshToken) {
        User user = repository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Clear refresh token
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        repository.save(user);
    }
}