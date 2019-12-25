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
 * @author Arnah
*/
public class UpgradeItem {

    private int itemID;
    private short incMaxHP;// incMHP
    private short incSTR, incDEX, incINT, incLUK;
    private short incACC, incEVA;
    private short incSpeed, incJump;
    private short incPAD, incPDD, incMAD, incMDD;
    private byte success;

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public short getIncMaxHP() {
        return incMaxHP;
    }

    public void setIncMaxHP(short incMaxHP) {
        this.incMaxHP = incMaxHP;
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

    public short getIncPAD() {
        return incPAD;
    }

    public void setIncPAD(short incPAD) {
        this.incPAD = incPAD;
    }

    public short getIncPDD() {
        return incPDD;
    }

    public void setIncPDD(short incPDD) {
        this.incPDD = incPDD;
    }

    public short getIncMAD() {
        return incMAD;
    }

    public void setIncMAD(short incMAD) {
        this.incMAD = incMAD;
    }

    public short getIncMDD() {
        return incMDD;
    }

    public void setIncMDD(short incMDD) {
        this.incMDD = incMDD;
    }

    public byte getSuccess() {
        return success;
    }

    public void setSuccess(byte success) {
        this.success = success;
    }
}
