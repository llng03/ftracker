package de.ftracker.services.repositories;

import de.ftracker.domain.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByProviderAndProviderUserId(String google, String providerId);

    @Query("SELECT u FROM AppUser u WHERE u.demo = true AND u.expiresAt < CURRENT_TIMESTAMP")
    List<AppUser> findExpiredDemoUsers();
}
