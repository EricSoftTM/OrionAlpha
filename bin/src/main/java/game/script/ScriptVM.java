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
package game.script;

import game.field.Creature;
import game.field.GameObject;
import game.field.life.npc.Npc;
import game.field.portal.Portal;
import game.user.User;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import util.Logger;

/**
 *
 * @author Eric
 */
public class ScriptVM {
    // VM Decoder Status
    public static final int 
            Ready       = 0,
            Decoding    = 1,
            Message     = 2,
            Pending     = 3,
            Finishing   = 4
    ;
    private static final ScriptEngine PYTHON_ENGINE = new ScriptEngineManager().getEngineByName("python");
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final String PATH = "data/Script/";
    
    private final Lock lock;
    private final LinkedList<MsgHistory> msgHistory;
    private final AtomicInteger status;
    private ScriptSysFunc scriptSys;
    private int posMsgHistory;
    private User target;
    private GameObject self;
    private Point curPos;
    private File script;
    
    public ScriptVM() {
        this.lock = new ReentrantLock();
        this.scriptSys = new ScriptSysFunc(this);
        this.msgHistory = new LinkedList<>();
        this.status = new AtomicInteger(Ready);
        this.posMsgHistory = 0;
        this.curPos = new Point();
    }
    
    public void destroy(User target) {
        lock.lock();
        try {
            if (this.target == target) {
                if (target.lock()) {
                    try {
                        this.target.setScriptVM(null);
                        
                        if (this.script != null) {
                            this.script = null;
                        }
                        if (this.scriptSys != null) {
                            this.scriptSys = null;
                        }
                        if (this.target != null) {
                            this.target = null;
                        }
                    } finally {
                        target.unlock();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
    
    public Point getCurrentPos() {
        return curPos;
    }
    
    public int getHistoryPos() {
        return posMsgHistory - 1;
    }
    
    public LinkedList<MsgHistory> getMsgHistory() {
        return msgHistory;
    }
    
    public ScriptSysFunc getScriptSys() {
        return scriptSys;
    }
    
    public GameObject getSelf() {
        return self;
    }
    
    public AtomicInteger getStatus() {
        return status;
    }
    
    public User getTarget() {
        return target;
    }
    
    public void setHistoryPos(int pos) {
        this.posMsgHistory = pos;
    }
    
    public boolean setScript(User target, String scriptName, GameObject s) {
        lock.lock();
        try {
            if (this.target == null) {
                if (target.lock()) {
                    try {
                        if (target.canAttachAdditionalProcess() && scriptName != null) {
                            //target.setScriptVM(this);
                            this.target = target;
                            this.self = s;
                            
                            Npc npc = null;
                            if (s instanceof Creature) {
                                Creature obj = (Creature) s;
                                if (obj instanceof Npc) {
                                    npc = (Npc) obj;
                                    curPos.x = npc.getCurrentPos().x;
                                    curPos.y = npc.getCurrentPos().y;
                                }
                            }
                            
                            File file = new File(PATH + scriptName + ".py");
                            if (!file.exists()) {
                                if (npc != null) {
                                    file = new File (PATH + s.getTemplateID() + ".py");
                                }
                            }
                            
                            if (file.exists()) {
                                this.script = file;
                                return true;
                            } else {
                                // TODO: Render NPC dialog instead.
                                target.sendSystemMessage("The script {" + scriptName + "} has not yet been implemented.");
                                
                                Logger.logError("inexistent script '%s' (TemplateID: %d)", scriptName, s != null ? s.getTemplateID() : -1);
                            }
                        }
                    } finally {
                        target.unlock();
                    }
                }
            }
            destroy(target);
            return false;
        } finally {
            lock.unlock();
        }
    }
    
    public void run(User target) {
        lock.lock();
        try {
            if (this.target != target || self == null || (!(self instanceof Creature) && !(self instanceof Portal))) {
                return;
            }
            target.setScriptVM(this);
        } finally {
            lock.unlock();
        }
        
        getPool().submit(() -> {
            try {
                getEngine().put("target", getTarget());
                getEngine().put("self", getScriptSys());
                getEngine().eval(new FileReader(script));
            } catch (FileNotFoundException | ScriptException ex) {
                ex.printStackTrace(System.err);
            } finally {
                destroy(getTarget());
            }
        });
    }
    
    static ScriptEngine getEngine() {
        return PYTHON_ENGINE;
    }
    
    static ExecutorService getPool() {
        return POOL;
    }
}
