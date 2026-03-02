package de.ftracker.domain.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.costDTOs.Category;

public interface CategoryPort {
    Category getDefaultForUser(AppUser user);
}
