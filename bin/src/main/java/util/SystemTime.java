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
package util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * _SYSTEMTIME
 * 
 * @author Eric, Five
 */
public class SystemTime {
    // Used to manage and store the time.
    private LocalDateTime localDT;
    
    private int year;
    private int month;
    private int dayOfWeek;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int milliseconds;
    
    /**
     * Constructs an empty, un-used SYSTEMTIME.
     * If we are to utilize SystemTime, we 
     * 
     */
    public SystemTime() {
        this(0);
    }
    
    public SystemTime(long nMilliSec) {
        this.localDT = LocalDateTime.ofInstant(Instant.ofEpochMilli(nMilliSec), ZoneId.systemDefault());
        this.year = this.localDT.getYear();
        this.month = this.localDT.getMonthValue();
        this.dayOfWeek = this.localDT.getDayOfWeek().getValue();
        this.day = this.localDT.getDayOfMonth();
        this.hour = this.localDT.getHour();
        this.minute = this.localDT.getMinute();
        this.second = this.localDT.getSecond();
        this.milliseconds = this.localDT.getNano();
    }
    
    public long getTime() {
        return localDT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    public int getYear() {
        return year;
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getHour() {
        return hour;
    }
    
    public int getMinute() {
        return minute;
    }
    
    public int getSecond() {
        return second;
    }
    
    public int getMilliseconds() {
        return milliseconds;
    }
    
    public void setYear(int year) {
        this.localDT = localDT.withYear(year);
        this.year = year;
    }
    
    public void setMonth(int month) {
        this.localDT = localDT.withMonth(month);
        this.month = month;
    }
    
    public void setDay(int day) {
        this.localDT = localDT.withDayOfMonth(day);
        this.day = day;
    }
    
    public void setHour(int hour) {
        this.localDT = localDT.withHour(hour);
        this.hour = hour;
    }
    
    public void setMilliseconds(int milliSec) {
        this.localDT = localDT.withNano(milliSec);
        this.milliseconds = milliSec;
    }
    
    public void setMinute(int min) {
        this.localDT = localDT.withMinute(min);
        this.minute = min;
    }
    
    public void setSecond(int sec) {
        this.localDT = localDT.withSecond(sec);
        this.second = sec;
    }
    
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public FileTime systemTimeToFileTime() {
        return new FileTime(getTime());
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm:ss");
        localDT.format(formatter);
        return localDT.toString();
    }
    
    public String toString(String sFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(sFormat);
        localDT.format(formatter);
        return localDT.toString();
    }
    
    public static int compareSystemTime(SystemTime st1, SystemTime st2) {
        //st1 -> Current Time
        //st2 -> Boarding Time
        int nDiff = st1.year - st2.year;
        if (st1.year == st2.year) {
            nDiff = st1.month - st2.month;
            if (st1.month == st2.month) {
                nDiff = st1.day - st2.day;
                if (st1.day == st2.day) {
                    nDiff = st1.hour - st2.hour;
                    if (st1.hour == st2.hour) {
                        nDiff = st1.minute - st2.minute;
                        if (st1.minute == st2.minute) {
                            nDiff = st1.second - st2.second;
                            if (st1.second == st2.second) {
                                nDiff = st1.milliseconds - st2.milliseconds;
                            }
                        }
                    }
                }
            }
        }
        return nDiff >= 0 ? 1 : -1;
    }
    
    public static SystemTime getLocalTime() {
        return new SystemTime(System.currentTimeMillis());
    }
}
