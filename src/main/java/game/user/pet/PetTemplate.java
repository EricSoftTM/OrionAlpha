package game.user.pet;

import util.wz.WzProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Eric
 */
public class PetTemplate {
	private static final Map<Integer, PetTemplate> templates = new HashMap<>();
	private static final Lock lockPet = new ReentrantLock();
	private int templateID;
	private String name;
	
	public PetTemplate() {
		this.name = "";
	}
	
	public static PetTemplate getPetTemplate(int templateID) {
		lockPet.lock();
		try {
			return templates.get(templateID);
		} finally {
			lockPet.unlock();
		}
	}
	
	public static void load() {
		//
	}
	
	private static void registerPet(int templateID, WzProperty prop) {
	
	}
	
	public String getName() {
		return name;
	}
	
	public int getTemplateID() {
		return templateID;
	}
}
