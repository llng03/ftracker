package de.ftracker.services;

import de.ftracker.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByProviderAndProviderUserId(String google, String providerId);
}
