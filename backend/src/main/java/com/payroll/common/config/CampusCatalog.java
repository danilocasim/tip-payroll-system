package com.payroll.common.config;

import java.util.List;

public final class CampusCatalog {
    public static final String CASAL = "Casal";
    public static final String ARLEGUI = "Arlegui";
    public static final List<String> CAMPUSES = List.of(CASAL, ARLEGUI);

    private CampusCatalog() {
    }

    public static boolean isValid(String campus) {
        if (campus == null || campus.isBlank()) {
            return false;
        }
        return CAMPUSES.stream().anyMatch(option -> option.equalsIgnoreCase(campus.trim()));
    }

    public static String normalize(String campus) {
        if (!isValid(campus)) {
            throw new IllegalArgumentException("campus must be Casal or Arlegui");
        }

        return CAMPUSES.stream()
                .filter(option -> option.equalsIgnoreCase(campus.trim()))
                .findFirst()
                .orElseThrow();
    }
}
