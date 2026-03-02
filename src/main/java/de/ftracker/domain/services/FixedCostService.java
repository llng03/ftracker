package de.ftracker.domain.services;

import de.ftracker.domain.model.AppUser;
import de.ftracker.domain.model.costDTOs.Category;
import de.ftracker.domain.model.costDTOs.Cost;
import de.ftracker.domain.model.costDTOs.FixedCost;
import de.ftracker.domain.model.costDTOs.Interval;
import de.ftracker.services.CategoryRepository;
import de.ftracker.services.CostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class FixedCostService {
    private final CategoryPort categoryPort;
    private final CostRepository costRepository;

    public FixedCost create(Long id, String descr, BigDecimal amount, boolean isIncome, AppUser user, Interval frequency, YearMonth start, YearMonth end) {
        Category defaultCat = categoryPort.getDefaultForUser(user);

        FixedCost fc = new FixedCost(
                id,
                descr,
                amount,
                isIncome,
                defaultCat,
                user,
                frequency,
                start,
                end

        );
        return costRepository.save(fc);
    }

    public FixedCost create(String descr, BigDecimal amount, boolean isIncome, AppUser user, Interval frequency, YearMonth start, YearMonth end) {
        Category defaultCat = categoryPort.getDefaultForUser(user);

        FixedCost fc = new FixedCost(
                descr,
                amount,
                isIncome,
                defaultCat,
                user,
                frequency,
                start,
                end

        );
        return costRepository.save(fc);
    }

    public FixedCost create(String descr, BigDecimal amount, boolean isIncome, AppUser user, Interval frequency) {
        return create(descr, amount, isIncome, user, frequency, YearMonth.now(), null);
    }

    public FixedCost create(Long id, String descr, BigDecimal amount, boolean isIncome, AppUser user, Interval frequency) {
        return create(id, descr, amount, isIncome, user, frequency, YearMonth.now(), null);
    }
}
