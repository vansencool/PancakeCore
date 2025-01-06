package dev.vansen.pancakecore.economy;

import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.LinkedHashMap;
import java.util.Map;

@ThreadSafe
public class ValueTranslator {

    private static final Map<String, Double> SUFFIX_MAP = new LinkedHashMap<>();

    static {
        SUFFIX_MAP.put("T", 1000000000000D);
        SUFFIX_MAP.put("B", 1000000000D);
        SUFFIX_MAP.put("M", 1000000D);
        SUFFIX_MAP.put("K", 1000D);
    }

    public static double convert(@NotNull String value) {
        String suffix = value.replaceAll("[^kKmMbBtT]", "").toUpperCase();
        String numStr = value.replaceAll("[kKmMbBtT]", "");

        if (suffix.isEmpty()) {
            try {
                return Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value: '" + value + "'", e);
            }
        }

        if (!SUFFIX_MAP.containsKey(suffix)) {
            throw new IllegalArgumentException("Unknown suffix: '" + suffix + "' in value: '" + value + "'");
        }

        try {
            return Double.parseDouble(numStr) * SUFFIX_MAP.get(suffix);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid numeric value: '" + value + "'", e);
        }
    }

    public static String format(double value) {
        for (Map.Entry<String, Double> entry : SUFFIX_MAP.entrySet()) {
            double suffixValue = entry.getValue();
            if (value >= suffixValue) {
                double num = value / suffixValue;
                if (num % 1 == 0) {
                    return String.format("%.0f%s", num, entry.getKey());
                } else {
                    return String.format("%.1f%s", num, entry.getKey());
                }
            }
        }
        return String.format("%.1f", value);
    }
}
