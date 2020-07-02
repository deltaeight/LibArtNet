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

package de.deltaeight.libartnet.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtDmxBuilderTest extends AbstractPacketBuilderTest {

    private static final byte[] DEFAULT_PACKET = getExpectedData(0x00, 0x00, 0x00, 0x00, 0x00, new byte[0]);

    private static byte[] getExpectedData(int sequence,
                                          int physical,
                                          int netAddress,
                                          int subnetAddress,
                                          int universeAddress,
                                          byte[] data) {

        byte[] bytes = new byte[18 + data.length];

        System.arraycopy(new byte[]{
                0x41, 0x72, 0x74, 0x2D, 0x4E, 0x65, 0x74, 0x00, // Header
                0x00, 0x50,                                     // OpCode
                0x00, 0x0E,                                     // Protocol version
        }, 0, bytes, 0, 12);

        bytes[12] = (byte) sequence;
        bytes[13] = (byte) physical;
        bytes[14] = (byte) (subnetAddress << 4 | universeAddress);
        bytes[15] = (byte) netAddress;
        bytes[16] = (byte) (data.length >> 8);
        bytes[17] = (byte) data.length;

        System.arraycopy(data, 0, bytes, 18, data.length);

        return bytes;
    }

    @Test
    void build() {
        assertPackets(DEFAULT_PACKET, new ArtDmxBuilder());
    }

    @Test
    void sequence() {

        byte[] expectedDataSequence1 = getExpectedData(0x01, 0x00, 0x00, 0x00, 0x00, new byte[0]);
        byte[] expectedDataSequence255 = getExpectedData(0xff, 0x00, 0x00, 0x00, 0x00, new byte[0]);

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getSequence());

        builder.setSequence(1);
        assertEquals(1, builder.getSequence());
        assertPackets(expectedDataSequence1, builder);

        builder.setSequence(0);
        assertEquals(0, builder.getSequence());
        assertPackets(DEFAULT_PACKET, builder);

        builder.setSequence(255);
        assertEquals(255, builder.getSequence());
        assertPackets(expectedDataSequence255, builder);

        assertSame(builder, builder.withSequence(1));
        assertEquals(1, builder.getSequence());
        assertPackets(expectedDataSequence1, builder);

        assertSame(builder, builder.withSequence(0));
        assertEquals(0, builder.getSequence());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withSequence(255));
        assertEquals(255, builder.getSequence());
        assertPackets(expectedDataSequence255, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setSequence(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withSequence(256));
    }

    @Test
    void physical() {

        byte[] expectedDataPhysical1 = getExpectedData(0x00, 0x01, 0x00, 0x00, 0x00, new byte[0]);
        byte[] expectedDataPhysical255 = getExpectedData(0x00, 0xff, 0x00, 0x00, 0x00, new byte[0]);

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getPhysical());

        builder.setPhysical(1);
        assertEquals(1, builder.getPhysical());
        assertPackets(expectedDataPhysical1, builder);

        builder.setPhysical(0);
        assertEquals(0, builder.getPhysical());
        assertPackets(DEFAULT_PACKET, builder);

        builder.setPhysical(255);
        assertEquals(255, builder.getPhysical());
        assertPackets(expectedDataPhysical255, builder);

        assertSame(builder, builder.withPhysical(1));
        assertEquals(1, builder.getPhysical());
        assertPackets(expectedDataPhysical1, builder);

        assertSame(builder, builder.withPhysical(0));
        assertEquals(0, builder.getPhysical());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withPhysical(255));
        assertEquals(255, builder.getPhysical());
        assertPackets(expectedDataPhysical255, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setPhysical(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withPhysical(256));
    }

    @Test
    void subnetAddress() {

        byte[] expectedDataSubnet1 = getExpectedData(0x00, 0x00, 0x00, 0x01, 0x00, new byte[0]);
        byte[] expectedDataSubnet15 = getExpectedData(0x00, 0x00, 0x00, 0x0f, 0x00, new byte[0]);

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getSubnetAddress());

        builder.setSubnetAddress(1);
        assertEquals(1, builder.getSubnetAddress());
        assertPackets(expectedDataSubnet1, builder);

        builder.setSubnetAddress(0);
        assertEquals(0, builder.getSubnetAddress());
        assertPackets(DEFAULT_PACKET, builder);

        builder.setSubnetAddress(15);
        assertEquals(15, builder.getSubnetAddress());
        assertPackets(expectedDataSubnet15, builder);

        assertSame(builder, builder.withSubnetAddress(1));
        assertEquals(1, builder.getSubnetAddress());
        assertPackets(expectedDataSubnet1, builder);

        assertSame(builder, builder.withSubnetAddress(0));
        assertEquals(0, builder.getSubnetAddress());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withSubnetAddress(15));
        assertEquals(15, builder.getSubnetAddress());
        assertPackets(expectedDataSubnet15, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setSubnetAddress(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withSubnetAddress(16));
    }

    @Test
    void universeAddress() {

        byte[] expectedDataUniverse1 = getExpectedData(0x00, 0x00, 0x00, 0x00, 0x01, new byte[0]);
        byte[] expectedDataUniverse15 = getExpectedData(0x00, 0x00, 0x00, 0x00, 0x0f, new byte[0]);

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getUniverseAddress());

        builder.setUniverseAddress(1);
        assertEquals(1, builder.getUniverseAddress());
        assertPackets(expectedDataUniverse1, builder);

        builder.setUniverseAddress(0);
        assertEquals(0, builder.getUniverseAddress());
        assertPackets(DEFAULT_PACKET, builder);

        builder.setUniverseAddress(15);
        assertEquals(15, builder.getUniverseAddress());
        assertPackets(expectedDataUniverse15, builder);

        assertSame(builder, builder.withUniverseAddress(1));
        assertEquals(1, builder.getUniverseAddress());
        assertPackets(expectedDataUniverse1, builder);

        assertSame(builder, builder.withUniverseAddress(0));
        assertEquals(0, builder.getUniverseAddress());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withUniverseAddress(15));
        assertEquals(15, builder.getUniverseAddress());
        assertPackets(expectedDataUniverse15, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setUniverseAddress(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withUniverseAddress(16));
    }

    @Test
    void netAddress() {

        byte[] expectedDataNetAddress1 = getExpectedData(0x00, 0x00, 0x01, 0x00, 0x00, new byte[0]);
        byte[] expectedDataNetAddress127 = getExpectedData(0x00, 0x00, 0x7f, 0x00, 0x00, new byte[0]);

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getNetAddress());

        builder.setNetAddress(1);
        assertEquals(1, builder.getNetAddress());
        assertPackets(expectedDataNetAddress1, builder);

        builder.setNetAddress(0);
        assertEquals(0, builder.getNetAddress());
        assertPackets(DEFAULT_PACKET, builder);

        builder.setNetAddress(127);
        assertEquals(127, builder.getNetAddress());
        assertPackets(expectedDataNetAddress127, builder);

        builder.setNetAddress(0);
        assertEquals(0, builder.getNetAddress());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withNetAddress(1));
        assertEquals(1, builder.getNetAddress());
        assertPackets(expectedDataNetAddress1, builder);

        assertSame(builder, builder.withNetAddress(0));
        assertEquals(0, builder.getNetAddress());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withNetAddress(127));
        assertEquals(127, builder.getNetAddress());
        assertPackets(expectedDataNetAddress127, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setNetAddress(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withNetAddress(128));
    }

    @Test
    void data() {

        ArtDmxBuilder builder = new ArtDmxBuilder();

        assertEquals(0, builder.getDataSize());
        assertArrayEquals(new byte[0], builder.getData());

        assertEquals(0x00, builder.getData(0));
        assertEquals(0x00, builder.getData(511));

        assertThrows(IllegalArgumentException.class, () -> builder.getData(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.getData(512));

        builder.setData(2, (byte) 0x0F);
        assertEquals(3, builder.getDataSize());
        assertEquals(0x0F, builder.getData(2));
        assertArrayEquals(new byte[]{0x00, 0x00, 0x0F}, builder.getData());
        assertPackets(getExpectedData(0x00, 0x00, 0x00, 0x00, 0x00, new byte[]{0x00, 0x00, 0x0F, 0x00}), builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setData(512, (byte) 0xFF));

        builder.setData(new byte[]{0x0F});
        assertEquals(1, builder.getDataSize());
        assertArrayEquals(new byte[]{0x0F}, builder.getData());
        assertPackets(getExpectedData(0x00, 0x00, 0x00, 0x00, 0x00, new byte[]{0x0F, 0x00}), builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setData(new byte[513]));

        assertSame(builder, builder.withData(2, (byte) 0x12));
        assertEquals(3, builder.getDataSize());
        assertEquals(0x12, builder.getData(2));
        assertArrayEquals(new byte[]{0x0F, 0x00, 0x12}, builder.getData());
        assertPackets(getExpectedData(0x00, 0x00, 0x00, 0x00, 0x00, new byte[]{0x0F, 0x00, 0x12, 0x00}), builder);

        assertThrows(IllegalArgumentException.class, () -> builder.withData(512, (byte) 0xFF));

        assertSame(builder, builder.withData(new byte[]{0x21, 0x22}));
        assertEquals(2, builder.getDataSize());
        assertArrayEquals(new byte[]{0x21, 0x22}, builder.getData());
        assertPackets(getExpectedData(0x00, 0x00, 0x00, 0x00, 0x00, new byte[]{0x21, 0x22}), builder);

        assertThrows(IllegalArgumentException.class, () -> builder.withData(new byte[513]));
    }
}
