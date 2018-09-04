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
 * @author Julian Rabe
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public enum ArtNet {

    /**
     * The protocol revision this library works with (14).
     */
    PROTOCOL_REVISION(14),

    /**
     * The Art-Net Header to send with in every {@link de.deltaeight.libartnet.packets.ArtNetPacket} ("Art-Net\0").
     */
    HEADER(new byte[]{0x41, 0x72, 0x74, 0x2D, 0x4E, 0x65, 0x74, 0x00}),

    /**
     * The UDP port on which network traffic is handled ({@code 0x1936}).
     */
    PORT(0x1936);

    private final byte[] bytes;

    ArtNet(byte[] bytes) {
        this.bytes = bytes;
    }

    ArtNet(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >> 8);
        bytes[1] = (byte) value;
        this.bytes = bytes;
    }

    /**
     * @return Bytes in big endian byte order.
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * @return Bytes in little endian byte order.
     */
    public byte[] getBytesLittleEndian() {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[bytes.length - i - 1];
        }
        return result;
    }
}
