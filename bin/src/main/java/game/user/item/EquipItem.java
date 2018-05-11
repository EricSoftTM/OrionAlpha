package game.user.item;


/**
 * @author Arnah
*/
public class EquipItem{

	protected int itemID;
	public String itemName;
	public int reqSTR, reqDEX, reqINT, reqLUK, reqPOP, reqJob, reqLevel;
	public int sellPrice;// price
	public boolean cash;
	public short incSTR, incDEX, incINT, incLUK;
	public short incMaxHP, incMaxMP;// incMHP, incMMP
	public short incPAD, incMAD, incPDD, incMDD;
	public short incACC, incEVA, incCraft, incSpeed, incJump, incSwim;
	public int knockback;
	public int attackSpeed;
	public int tuc;
}
