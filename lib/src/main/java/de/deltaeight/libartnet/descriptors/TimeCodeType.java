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

package de.deltaeight.libartnet.descriptors;

/**
 * Available timecode types and their framerates.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.packets.ArtTimeCode
 */
public enum TimeCodeType {

    Film(24, 0x00),
    EBU(25, 0x01),
    DF(29.97f, 0x02),
    SMPTE(30, 0x03);

    private final float framerate;
    private final int value;

    TimeCodeType(float framerate, int value) {
        this.framerate = framerate;
        this.value = value;
    }

    public static TimeCodeType getTimeCodeType(int value) {
        if (value > values().length - 1) {
            return EBU;
        }
        return values()[value];
    }

    public float getFramerate() {
        return framerate;
    }

    public byte getByte() {
        return (byte) value;
    }
}
