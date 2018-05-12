package com.tmendes.birthdaydroid;

import java.util.Map;
import java.util.TreeMap;

class StatisticsProvider {
    private final Map<Integer, Integer> ageStats;
    private final Map<String, Integer> signStats;
    private final Map<Integer, Integer> monthStats;
    private final Map<Integer, Integer> weekStats;

    public StatisticsProvider() {
        ageStats = new TreeMap<>();
        signStats = new TreeMap<>();
        monthStats = new TreeMap<>();
        weekStats = new TreeMap<>();
    }

    public void reset() {
        ageStats.clear();
        signStats.clear();
        monthStats.clear();
        weekStats.clear();
    }

    public Map<Integer, Integer> getAgeStats() {
        return ageStats;
    }

    public Map<String, Integer> getSignStats() {
        return signStats;
    }

    public Map<Integer, Integer> getMonthStats() {
        return monthStats;
    }

    public Map<Integer, Integer> getWeekStats() {
        return weekStats;
    }

}
