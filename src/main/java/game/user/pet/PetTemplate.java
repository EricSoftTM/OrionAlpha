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
package game.user.pet;

import util.wz.WzFileSystem;
import util.wz.WzNodeType;
import util.wz.WzPackage;
import util.wz.WzProperty;
import util.wz.WzSAXProperty;
import util.wz.WzUtil;
import util.wz.WzXML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
public class PetTemplate implements WzXML {
	private static final Map<Integer, PetTemplate> templates = new HashMap<>();
	private static final Lock lockPet = new ReentrantLock();
	
	private int templateID;
	private byte hungry;
	private short life;
	private String name;
	private final List<PetInteraction> interactions;
	
	public PetTemplate() {
		this.name = "";
		this.interactions = new ArrayList<>();
	}
	
	public static PetTemplate getPetTemplate(int templateID) {
		lockPet.lock();
		try {
			return templates.get(templateID);
		} finally {
			lockPet.unlock();
		}
	}
	
	public static void load(boolean useSAX) {
		WzPackage petDir = new WzFileSystem().init("Item/Pet").getPackage();
		if (petDir != null) {
			if (useSAX) {
				for (WzSAXProperty petData : petDir.getSAXEntries().values()) {
					registerPet(Integer.parseInt(petData.getFileName().replaceAll(".img.xml", "")), petData);
				}
			} else {
				for (WzProperty petData : petDir.getEntries().values()) {
					registerPet(Integer.parseInt(petData.getNodeName().replaceAll(".img", "")), petData);
				}
			}
			petDir.release();
		}
		petDir = null;
	}
	
	private static void registerPet(int templateID, WzProperty prop) {
		WzProperty info = prop.getNode("info");
		if (info == null) {
			return;
		}
		PetTemplate template = new PetTemplate();
		template.templateID = templateID;
		template.name = WzUtil.getString(info.getNode("name"), "NULL");
		template.life = WzUtil.getShort(info.getNode("life"), 90);
		template.hungry = WzUtil.getByte(info.getNode("hungry"), 3);
		
		WzProperty interactions = prop.getNode("interact");
		if (interactions != null) {
			for (WzProperty interact : interactions.getChildNodes()) {
				PetInteraction interaction = new PetInteraction();
				interaction.setFriendnessInc(WzUtil.getInt32(interact.getNode("inc"), 0));
				interaction.setProb(WzUtil.getInt32(interact.getNode("prob"), 0));
				interaction.getLevelRange().setLow(WzUtil.getInt32(interact.getNode("l0"), 0));
				interaction.getLevelRange().setHigh(WzUtil.getInt32(interact.getNode("l1"), 0));
				template.getInteractions().add(interaction);
			}
		}
		
		templates.put(templateID, template);
	}
	
	private static void registerPet(int templateID, WzSAXProperty prop) {
		PetTemplate template = new PetTemplate();
		template.templateID = templateID;
		
		prop.addEntity(template);
		prop.parse();
		
		templates.put(templateID, template);
	}
	
	public byte getHungry() {
		return hungry;
	}
	
	public List<PetInteraction> getInteractions() {
		return interactions;
	}
	
	public short getLife() {
		return life;
	}
	
	public String getName() {
		return name;
	}
	
	public int getTemplateID() {
		return templateID;
	}
	
	public boolean isValidAction(int action) {
		return action >= PetAction.Move && action < PetAction.NO;
	}
	
	@Override
	public void parse(String root, String name, String value, WzNodeType type) {
		if (type.equals(WzNodeType.STRING)) {
			if (name.equals("name")) {
				this.name = value;
				if (this.name == null) {
					this.name = "NULL";
				}
			}
		} else if (type.equals(WzNodeType.INT)) {
			switch (name) {
				case "life":
					this.life = WzUtil.getShort(value, 90);
					break;
				case "hungry":
					this.hungry = WzUtil.getByte(value, 3);
					break;
			}
		}
	}
}
