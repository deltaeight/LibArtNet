/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2018 Julian Rabe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.deltaeight.libartnet.enums;

import java.util.HashMap;

/**
 * Priorities for diag data and their bytes
 *
 * @author Julian Rabe
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public enum Priority {

    Low(0x10),
    Medium(0x40),
    High(0x80),
    Critical(0xE0),
    Volatile(0xF0);

    private static final HashMap<Byte, Priority> priorities;

    static {
        priorities = new HashMap<>(5);
        for (Priority value : values()) {
            priorities.put(value.getValue(), value);
        }
    }

    private final byte value;

    Priority(int value) {
        this.value = (byte) value;
    }

    public static Priority getPriority(byte value) {
        return priorities.getOrDefault(value, Low);
    }

    public byte getValue() {
        return value;
    }
}
