package com.brianfromoregon.tiles;

import com.google.common.base.Preconditions;

/**
 * A utility to measure progress.
 */
public class ProgressCalc {

    private final int totalTasks;
    private final long startMillis;

    public ProgressCalc(int totalTasks) {
        Preconditions.checkArgument(totalTasks > 0, "totalTasks > 0");
        this.totalTasks = totalTasks;
        this.startMillis = System.currentTimeMillis();
    }

    /**
     * Return a string saying how long until completion.
     */
    public String eta(int tasksCompleted) {
        validate(tasksCompleted);
        if (tasksCompleted == 0) {
            return "some amount of time";
        }
        int secondsElapsed = (int) ((System.currentTimeMillis() - startMillis) / 1000d);
        int secondsLeft = (totalTasks - tasksCompleted) * secondsElapsed / tasksCompleted;
        return prettyDuration(secondsLeft);
    }

    /**
     * Return a number between 0 and 1 representing completion percent.
     */
    public double percent(int tasksCompleted) {
        return validate(tasksCompleted) / (double) totalTasks;
    }

    /**
     * Return an int between 0 and 100 representing completion percent.
     */
    public int percentInt(int tasksCompleted) {
        return (int) (percent(tasksCompleted) * 100);
    }

    private int validate(int tasksCompleted) {
        Preconditions.checkArgument(tasksCompleted >= 0, "tasksCompleted >= 0");
        Preconditions.checkArgument(tasksCompleted <= totalTasks, "tasksCompleted <= totalTasks");
        return tasksCompleted;
    }

    /**
     * Return a simple, human readable duration string such as "one second", "4 hours", or "a long time".
     */
    private String prettyDuration(int seconds) {
        seconds = Math.abs(seconds);
        int SECOND = 1;
        int MINUTE = 60 * SECOND;
        int HOUR = 60 * MINUTE;
        int DAY = 24 * HOUR;

        if (seconds < 1 * MINUTE) {
            return seconds == 1 ? "one second" : seconds + " seconds";
        }
        if (seconds < 2 * MINUTE) {
            return "one minute";
        }
        if (seconds < 45 * MINUTE) {
            return seconds / MINUTE + " minutes";
        }
        if (seconds < 90 * MINUTE) {
            return "an hour";
        }
        if (seconds < 24 * HOUR) {
            return seconds / HOUR + " hours";
        }
        if (seconds < 48 * HOUR) {
            return "a day";
        }
        if (seconds < 30 * DAY) {
            return seconds / DAY + " days";
        }
        return "more than a month";
    }
}
