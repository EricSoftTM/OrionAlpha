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
package util;

/**
 * A C++ Pointer class representing a Java Pointer. 
 * int* pData -> Pointer<Integer> pData
 * 
 * @author Eric
 * @param <P> The Object type to be stored.
 */
public class Pointer<P> {
    public P data;

    public Pointer() {
        this.data = null;
    }

    public Pointer(P data) {
        this.data = data;
    }
    
    /**
     * Returns the value of the typeof Object<P>
     * 
     * @return The value of pData
     */
    public P get() {
        return this.data;
    }
    
    /**
     * Assigns a new value to the data and returns the new value.
     * 
     * @param data The value to assign to pData
     * @return The value of the new pData
     */
    public P set(P data) {
        this.data = data;
        return this.data;
    }
    
    /**
     * If the Pointer<P> instances type of Number, this will add
     * to the current value <code>nVal</code>.
     * 
     * @param val The value to add
     */
    public void add(int val) {
        if (data != null && data instanceof Number) {
            Number p = (Number) data;
            if (p instanceof Byte) {
                Pointer<Byte> cur = (Pointer<Byte>) this;
                cur.set((byte) (cur.get() + val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Short) {
                Pointer<Short> cur = (Pointer<Short>) this;
                cur.set((short) (cur.get() + val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Integer) {
                Pointer<Integer> cur = (Pointer<Integer>) this;
                cur.set((cur.get() + val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Long) {
                Pointer<Long> cur = (Pointer<Long>) this;
                cur.set((cur.get() + val));
                
                this.data = (P) cur.data;
            }
        }
    }
    
    /**
     * If the Pointer<P> instances type of Number, this will 
     * subtract <code>nVal</code> from the current value.
     * 
     * @param val The value to deduct
     */
    public void subtract(int val) {
        if (data != null && data instanceof Number) {
            Number p = (Number) data;
            if (p instanceof Byte) {
                Pointer<Byte> cur = (Pointer<Byte>) this;
                cur.set((byte) (cur.get() - val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Short) {
                Pointer<Short> cur = (Pointer<Short>) this;
                cur.set((short) (cur.get() - val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Integer) {
                Pointer<Integer> cur = (Pointer<Integer>) this;
                cur.set((cur.get() - val));
                
                this.data = (P) cur.data;
            } else if (p instanceof Long) {
                Pointer<Long> cur = (Pointer<Long>) this;
                cur.set((cur.get() - val));
                
                this.data = (P) cur.data;
            }
        }
    }
    
    /**
     * Determines if the value is set. This returns true under two
     * conditions: a) The pointer is an Integer, and the value != 0,
     * or b) The pointer is a Boolean, and the value == true.
     * 
     * @return If the data is set, or otherwise 'true'.
     */
    public boolean isSet() {
        if (data != null) {
            if (data instanceof Number) {
                Number p = (Number) data;
                return p.intValue() != 0;
            } else if (data instanceof Boolean) {
                return (Boolean) data;
            }
        }
        return false;
    }

    /**
     * Determines if two Pointer<P> objects are equal.
     * 
     * @return 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pointer other = (Pointer) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        return true;
    }

    /**
     * Turns the current Pointer<P> instance into a string.
     *
     * @return 
     */
    @Override
    public String toString() {
        return data.toString();
    }

    /**
     * Gets the hash code of this pointer.
     * @return 
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }
}
