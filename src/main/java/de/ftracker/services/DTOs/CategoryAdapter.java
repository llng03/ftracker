package de.ftracker.services.DTOs;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.costDTOs.Category;
import de.ftracker.domain.services.CategoryPort;
import de.ftracker.services.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryAdapter implements CategoryPort {
    private final CategoryRepository categoryRepository;

    public Category getDefaultForUser(AppUser user) {
        return categoryRepository.findByUserAndCategoryName(user, "default").orElseThrow(() ->
                new IllegalArgumentException("found no default category for user: " + user.getName()));
    }
}
