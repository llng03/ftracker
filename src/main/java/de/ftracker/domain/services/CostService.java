package de.ftracker.domain.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.cost.Category;
import de.ftracker.domain.model.cost.Cost;
import de.ftracker.services.repositories.CostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CostService {
    private final CategoryPort categoryPort;
    private final CostRepository costRepository;

    public Cost create(Long id, String descr, BigDecimal amount, boolean isIncome, AppUser user) {
        Category defaultCat = categoryPort.getDefaultForUser(user);

        Cost cost = new Cost(
                id,
                descr,
                amount,
                isIncome,
                defaultCat,
                user
        );
        return costRepository.save(cost);
    }

    public Cost create(String descr, BigDecimal amount, boolean isIncome, AppUser user) {
        Category defaultCat = categoryPort.getDefaultForUser(user);

        Cost cost = new Cost(
                descr,
                amount,
                isIncome,
                user,
                defaultCat
        );
         return costRepository.save(cost);
    }

    public Cost createTransient(String descr, BigDecimal amount, boolean isIncome, AppUser user) {
        Category defaultCat = categoryPort.getDefaultForUser(user);

        return new Cost(
                descr,
                amount,
                isIncome,
                user,
                defaultCat
        );
    }

}
