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
package game.user.item;

/**
 *
 * @author Arnah
 * @author Eric
*/
public class EquipItem {

    private int itemID;
    private String itemName;
    private int reqSTR, reqDEX, reqINT, reqLUK, reqPOP, reqJob, reqLevel;
    private int sellPrice;// price
    private boolean cash;
    private short incSTR, incDEX, incINT, incLUK;
    private short incMaxHP, incMaxMP;// incMHP, incMMP
    private short incPAD, incMAD, incPDD, incMDD;
    private short incACC, incEVA, incCraft, incSpeed, incJump, incSwim;
    private int knockback, attackSpeed;
    private byte tuc;//Total Upgrade Count
    private float recovery;
    private int petTemplateFlag;
    
    public boolean isItemSuitedForPet(int petTemplateID) {
        if (itemID / 10000 == 180 && petTemplateID / 10000 == 500) {
            return ((1 << (petTemplateID % 100)) & petTemplateFlag) != 0;
        }
        return false;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getReqSTR() {
        return reqSTR;
    }

    public void setReqSTR(int reqSTR) {
        this.reqSTR = reqSTR;
    }

    public int getReqDEX() {
        return reqDEX;
    }

    public void setReqDEX(int reqDEX) {
        this.reqDEX = reqDEX;
    }

    public int getReqINT() {
        return reqINT;
    }

    public void setReqINT(int reqINT) {
        this.reqINT = reqINT;
    }

    public int getReqLUK() {
        return reqLUK;
    }

    public void setReqLUK(int reqLUK) {
        this.reqLUK = reqLUK;
    }

    public int getReqPOP() {
        return reqPOP;
    }

    public void setReqPOP(int reqPOP) {
        this.reqPOP = reqPOP;
    }

    public int getReqJob() {
        return reqJob;
    }

    public void setReqJob(int reqJob) {
        this.reqJob = reqJob;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public short getIncSTR() {
        return incSTR;
    }

    public void setIncSTR(short incSTR) {
        this.incSTR = incSTR;
    }

    public short getIncDEX() {
        return incDEX;
    }

    public void setIncDEX(short incDEX) {
        this.incDEX = incDEX;
    }

    public short getIncINT() {
        return incINT;
    }

    public void setIncINT(short incINT) {
        this.incINT = incINT;
    }

    public short getIncLUK() {
        return incLUK;
    }

    public void setIncLUK(short incLUK) {
        this.incLUK = incLUK;
    }

    public short getIncMaxHP() {
        return incMaxHP;
    }

    public void setIncMaxHP(short incMaxHP) {
        this.incMaxHP = incMaxHP;
    }

    public short getIncMaxMP() {
        return incMaxMP;
    }

    public void setIncMaxMP(short incMaxMP) {
        this.incMaxMP = incMaxMP;
    }

    public short getIncPAD() {
        return incPAD;
    }

    public void setIncPAD(short incPAD) {
        this.incPAD = incPAD;
    }

    public short getIncMAD() {
        return incMAD;
    }

    public void setIncMAD(short incMAD) {
        this.incMAD = incMAD;
    }

    public short getIncPDD() {
        return incPDD;
    }

    public void setIncPDD(short incPDD) {
        this.incPDD = incPDD;
    }

    public short getIncMDD() {
        return incMDD;
    }

    public void setIncMDD(short incMDD) {
        this.incMDD = incMDD;
    }

    public short getIncACC() {
        return incACC;
    }

    public void setIncACC(short incACC) {
        this.incACC = incACC;
    }

    public short getIncEVA() {
        return incEVA;
    }

    public void setIncEVA(short incEVA) {
        this.incEVA = incEVA;
    }

    public short getIncCraft() {
        return incCraft;
    }

    public void setIncCraft(short incCraft) {
        this.incCraft = incCraft;
    }

    public short getIncSpeed() {
        return incSpeed;
    }

    public void setIncSpeed(short incSpeed) {
        this.incSpeed = incSpeed;
    }

    public short getIncJump() {
        return incJump;
    }

    public void setIncJump(short incJump) {
        this.incJump = incJump;
    }

    public short getIncSwim() {
        return incSwim;
    }

    public void setIncSwim(short incSwim) {
        this.incSwim = incSwim;
    }

    public int getKnockback() {
        return knockback;
    }

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public byte getTUC() {
        return tuc;
    }

    public void setTUC(int tuc) {
        this.tuc = (byte) tuc;
    }
    
    public float getRecovery() {
        return recovery;
    }
    
    public void setRecovery(float recovery) {
        this.recovery = recovery;
    }
    
    public void addPetTemplateFlag(int flag) {
        this.petTemplateFlag |= flag;
    }
}
