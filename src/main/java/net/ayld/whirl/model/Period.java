package net.ayld.whirl.model;

/**
 *
 * Created by siliev on 14-6-25.
 */
public enum Period implements Comparable<Period>{
    SECOND(1),
    MINUTE(60),
    HOUR(60 * 60),
    FOUR_HOURS(4 * 60 * 60),
    HALF_DAY(12 * 60 * 60),
    DAY(24 * 60 * 60);

    private final int inSeconds;

    Period(int inSeconds) {
        this.inSeconds = inSeconds;
    }

    public int inSeconds() {
        return inSeconds;
    }

    public int in(Period p) {
        if (this.inSeconds < p.inSeconds) {
            return 0;
        }
        else if (this.inSeconds == p.inSeconds) {
            return 1;
        }
        else {
            return this.inSeconds / p.inSeconds;
        }
    }

    public static Period fromSeconds(int seconds) {
        for (Period p : values()) {
            if (p.inSeconds == seconds) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unsupported period: " + seconds + " seconds");
    }
}
