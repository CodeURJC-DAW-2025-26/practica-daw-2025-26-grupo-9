package es.urjc.daw.equis.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public User register(User user) {

        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (user.getEncodedPassword() == null || user.getEncodedPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String normalizedEmail = normalizeEmail(user.getEmail());
        user.setEmail(normalizedEmail);

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setEncodedPassword(passwordEncoder.encode(user.getEncodedPassword()));

        User savedUser = userRepository.save(user);

        // Enviar email de bienvenida (sin romper registro si falla)
        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            // Loggear el error pero no interrumpir el registro
            System.err.println("Error sending welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(normalizeEmail(email));
    }

 //QUERY METHODS

    @Transactional(readOnly = true)
    public User getByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

//UPDATE PROFILE

    @Transactional
    public User updateProfile(String currentEmail,
                              String name,
                              String surname,
                              String newEmail,
                              String rawPassword,
                              String description,
                              MultipartFile profileImage,
                              MultipartFile coverImage) throws IOException, SQLException {

        User user = getByEmailOrThrow(currentEmail);

        // Basic fields
        user.setName(name);
        user.setSurname(surname);
        user.setDescription(description);

        // Email (only if changed)
        if (newEmail != null && !newEmail.isBlank()) {
            String normalizedEmail = normalizeEmail(newEmail);

            Optional<User> other = userRepository.findByEmail(normalizedEmail);
            if (other.isPresent() && !other.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Email already in use");
            }

            user.setEmail(normalizedEmail);
        }

        // Password (only if provided)
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setEncodedPassword(passwordEncoder.encode(rawPassword));
        }

        // Images (User uses Blob) :contentReference[oaicite:3]{index=3}
        if (profileImage != null && !profileImage.isEmpty()) {
            user.setProfilePicture(bytesToBlob(profileImage.getBytes()));
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            user.setCoverPicture(bytesToBlob(coverImage.getBytes()));
        }

        return userRepository.save(user);
    }

//IMAGE HELPERS

    @Transactional(readOnly = true)
    public byte[] getProfilePictureBytes(Long userId) throws SQLException {
        User user = getByIdOrThrow(userId);
        return blobToBytes(user.getProfilePicture());
    }

    @Transactional(readOnly = true)
    public byte[] getCoverPictureBytes(Long userId) throws SQLException {
        User user = getByIdOrThrow(userId);
        return blobToBytes(user.getCoverPicture());
    }

 //PRIVATE UTILITIES

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private Blob bytesToBlob(byte[] bytes) throws SQLException {
        return new SerialBlob(bytes);
    }

    private byte[] blobToBytes(Blob blob) throws SQLException {
        if (blob == null) return null;
        return blob.getBytes(1, (int) blob.length());
    }
}