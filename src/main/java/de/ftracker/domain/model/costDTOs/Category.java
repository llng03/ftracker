package de.ftracker.domain.model.costDTOs;

import de.ftracker.domain.model.AppUser;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "category_name"})
)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @ManyToOne(optional=false)
    private AppUser user;

    protected Category() {}

    public Category(String name, AppUser user) {
        this.categoryName = name;
        this.user = user;
    }
}
