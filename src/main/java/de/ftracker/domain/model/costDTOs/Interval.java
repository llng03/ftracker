package de.ftracker.domain.model.costDTOs;

public enum Interval {
    MONTHLY("Montlich"),
    QUARTERLY("Vierteljährlich"),
    SEMI_ANNUAL("Halbjährlich"),
    ANNUAL("Jährlich");

    private final String label;

    Interval(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

