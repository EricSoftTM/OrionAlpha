package game.user.pet;

import common.item.ItemSlotBase;
import common.item.ItemSlotPet;
import common.item.ItemType;
import game.field.Field;
import game.field.StaticFoothold;
import game.user.User;
import network.packet.OutPacket;

import java.awt.*;

/**
 *
 * @author Eric
 */
public class Pet {
	private PetTemplate template;
	private long petLockerSN;
	private User owner;
	private Field field;
	private String name;
	private int itemSlotPos;
	private Point curPos;
	private byte moveAction;
	private short footholdSN;
	
	public Pet() {
		this.name = "";
		this.itemSlotPos = 0;
		this.curPos = new Point(0, 0);
	}
	
	public void encodeEnterPacket(OutPacket packet) {
		ItemSlotPet item = getItemSlot();
		packet.encodeInt(template.getTemplateID());
		packet.encodeString(item.getPetName());
		packet.encodeByte(item.getLevel());
		packet.encodeShort(item.getTameness());
		packet.encodeByte(item.getRepleteness());
		packet.encodeShort(curPos.x);
		packet.encodeShort(curPos.y);
		packet.encodeByte(moveAction);
		packet.encodeShort(footholdSN);
	}
	
	public Field getField() {
		return field;
	}
	
	public short getFootholdSN() {
		return footholdSN;
	}
	
	public ItemSlotPet getItemSlot() {
		return (ItemSlotPet) owner.getCharacter().getItem(ItemType.Cash, getItemSlotPos());
	}
	
	public int getItemSlotPos() {
		ItemSlotBase item = owner.getCharacter().getItem(ItemType.Cash, itemSlotPos);
		if (item == null || item.getCashItemSN() != petLockerSN) {
			itemSlotPos = owner.getCharacter().findCashItemSlotPosition(ItemType.Cash, petLockerSN);
		}
		return itemSlotPos;
	}
	
	public byte getMoveAction() {
		return moveAction;
	}
	
	public String getName() {
		return name;
	}
	
	public User getOwner() {
		return owner;
	}
	
	public PetTemplate getTemplate() {
		return template;
	}
	
	public boolean init(User user, short pos) {
		ItemSlotPet item = (ItemSlotPet) user.getCharacter().getItem(ItemType.Cash, pos);
		if (item != null) {
			this.owner = user;
			this.petLockerSN = item.getCashItemSN();
			this.template = PetTemplate.getPetTemplate(item.getItemID());
			this.curPos.setLocation(user.getCurrentPosition());
			this.curPos.y -= 20;
			this.name = item.getPetName();
			onEnterField(user.getField());
			return !isDead();
		}
		return false;
	}
	
	public boolean isDead() {
		ItemSlotPet item = getItemSlot();
		if (item != null) {
			return item.isDead();
		}
		return false;
	}
	
	public void onEnterField(Field field) {
		this.field = field;
		this.curPos.setLocation(owner.getCurrentPosition());
		
		short sn = 0;
		StaticFoothold fh = field.getSpace2D().getFootholdUnderneath(curPos.x, curPos.y, null);
		if (fh != null) {
			sn = (short) fh.getSN();
		}
		
		this.moveAction = 0;
		this.footholdSN = sn;
	}
	
	public void setMovePosition(int x, int y, byte moveAction, short sn) {
		this.curPos.setLocation(x, y);
		this.moveAction = moveAction;
		this.footholdSN = sn;
	}
}
