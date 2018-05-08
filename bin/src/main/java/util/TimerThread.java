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

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Eric
 */
public class TimerThread {
    public static final TimerThread
            World       = new TimerThread("World_Timer"),
            Field       = new TimerThread("Field_Timer"),
            Etc         = new TimerThread("Etc_Timer")
    ;
    private final String threadName;
    private ScheduledThreadPoolExecutor threadPool;
    
    public TimerThread(String threadName) {
        this.threadName = threadName;
    }
    
    public static final void createTimerThread() {
        World.start();
        Field.start();
        Etc.start();
        Logger.logReport("Timer thread started");
    }
    
    public static final void terminate() {
        World.stop();
        Field.stop();
        Etc.stop();
    }

    public void start() {
        if (threadPool != null && !threadPool.isShutdown() && !threadPool.isTerminated()) {
            return;
        }
	final String name = threadName + Rand32.getInstance().random();
        final ThreadFactory thread = new ThreadFactory() {
            private final AtomicInteger threadCounter = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                final Thread t = new Thread(r);
                t.setName(name + "-Worker-" + threadCounter.incrementAndGet());
                return t;
            }
        };
        threadPool = new ScheduledThreadPoolExecutor(5, thread);
        threadPool.setKeepAliveTime(10, TimeUnit.MINUTES);
        threadPool.allowCoreThreadTimeOut(true);
        threadPool.setMaximumPoolSize(8);
        threadPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
    }

    public void stop() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    public ScheduledFuture<?> Register(Runnable r, long repeat, long delay) {
	if (threadPool == null) {
	    return null;
	}
        return threadPool.scheduleAtFixedRate(new LoggingSaveRunnable(r), delay, repeat, TimeUnit.MILLISECONDS);
    }
    
    public ScheduledFuture<?> Register(Runnable r, long repeat) {
	if (threadPool == null) {
            return null;
        }
        return threadPool.scheduleAtFixedRate(new LoggingSaveRunnable(r), 0, repeat, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> Schedule(Runnable r, long delay) {
	if (threadPool == null) {
            return null;
        }
        return threadPool.schedule(new LoggingSaveRunnable(r), delay, TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> ScheduleAtTimestamp(Runnable r, long timestamp) {
        return Schedule(r, timestamp - System.currentTimeMillis());
    }

    private static class LoggingSaveRunnable implements Runnable {
	Runnable r;
        
	public LoggingSaveRunnable(final Runnable r) {
	    this.r = r;
	}

	@Override
	public void run() {
	    try {
		r.run();
	    } catch (Throwable t) {
                t.printStackTrace(System.err);
	    }
	}
    }
}
