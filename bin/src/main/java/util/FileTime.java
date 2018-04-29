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

/**
 * FileTime
 * Specifically recording time in 100-nanosecond intervals starting from January 1, 1601.
 * Note that UNIX time is recorded starting from January 1, 1970.
 * 
 * @author Eric
 */
public class FileTime {
    // Nexon uses smalldatetime which ranges from 1900-01-01 through 2079-06-06.
    // However, they only pre-define their dates up to 2079-01-01.
    public static final FileTime
            /* Minimum date range [DB_DATE_19000101] */
            DATE_1900 = new FileTime(4259332096L, 21968699L),
            /* Maximum date range [DB_DATE_20790101] */
            DATE_2079 = new FileTime(3137699840L, 35120710L),
            /* An empty, unused/null date */
            DATE_NULL = new FileTime(0L, 0L),
            
            NONE = DATE_NULL,
            START = DATE_1900,
            END = DATE_2079
    ;
    /* Important filetime intervals to construct times */
    public static final long
            FILETIME_MILLISECOND = 10000,
            FILETIME_SECOND = 10000000,
            FILETIME_MINUTE = 600000000L,
            FILETIME_HOUR = 36000000000L,
            FILETIME_DAY = 864000000000L,
            FILETIME_MONTH = 24192000000000L,
            FILETIME_YEAR = 290304000000000L,
            
            EPOCH_BIAS = 11644473600000L
    ;
    private int lowDateTime;
    private int highDateTime;
    
    public FileTime() {
        lowDateTime = 0;
        highDateTime = 0;
    }
    
    public FileTime(long milliSec) {
        milliSec += EPOCH_BIAS;
        milliSec *= FILETIME_MILLISECOND;
        highDateTime = (int) (milliSec >> 32 & 0xFFFFFFFFL);
        lowDateTime = (int) (milliSec & 0xFFFFFFFFL);
    }
    
    private FileTime(Long lowPart, Long highPart) {
        lowDateTime = lowPart.intValue();
        highDateTime = highPart.intValue();
    }
    
    private FileTime(FileTime ft) {
        lowDateTime = ft.lowDateTime;
        highDateTime = ft.highDateTime;
    }
    
    public final FileTime makeClone() {
        FileTime ft = new FileTime(this);
        return ft;
    }
    
    public int getLowDateTime() {
        return lowDateTime;
    }
    
    public int getHighDateTime() {
        return highDateTime;
    }
    
    public void setLowDateTime(int time) {
        lowDateTime = time;
    }
    
    public void setHighDateTime(int time) {
        highDateTime = time;
    }
    
    public long getFileTime() {
        return (long) highDateTime << 32 | lowDateTime & 0xFFFFFFFFL;
    }
    
    public void add(long nType, long liValue) {
        Long tTime = nType * liValue;
        tTime += getFileTime();
        
        highDateTime = (int) (tTime >> 32 & 0xFFFFFFFFL);
        lowDateTime = (int) (tTime & 0xFFFFFFFFL);
    }
    
    public long fileTimeToLong() {
        /* Disabled since this seems to completely cripple JVMs. Inf. loop? IDK
        if (FileTime.CompareFileTime(this, FileTime.START) < 0)
            return FileTime.START.FileTimeToLong();
        if (FileTime.CompareFileTime(this, FileTime.END) > 0)
            return FileTime.END.FileTimeToLong();
        */
        return (getFileTime() / FILETIME_MILLISECOND) - EPOCH_BIAS;
    }
    
    public SystemTime fileTimeToSystemTime() {
        SystemTime st = new SystemTime(fileTimeToLong());
        return st;
    }
    
    public static void ftSubtract(FileTime from, FileTime to, Pointer<Integer> day, Pointer<Integer> hour, Pointer<Integer> min, Pointer<Integer> sec) {
        long time = ((from.highDateTime - (long)to.highDateTime) << 32) - to.lowDateTime + from.lowDateTime;
        if (day != null)
            day.set((int) (time / FileTime.FILETIME_DAY));
        if (hour != null)
            hour.set((int) ((time / FileTime.FILETIME_HOUR) % 24));
        if (min != null)
            min.set((int) ((time / FileTime.FILETIME_MINUTE) % 60));
        if (sec != null)
            sec.set((int) ((time / FileTime.FILETIME_SECOND) % 60));
    }
    
    public static FileTime longToFileTime(long t) {
        FileTime ft = new FileTime(t);
        return ft;
    }
    
    public static FileTime getStringToFileTime(String date, boolean start) {
        if (date != null && !date.isEmpty()) {
            int year = Integer.valueOf(date.substring(0, 4));
            int month = Integer.valueOf(date.substring(4, 6));
            int day = Integer.valueOf(date.substring(6, 8));
            int hour = Integer.valueOf(date.substring(8, 10));
            SystemTime st = new SystemTime();
            st.setYear(year);
            st.setMonth(month);
            st.setDay(day);
            st.setHour(hour);
            st.setMilliseconds(0);
            st.setSecond(0);
            st.setMinute(0);
            st.setDayOfWeek(0);
            
            FileTime ft = st.systemTimeToFileTime();
            return ft;
        } else {
            if (start) {
                return FileTime.START;
            } else {
                return FileTime.END;
            }
        }
    }
    
    public static FileTime systemTimeToFileTime() {
        return new FileTime(System.currentTimeMillis());
    }
    
    public static int compareFileTime(FileTime fileTime1, FileTime fileTime2) {
        if (fileTime1.getFileTime() < fileTime2.getFileTime()) {
            return -1;
        } else if (fileTime1.getFileTime() > fileTime2.getFileTime()) {
            return 1;
        } else {
            return 0;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof FileTime) {
            FileTime ft = (FileTime) o;
            return super.equals(o) || ft.getFileTime() == getFileTime();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int prime = 24;
        prime = prime * 5 + (lowDateTime ^ (1 << highDateTime));
        return prime;
    }
}
