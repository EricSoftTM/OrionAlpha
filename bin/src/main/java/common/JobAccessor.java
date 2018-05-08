/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package common;

/**
 *
 * @author Eric
 */
public enum JobAccessor {
    // Warrior
    Swordman(100),
    Fighter(110),
    Page(120),
    Spearman(130),
    // Magician
    Magician(200),
    Wizard_Fire_Poison(210),
    Wizard_Thunder_Cold(220),
    Cleric(230),
    // Bowman
    Archer(300),
    Hunter(310),
    Crossbowman(320),
    // Thief
    Rogue(400),
    Assassin(410),
    Thief(420);
    private final int job;
    
    private JobAccessor(int job) {
        this.job = job;
    }
    
    public short getJob() {
        return (short) job;
    }
    
    public boolean validate(JobAccessor job) {
        return getJob() >= job.getJob() && getJob() / 100 == job.getJob() / 100;
    }
    
    public static JobAccessor findJob(int jobID) {
        for (JobAccessor job : JobAccessor.values()) {
            if (job.getJob() == jobID) {
                return job;
            }
        }
        return null;
    }
    
    public static int getJobCategory(int jobCode) {
        return jobCode % 1000 / 100;
    }
    
    public static int getJobChangeLevel(int job, int step) {
        switch (step) {
            case 1:
                return getJobCategory(job) != JobCategory.Wizard ? 10 : 8;
            case 2:
                return 30;
        }
        return 200;
    }
    
    public static boolean isCorrectJobForSkillRoot(int job, int skillRoot) {
        if ((skillRoot % 100) != 0) {
            return skillRoot / 10 == job / 10 && job % 10 >= skillRoot % 10;
        }
        return skillRoot / 100 == job / 100;
    }
    
    public static long getJobBitflag(int jobCode) {
        return 1L << (jobCode / 100);
    }
    
    public static String getJobName(int job) {
        switch(job) {
            case 100: // Warrior
                return "Warrior";
            case 110:
                return "Fighter";
            case 120:
                return "Page";
            case 130:
                return "Spearman";
            case 200: // Magician
                return "Magician";
            case 210:
                return "Wizard (Fire, Poison)";
            case 220:
                return "Wizard (Ice, Lightninig)";
            case 230:
                return "Cleric";
            case 300: // Bowman
                return "Archer";
            case 310:
                return "Hunter";
            case 320:
                return "Crossbowman";
            case 400: // Thief
                return "Rogue";
            case 410:
                return "Assassin";
            case 420:
                return "Bandit";
            default: {
                return "Beginner";
            }
        }
    }
}
