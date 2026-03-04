package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByProviderAndProviderUserId(String google, String providerId);

    @Modifying
    @Query("DELETE FROM AppUser u WHERE u.demo = true AND u.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredDemoUsers();
}
