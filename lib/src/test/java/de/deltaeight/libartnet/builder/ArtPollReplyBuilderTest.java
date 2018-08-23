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

package de.deltaeight.libartnet.builder;

import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ArtPollReplyBuilderTest {

    private static final int[] DEFAULT_ESTA_MAN_BYTES = new int[]{0x38, 0x44};
    private static final int[] DEFAULT_OEM_BYTES = new int[]{0x7F, 0xFF};
    private static final int[] DEFAULT_NAMES = new int[]{0x4C, 0x69, 0x62, 0x41, 0x72, 0x74, 0x4E, 0x65, 0x74};
    private static final int DEFAULT_EQUIPMENT_STYLE = 0x05;

    private static final byte[] DEFAULT_PACKET = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
            DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
            new int[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
            0x00, DEFAULT_EQUIPMENT_STYLE, new int[6], new int[4], 0x00, 0x00);

    private static byte[] getExpectedData(byte[] ipAddress,
                                          int[] nodeFirmwareVersion,
                                          int netSwitch,
                                          int subSwitch,
                                          int[] oemBytes,
                                          int ubeaVersion,
                                          int status1,
                                          int[] estaManBytes,
                                          int[] shortName,
                                          int[] longName,
                                          int[] nodeReport,
                                          int numPorts,
                                          int[] portTypes,
                                          int[] inputStatuses,
                                          int[] outputStatuses,
                                          int[] inputUniverseAddresses,
                                          int[] outputUniverseAddresses,
                                          int macrosActive,
                                          int remotesActive,
                                          int equipmentStyle,
                                          int[] macAddress,
                                          int[] bindIp,
                                          int bindIndex,
                                          int status2) {

        int[] tmp = new int[239];

        System.arraycopy(new int[]{
                0x41, 0x72, 0x74, 0x2D, 0x4E, 0x65, 0x74, 0x00, // Header
                0x00, 0x21,                                     // OpCode
        }, 0, tmp, 0, 10);

        int[] tmpIpAddress = new int[4];
        for (int i = 0; i < tmpIpAddress.length; i++) {
            tmpIpAddress[i] = ipAddress[i];
        }

        System.arraycopy(tmpIpAddress, 0, tmp, 10, 4);
        System.arraycopy(new int[]{0x36, 0x19}, 0, tmp, 14, 2); // Port
        System.arraycopy(nodeFirmwareVersion, 0, tmp, 16, 2);

        tmp[18] = netSwitch;
        tmp[19] = subSwitch;

        System.arraycopy(oemBytes, 0, tmp, 20, 2);

        tmp[22] = ubeaVersion;
        tmp[23] = status1;

        System.arraycopy(estaManBytes, 0, tmp, 24, 2);
        System.arraycopy(shortName, 0, tmp, 26, Math.min(shortName.length, 18));
        System.arraycopy(longName, 0, tmp, 44, Math.min(longName.length, 64));
        System.arraycopy(nodeReport, 0, tmp, 108, Math.min(nodeReport.length, 64));

        tmp[172] = 0x00;     // NumPortsHi
        tmp[173] = numPorts;

        System.arraycopy(portTypes, 0, tmp, 174, 4);
        System.arraycopy(inputStatuses, 0, tmp, 178, 4);
        System.arraycopy(outputStatuses, 0, tmp, 182, 4);
        System.arraycopy(inputUniverseAddresses, 0, tmp, 186, 4);
        System.arraycopy(outputUniverseAddresses, 0, tmp, 190, 4);

        tmp[194] = 0x00;     // SwVideo, deprecated
        tmp[195] = macrosActive;
        tmp[196] = remotesActive;

        // bytes 197 through 199 are spare and not used yet

        tmp[200] = equipmentStyle;

        System.arraycopy(macAddress, 0, tmp, 201, 6);
        System.arraycopy(bindIp, 0, tmp, 207, 4);

        tmp[211] = bindIndex;
        tmp[212] = status2;

        // bytes 209 through 238 are spare and not used yet

        byte[] result = new byte[tmp.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) tmp[i];
        }

        return result;
    }

    @Test
    void build() {
        assertArrayEquals(DEFAULT_PACKET, new ArtPollReplyBuilder().build().getBytes());
    }

    @Test
    void ipAddress() throws UnknownHostException {

        Inet4Address testAddress = (Inet4Address) InetAddress.getByAddress(new byte[]{0x7F, 0x00, 0x00, 0x00});
        byte[] expectedData = getExpectedData(testAddress.getAddress(), new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new int[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new int[6], new int[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertNull(builder.getIpAddress());

        builder.setIpAddress(testAddress);
        assertEquals(testAddress, builder.getIpAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setIpAddress(null);
        assertNull(builder.getIpAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withIpAddress(testAddress));
        assertEquals(testAddress, builder.getIpAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withIpAddress(null));
        assertNull(builder.getIpAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void nodeVersion() {

        int[] nodeVersion = new int[2];
        nodeVersion[0] = 666 >> 8;
        nodeVersion[1] = 666;

        byte[] expectedData = getExpectedData(new byte[4], nodeVersion, 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new int[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new int[6], new int[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals(0, builder.getNodeVersion());

        builder.setNodeVersion(666);
        assertEquals(666, builder.getNodeVersion());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setNodeVersion(0);
        assertEquals(0, builder.getNodeVersion());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withNodeVersion(666));
        assertEquals(666, builder.getNodeVersion());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withNodeVersion(0));
        assertEquals(0, builder.getNodeVersion());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void netAddress() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x2A, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new int[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new int[6], new int[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals(0, builder.getNetAddress());

        builder.setNetAddress(42);
        assertEquals(42, builder.getNetAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setNetAddress(0);
        assertEquals(0, builder.getNetAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withNetAddress(42));
        assertEquals(42, builder.getNetAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withNetAddress(0));
        assertEquals(0, builder.getNetAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IllegalArgumentException.class, () -> builder.setNetAddress(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withNetAddress(128));
    }
    
    @Test
    void subnetAddress() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x0D,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new int[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new int[6], new int[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals(0, builder.getSubnetAddress());

        builder.setSubnetAddress(13);
        assertEquals(13, builder.getSubnetAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setSubnetAddress(0);
        assertEquals(0, builder.getSubnetAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withSubnetAddress(13));
        assertEquals(13, builder.getSubnetAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withSubnetAddress(0));
        assertEquals(0, builder.getSubnetAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IllegalArgumentException.class, () -> builder.setSubnetAddress(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withSubnetAddress(16));
    }
}
