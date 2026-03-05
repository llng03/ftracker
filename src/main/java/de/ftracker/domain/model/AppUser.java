package de.ftracker.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String provider;
    private String providerUserId;
    private String name;
    private String email;
    @Column(name="is_demo", nullable = false)
    private boolean demo = false;
    private LocalDateTime expiresAt;
}
