package com.teesolutions.ipospu.services;

import com.teesolutions.ipospu.dto.CommercialApplicationDto;
import com.teesolutions.ipospu.models.User;
import com.teesolutions.ipospu.repositories.UserRepository;
import com.teesolutions.ipospu.utils.PasswordUtil;
import com.teesolutions.ipospu.utils.SecurityUtil;

import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    /** 8 digits, 2 letters + 6 digits, or UK + 8 digits + optional suffix (e.g. university sample UK10003429CompH). */
    private static final Pattern COMPANY_REGISTRATION_PATTERN =
            Pattern.compile("^(?:\\d{8}|[A-Z]{2}\\d{6}|UK\\d{8}[A-Za-z]*)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PERSON_NAME_PATTERN =
            Pattern.compile("^[A-Za-z ,.'-]{3,120}$");

    private final UserRepository userRepository = new UserRepository();
    private final CommsService commsService = new CommsService();

    public Optional<User> login(String loginId, String password) {
        if (loginId == null || loginId.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        String trimmed = loginId.trim();
        String hash = PasswordUtil.hash(password);
        Optional<Integer> auth = userRepository.authenticate(trimmed, hash, password);
        if (auth.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findById(auth.get());
    }

    public String registerNonCommercial(String email) {
        String normalizedEmail = requireValidEmail(email);
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        String generatedPassword = SecurityUtil.generateInitialPassword();
        userRepository.createNonCommercialUser(normalizedEmail, PasswordUtil.hash(generatedPassword));
        commsService.sendRegistrationEmail(normalizedEmail, generatedPassword);
        return generatedPassword;
    }

    public void forcePasswordChange(int userId, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("Password must have at least 8 characters");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        userRepository.updatePasswordAndClearFirstLogin(userId, PasswordUtil.hash(newPassword));
    }

    public void incrementCompletedOrders(int userId) {
        userRepository.incrementCompletedOrders(userId);
    }

    public int ensureGuestCheckoutUser() {
        String generatedSecret = PasswordUtil.hash("guest-checkout::" + SecurityUtil.generateInitialPassword());
        return userRepository.ensureGuestCheckoutUser(generatedSecret);
    }

    public String requireValidEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Please provide a valid email address");
        }
        String normalizedEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(normalizedEmail).matches()) {
            throw new IllegalArgumentException("Please provide a valid email address");
        }
        return normalizedEmail;
    }

    public CommercialApplicationDto validateCommercialApplication(
            String companyRegistrationNumber,
            String directorName,
            String businessType,
            String address,
            String email
    ) {
        String normalizedEmail = requireValidEmail(email);
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "This email already has a member account. Commercial applications must use a different email address."
            );
        }
        String normalizedCompanyNumber = requireCompanyRegistrationNumber(companyRegistrationNumber);
        String normalizedDirector = requireDirectorName(directorName);
        String normalizedBusinessType = requireBusinessType(businessType);
        String normalizedAddress = requireAddress(address);
        return new CommercialApplicationDto(
                normalizedCompanyNumber,
                normalizedDirector,
                normalizedBusinessType,
                normalizedAddress,
                normalizedEmail
        );
    }

    public String requireCompanyRegistrationNumber(String companyRegistrationNumber) {
        if (companyRegistrationNumber == null || companyRegistrationNumber.isBlank()) {
            throw new IllegalArgumentException("Company registration number is required");
        }
        String normalized = companyRegistrationNumber.trim().toUpperCase();
        if (!COMPANY_REGISTRATION_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Company registration number must be 8 digits, 2 letters + 6 digits, or UK + 8 digits (optional suffix, e.g. UK10003429COMPH)"
            );
        }
        return normalized;
    }

    public String requireDirectorName(String directorName) {
        if (directorName == null || directorName.isBlank()) {
            throw new IllegalArgumentException("Director name is required");
        }
        String normalized = directorName.trim();
        if (!PERSON_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Director name must be 3-120 characters and use sensible letters/punctuation");
        }
        return normalized;
    }

    public String requireBusinessType(String businessType) {
        if (businessType == null || businessType.isBlank()) {
            throw new IllegalArgumentException("Business type is required");
        }
        String normalized = businessType.trim();
        if (normalized.length() < 3 || normalized.length() > 120) {
            throw new IllegalArgumentException("Business type must be between 3 and 120 characters");
        }
        return normalized;
    }

    public String requireAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is required");
        }
        String normalized = address.trim();
        if (normalized.length() < 8 || normalized.length() > 500) {
            throw new IllegalArgumentException("Address must be between 8 and 500 characters");
        }
        return normalized;
    }
}
