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
package game.messenger;

import game.user.AvatarLook;
import game.user.User;

/**
 *
 * @author sunnyboy
 */
public class Character {

    private AvatarLook avatarLook;
    private int channelID;
    private String id;
    private User user;


    public Character() {
    }

    public AvatarLook getAvatarLook() {
        return avatarLook;
    }

    public int getChannelID() {
        return channelID;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setAvatarLook(AvatarLook avatarLook) {
        this.avatarLook = avatarLook;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
}
