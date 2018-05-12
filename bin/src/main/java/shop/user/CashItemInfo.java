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
package shop.user;

import network.packet.OutPacket;
import util.FileTime;

/**
 *
 * @author sunnyboy
 */
public class CashItemInfo {

    private long liSN;
    private long liCashItemSN;
    private int accountID;
    private int characterID;
    private int itemID;
    private int commodityID;
    private int number;
    private String buyCharacterID = "";
    private FileTime dateExpire;
    private int paybackRate;
    private int discountRate;

    public long getLiSN() {
        return liSN;
    }

    public void setLiSN(long liSN) {
        this.liSN = liSN;
    }

    public long getLiCashItemSN() {
        return liCashItemSN;
    }

    public void setLiCashItemSN(long liCashItemSN) {
        this.liCashItemSN = liCashItemSN;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getCharacterID() {
        return characterID;
    }

    public void setCharacterID(int characterID) {
        this.characterID = characterID;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getCommodityID() {
        return commodityID;
    }

    public void setCommodityID(int commodityID) {
        this.commodityID = commodityID;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getBuyCharacterID() {
        return buyCharacterID;
    }

    public void setBuyCharacterID(String buyCharacterID) {
        this.buyCharacterID = buyCharacterID;
    }

    public FileTime getDateExpire() {
        return dateExpire;
    }

    public void setDateExpire(FileTime dateExpire) {
        this.dateExpire = dateExpire;
    }

    public int getPaybackRate() {
        return paybackRate;
    }

    public void setPaybackRate(int paybackRate) {
        this.paybackRate = paybackRate;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public void encode(OutPacket op) {//verify struct
        op.encodeLong(liCashItemSN);
        op.encodeInt(accountID);
        op.encodeInt(characterID);
        op.encodeInt(itemID);
        op.encodeInt(commodityID);
        op.encodeShort(number);
        op.encodeString(buyCharacterID, 13);
        op.encodeFileTime(dateExpire);
       // op.encodeInt(paybackRate);
       // op.encodeInt(discountRate);
    }
}
