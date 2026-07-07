package com.gunrattler.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TabParser {
    private static double purse = 0.0;
    private static double bank = 0.0;
    private static double mithrilPowder = 0.0;
    private static double gemstonePowder = 0.0;
    private static double glacitePowder = 0.0;
    private static String sbDate = "";
    private static String sbTime = "";
    private static String mainArea = "";
    private static String slayerName = "";
    private static String slayerProgress = "";
    private static String objectiveLine = "";
    private static String objectiveSubtitleLine = "";
    private static boolean hasData = false;

    private static final Pattern NUMBER_PATTERN = Pattern.compile("([\\d,\\.]+)");
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)§[0-9a-fk-orx]");
    
    private static final Pattern SB_DATE_PATTERN = Pattern.compile("(?i)(early|late)?\\s*(spring|summer|autumn|winter)\\s+\\d+(st|nd|rd|th)");
    private static final Pattern SB_TIME_PATTERN = Pattern.compile("(?i)\\d{1,2}:\\d{2}(am|pm).*");
    private static final Pattern BOSS_PATTERN = Pattern.compile("(?i)(packmaster|horror|sven|revenant|tarantula|voidgloom|vampire|tier|\\b[IVXLCDM]+\\b)");

    private static final Set<String> MAIN_AREAS = Set.of(
        "hub", "private island", "the garden", "crimson isle", "the end", 
        "spider's den", "the barn", "mushroom desert", "deep caverns", 
        "dwarven mines", "crystal hollows", "gold mine", "glacite tunnels", 
        "dungeon hub", "rift dimension", "jerry's workshop"
    );

    public static void updateData() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.getConnection() == null) {
            resetData();
            return;
        }

        Scoreboard scoreboard = client.level.getScoreboard();
        if (scoreboard == null) {
            resetData();
            return;
        }

        Objective skyblockObjective = scoreboard.getObjective("SBScoreboard");
        if (skyblockObjective == null) {
            resetData();
            return;
        }

        String sidebarTitle = COLOR_CODE_PATTERN.matcher(skyblockObjective.getDisplayName().getString()).replaceAll("").toLowerCase();
        if (!sidebarTitle.contains("skyblock")) {
            resetData();
            return;
        }

        hasData = true;
        
        sbDate = "";
        sbTime = "";
        mainArea = "";
        objectiveLine = "";
        objectiveSubtitleLine = "";
        mithrilPowder = 0.0;
        gemstonePowder = 0.0;
        glacitePowder = 0.0;

        Collection<PlayerTeam> rawTeams = scoreboard.getPlayerTeams();
        List<PlayerTeam> sortedTeams = new ArrayList<>(rawTeams);
        sortedTeams.sort((t1, t2) -> Integer.compare(extractNumber(t2.getName()), extractNumber(t1.getName())));

        List<String> orderedFullLines = new ArrayList<>();
        List<String> orderedCleanedLines = new ArrayList<>();
        List<String> foundLocations = new ArrayList<>();

        for (PlayerTeam team : sortedTeams) {
            if (team == null) continue;

            String fullLine = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();
            String cleanedLine = COLOR_CODE_PATTERN.matcher(fullLine).replaceAll("").trim();
            if (cleanedLine.isEmpty()) continue;

            orderedFullLines.add(fullLine);
            orderedCleanedLines.add(cleanedLine);

            String lowerText = cleanedLine.toLowerCase();

            if (fullLine.contains("⏣") || lowerText.contains("area:") || lowerText.contains("zone:")) {
                if (!foundLocations.contains(fullLine)) {
                    foundLocations.add(fullLine);
                }
            }
            if (lowerText.contains("purse:") || lowerText.contains("coins:")) {
                purse = parseHypixelNumber(cleanedLine, "purse:", "coins:");
            }
            if (SB_DATE_PATTERN.matcher(cleanedLine).matches()) {
                sbDate = cleanedLine;
            }
            if (SB_TIME_PATTERN.matcher(cleanedLine).matches()) {
                sbTime = cleanedLine;
            }
        }

        for (int i = 0; i < orderedCleanedLines.size(); i++) {
            String clean = orderedCleanedLines.get(i);
            if (clean.toLowerCase().contains("objective")) {
                objectiveLine = orderedFullLines.get(i);
                if (i + 1 < orderedFullLines.size()) {
                    objectiveSubtitleLine = orderedFullLines.get(i + 1);
                }
                break;
            }
        }

        // Process locations
        if (foundLocations.size() == 1) {
            mainArea = foundLocations.get(0);
        } else if (foundLocations.size() >= 2) {
            String line1 = foundLocations.get(0);
            String line2 = foundLocations.get(1);

            String clean1 = COLOR_CODE_PATTERN.matcher(line1).replaceAll("").toLowerCase();
            String clean2 = COLOR_CODE_PATTERN.matcher(line2).replaceAll("").toLowerCase();

            boolean line1IsMain = MAIN_AREAS.stream().anyMatch(clean1::contains);
            boolean line2IsMain = MAIN_AREAS.stream().anyMatch(clean2::contains);

            if (line1IsMain && !line2IsMain) {
                mainArea = line1;
            } else if (line2IsMain && !line1IsMain) {
                mainArea = line2;
            } else {
                mainArea = line1;
            }
        }

        var connection = client.getConnection();
        var players = connection.getListedOnlinePlayers();
        if (players == null || players.isEmpty()) {
            players = connection.getOnlinePlayers();
        }

        if (players != null) {
            boolean hasSlayerHeader = false;
            String tempName = "";
            String tempProgress = "";

            for (PlayerInfo info : players) {
                if (info == null) continue;

                Component displayName = info.getTabListDisplayName();
                if (displayName == null) continue;

                String tabLine = COLOR_CODE_PATTERN.matcher(displayName.getString()).replaceAll("").trim();
                if (tabLine.isEmpty()) continue;

                String lowerTab = tabLine.toLowerCase();

                if (lowerTab.contains("bank:")) {
                    bank = parseHypixelNumber(tabLine, "bank:");
                    continue;
                }
                if (lowerTab.startsWith("mithril:")) {
                    mithrilPowder = parseHypixelNumber(tabLine, "mithril:");
                    continue;
                }
                if (lowerTab.startsWith("gemstone:")) {
                    gemstonePowder = parseHypixelNumber(tabLine, "gemstone:");
                    continue;
                }
                if (lowerTab.startsWith("glacite:")) {
                    glacitePowder = parseHypixelNumber(tabLine, "glacite:");
                    continue;
                }
                if (lowerTab.equals("slayer:")) {
                    hasSlayerHeader = true;
                    continue;
                }
                if (lowerTab.contains("level")) {
                    continue;
                }
                if (lowerTab.contains("/") && (lowerTab.contains("combat xp") || lowerTab.contains("kills"))) {
                    tempProgress = tabLine;
                } else if (BOSS_PATTERN.matcher(lowerTab).find()) {
                    tempName = tabLine;
                }
            }

            if (hasSlayerHeader) {
                slayerName = tempName;
                slayerProgress = tempProgress;
            } else {
                slayerName = "";
                slayerProgress = "";
            }
        }
    }

    private static int extractNumber(String s) {
        try {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(s);
            if (m.find()) {
                return Integer.parseInt(m.group());
            }
        } catch (Exception e) {
            // Drop out quietly
        }
        return 0;
    }

    private static void resetData() {
        hasData = false;
        purse = 0.0;
        bank = 0.0;
        mithrilPowder = 0.0;
        gemstonePowder = 0.0;
        glacitePowder = 0.0;
        sbDate = "";
        sbTime = "";
        mainArea = "";
        slayerName = "";
        slayerProgress = "";
        objectiveLine = "";
        objectiveSubtitleLine = "";
    }

    private static double parseHypixelNumber(String line, String... triggers) {
        try {
            String lower = line.toLowerCase();
            String isolatedTarget = "";

            for (String trigger : triggers) {
                if (lower.contains(trigger)) {
                    int index = lower.indexOf(trigger) + trigger.length();
                    isolatedTarget = lower.substring(index).trim();
                    break;
                }
            }

            if (isolatedTarget.isEmpty()) return 0.0;

            Matcher matcher = NUMBER_PATTERN.matcher(isolatedTarget);
            if (!matcher.find()) return 0.0;

            String rawNumber = matcher.group(1);
            String cleaned = rawNumber.replace(",", "");
            if (cleaned.isEmpty()) return 0.0;

            double value = Double.parseDouble(cleaned);

            if (isolatedTarget.contains("b")) value *= 1_000_000_000.0;
            else if (isolatedTarget.contains("m")) value *= 1_000_000.0;
            else if (isolatedTarget.contains("k")) value *= 1_000.0;

            return value;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static String formatPrice(String prefix, double price) {
        if (price >= 1_000_000_000) return String.format("%s: %.2fB", prefix, price / 1_000_000_000.0);
        if (price >= 1_000_000) return String.format("%s: %.2fM", prefix, price / 1_000_000.0);
        if (price >= 1_000) return String.format("%s: %.1fK", prefix, price / 1_000.0);
        return String.format("%s: %,.0f", prefix, price);
    }

    public static boolean hasData() { return hasData; }
    public static String getPurse() { return formatPrice("Purse", purse); }
    public static String getBank() { return formatPrice("Bank", bank); }
    public static double getMithrilPowderRaw() { return mithrilPowder; }
    public static String getMithrilPowder() { return formatPrice("Mithril", mithrilPowder); }
    public static double getGemstonePowderRaw() { return gemstonePowder; }
    public static String getGemstonePowder() { return formatPrice("Gemstone", gemstonePowder); }
    public static double getGlacitePowderRaw() { return glacitePowder; }
    public static String getGlacitePowder() { return formatPrice("Glacite", glacitePowder); }
    public static String getSbDate() { return sbDate; }
    public static String getSbTime() { return sbTime; }
    public static String getMainArea() { return mainArea; }
    public static String getSlayerName() { return slayerName; }
    public static String getSlayerProgress() { return slayerProgress; }
    public static String getObjectiveLine() { return objectiveLine; }
    public static String getObjectiveSubtitleLine() { return objectiveSubtitleLine; }
}