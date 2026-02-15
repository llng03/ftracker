package de.ftracker.domain.model.costDTOs;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Getter
@Entity
public class Category {
    @Id
    @Column(unique = true, nullable = false)
    private String categoryName;

    protected Category() {}

    public Category(String name) {
        this.categoryName = name;
    }
}
