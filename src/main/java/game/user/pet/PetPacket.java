package game.user.pet;

import game.field.MovePath;
import network.packet.LoopbackPacket;
import network.packet.OutPacket;

/**
 *
 * @author Eric
 */
public class PetPacket {
	
	/**
	 * Activates and summons a new Pet on to the field.
	 *
	 * @param characterID The CharacterID of who owns the pet
	 * @param pet If being initialized, the specific Pet to encode
	 * @return The pet activation packet
	 */
	public static OutPacket onActivated(int characterID, Pet pet) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetActivated);
		packet.encodeInt(characterID);
		packet.encodeBool(pet != null);
		if (pet != null) {
			pet.encodeEnterPacket(packet);
		}
		return packet;
	}
	
	/**
	 * Controls the movement of a pet.
	 *
	 * @param characterID The CharacterID of who owns the pet
	 * @param mp The latest MovePath of the pet
	 * @return The pet movement packet
	 */
	public static OutPacket onMove(int characterID, MovePath mp) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetMove);
		packet.encodeInt(characterID);
		mp.encode(packet);
		return packet;
	}
	
	/**
	 * Controls pet actions based on their commands (ex: /pet Hello!).
	 *
	 * @param dwCharacterID The CharacterID of who owns the pet
	 * @param action The index of the action
	 * @param chat The text to make the pet speak
	 * @return The pet action packet
	 */
	public static OutPacket onAction(int dwCharacterID, byte action, String chat) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetAction);
		packet.encodeInt(dwCharacterID);
		packet.encodeByte(action);
		packet.encodeString(chat);
		return packet;
	}
	
	/**
	 * Handles pet interactions from the user and their commands in addition
	 * to when they're fed pet food.
	 *
	 * @param characterID The CharacterID of who owns the pet
	 * @param interaction The interaction index
	 * @param inc If the reaction has made closeness increase
	 * @param tameness If inc is true, this is the closeness to gain
	 * @return The pet interaction packet
	 */
	public static OutPacket onActionCommand(int characterID, int interaction, boolean inc, short tameness) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetActionCommand);
		packet.encodeInt(characterID);
		packet.encodeByte(interaction);//this->m_bInteractionRequested = nInteraction;
		packet.encodeBool(inc);
		if (inc) {
			packet.encodeShort(tameness);
		}
		return packet;
	}
	
	/**
	 * Updates (visually) a pet name change or its updated level, closeness, and hunger.
	 *
	 * @param characterID The CharacterID of who owns the pet
	 * @param name The new name for the pet
	 * @param level The level of the pet
	 * @param tameness The closeness of the pet
	 * @param repleteness The hungriness of the pet
	 * @return The pet info change packet
	 */
	public static OutPacket onDataChanged(int characterID, String name, byte level, short tameness, byte repleteness) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetDataChanged);
		packet.encodeInt(characterID);
		packet.encodeBool(name != null && !name.isEmpty());
		if (name != null && !name.isEmpty()) {
			packet.encodeString(name);
		} else {
			packet.encodeByte(level);
			packet.encodeShort(tameness);
			packet.encodeByte(repleteness);
		}
		return packet;
	}
	
	/**
	 * The pet effect packet.
	 * This sends level up, teleport, and evolution effects to nearby users.
	 *
	 * @param petEffect The type of pet effect (@see game.user.pet.PetEffect)
	 * @return The pet effect packet
	 */
	public static OutPacket onPetEffect(int characterID, byte petEffect) {
		OutPacket packet = new OutPacket(LoopbackPacket.PetEffect);
		packet.encodeInt(characterID);
		packet.encodeByte(petEffect);
		return packet;
	}
}
