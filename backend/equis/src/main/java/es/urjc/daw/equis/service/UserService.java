package es.urjc.daw.equis.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.urjc.daw.equis.model.User;
import es.urjc.daw.equis.repository.UserRepository;
import es.urjc.daw.equis.repository.LikeRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final LikeRepository likeRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService,
            LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public User register(User user,
            MultipartFile profileImage,
            MultipartFile coverImage) throws IOException, SQLException {

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

        // PROFILE IMAGE
        if (profileImage != null && !profileImage.isEmpty()) {
            user.setProfilePicture(bytesToBlob(profileImage.getBytes()));
        } else {
            var is = getClass().getResourceAsStream("/static/assets/images/avatardefault.png");
            if (is == null)
                throw new IllegalStateException("Missing /static/assets/images/avatardefault.png");
            user.setProfilePicture(bytesToBlob(is.readAllBytes()));
        }

        // COVER IMAGE
        if (coverImage != null && !coverImage.isEmpty()) {
            user.setCoverPicture(bytesToBlob(coverImage.getBytes()));
        } else {
            var is = getClass().getResourceAsStream("/static/assets/images/coverdefault.jpg");
            if (is == null)
                throw new IllegalStateException("Missing /static/assets/images/coverdefault.jpg");
            user.setCoverPicture(bytesToBlob(is.readAllBytes()));
        }

        User savedUser = userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(savedUser);
        } catch (Exception e) {
            System.err.println("Error sending welcome email: " + e.getMessage());
        }

        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null)
            return Optional.empty();
        return userRepository.findByEmail(normalizeEmail(email));
    }

    // QUERY METHODS

    @Transactional(readOnly = true)
    public User getByEmailOrThrow(String email) {
        return findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional(readOnly = true)
    public User getByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    // UPDATE PROFILE

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

    // IMAGE HELPERS

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

    // PRIVATE UTILITIES

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private Blob bytesToBlob(byte[] bytes) throws SQLException {
        return new SerialBlob(bytes);
    }

    private byte[] blobToBytes(Blob blob) throws SQLException {
        if (blob == null)
            return null;
        return blob.getBytes(1, (int) blob.length());
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void deleteUser(String email) {

        User user = getByEmailOrThrow(email);
        Long userId = user.getId();

        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteUserById(Long id) {

        likeRepository.deleteByUserId(id);

        userRepository.deleteById(id);
    }

    @Transactional
    public void toggleUserActive(Long userId, String currentAdminEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User currentAdmin = userRepository.findByEmail(currentAdminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Prevent the admin from blocking themselves
        if (user.getId().equals(currentAdmin.getId())) {
            throw new IllegalArgumentException("No puedes bloquearte a ti mismo");
        }

        user.setActive(!user.isActive());

        userRepository.save(user);
    }

}