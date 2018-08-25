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

package de.deltaeight.libartnet;

import de.deltaeight.libartnet.enums.EquipmentStyle;
import de.deltaeight.libartnet.enums.OemCode;
import de.deltaeight.libartnet.packet.ArtPollReply;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class ArtPollReplyBuilderTest {

    private static final int[] DEFAULT_ESTA_MAN_BYTES = new int[]{0x38, 0x44};
    private static final int[] DEFAULT_OEM_BYTES = new int[]{0x7F, 0xFF};
    private static final byte[] DEFAULT_NAMES = new byte[]{0x4C, 0x69, 0x62, 0x41, 0x72, 0x74, 0x4E, 0x65, 0x74};
    private static final int DEFAULT_EQUIPMENT_STYLE = 0x05;

    private static final byte[] DEFAULT_PACKET = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
            DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
            new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
            0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

    private static byte[] getExpectedData(byte[] ipAddress,
                                          int[] nodeFirmwareVersion,
                                          int netSwitch,
                                          int subSwitch,
                                          int[] oemBytes,
                                          int ubeaVersion,
                                          int status1,
                                          int[] estaManBytes,
                                          byte[] shortName,
                                          byte[] longName,
                                          byte[] nodeReport,
                                          int numPorts,
                                          int[] portTypes,
                                          int[] inputStatuses,
                                          int[] outputStatuses,
                                          int[] inputUniverseAddresses,
                                          int[] outputUniverseAddresses,
                                          int macrosActive,
                                          int remotesActive,
                                          int equipmentStyle,
                                          byte[] macAddress,
                                          byte[] bindIp,
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


        tmp[211] = bindIndex;
        tmp[212] = status2;

        // bytes 209 through 238 are spare and not used yet

        byte[] result = new byte[tmp.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) tmp[i];
        }

        System.arraycopy(shortName, 0, result, 26, Math.min(shortName.length, 18));
        System.arraycopy(longName, 0, result, 44, Math.min(longName.length, 64));
        System.arraycopy(nodeReport, 0, result, 108, Math.min(nodeReport.length, 64));

        System.arraycopy(macAddress, 0, result, 201, 6);
        System.arraycopy(bindIp, 0, result, 207, 4);

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
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

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
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

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
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

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
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

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

    @Test
    void oemCode() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                new int[]{0x00, 0x00}, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES,
                DEFAULT_NAMES, DEFAULT_NAMES, new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4],
                new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4],
                0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertSame(OemCode.UNKNOWN, builder.getOemCode());

        builder.setOemCode(OemCode.OemDMXHub);
        assertSame(OemCode.OemDMXHub, builder.getOemCode());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setOemCode(null);
        assertSame(OemCode.UNKNOWN, builder.getOemCode());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withOemCode(OemCode.OemDMXHub));
        assertSame(OemCode.OemDMXHub, builder.getOemCode());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withOemCode(null));
        assertSame(OemCode.UNKNOWN, builder.getOemCode());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void ubeaVersion() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x2A, 0x00, DEFAULT_ESTA_MAN_BYTES,
                DEFAULT_NAMES, DEFAULT_NAMES, new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4],
                new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4],
                0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals(0, builder.getUbeaVersion());

        builder.setUbeaVersion(42);
        assertEquals(42, builder.getUbeaVersion());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setUbeaVersion(0);
        assertEquals(0, builder.getUbeaVersion());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withUbeaVersion(42));
        assertEquals(42, builder.getUbeaVersion());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withUbeaVersion(0));
        assertEquals(0, builder.getUbeaVersion());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IllegalArgumentException.class, () -> builder.setUbeaVersion(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withUbeaVersion(256));
    }

    @Test
    void indicatorState() {

        byte[][] expectedData = {
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b00000000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b01000000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b10000000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b11000000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00)
        };

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertSame(ArtPollReply.IndicatorState.Unknown, builder.getIndicatorState());

        for (int i = 0; i < 4; i++) {

            ArtPollReply.IndicatorState currentState = ArtPollReply.IndicatorState.values()[i];
            byte[] expectedDatum = expectedData[i];

            builder.setIndicatorState(currentState);
            assertSame(currentState, builder.getIndicatorState());
            assertArrayEquals(expectedDatum, builder.build().getBytes());

            builder.setIndicatorState(null);
            assertSame(ArtPollReply.IndicatorState.Unknown, builder.getIndicatorState());
            assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

            assertSame(builder, builder.withIndicatorState(currentState));
            assertSame(currentState, builder.getIndicatorState());
            assertArrayEquals(expectedDatum, builder.build().getBytes());

            assertSame(builder, builder.withIndicatorState(null));
            assertSame(ArtPollReply.IndicatorState.Unknown, builder.getIndicatorState());
            assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
        }
    }

    @Test
    void portAddressingAuthority() {

        byte[][] expectedData = {
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b00000000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b00010000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b00100000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                        0x00, 0b00110000, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                        0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                        0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00)
        };

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertSame(ArtPollReply.IndicatorState.Unknown, builder.getIndicatorState());

        for (int i = 0; i < 4; i++) {

            ArtPollReply.PortAddressingAuthority currentState = ArtPollReply.PortAddressingAuthority.values()[i];
            byte[] expectedDatum = expectedData[i];

            builder.setPortAddressingAuthority(currentState);
            assertSame(currentState, builder.getPortAddressingAuthority());
            assertArrayEquals(expectedDatum, builder.build().getBytes());

            builder.setPortAddressingAuthority(null);
            assertSame(ArtPollReply.PortAddressingAuthority.Unknown, builder.getPortAddressingAuthority());
            assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

            assertSame(builder, builder.withPortAddressingAuthority(currentState));
            assertSame(currentState, builder.getPortAddressingAuthority());
            assertArrayEquals(expectedDatum, builder.build().getBytes());

            assertSame(builder, builder.withPortAddressingAuthority(null));
            assertSame(ArtPollReply.PortAddressingAuthority.Unknown, builder.getPortAddressingAuthority());
            assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
        }
    }

    @Test
    void bootedFromRom() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0b00000100, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.isBootedFromRom());

        builder.setBootedFromRom(true);
        assertTrue(builder.isBootedFromRom());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setBootedFromRom(false);
        assertFalse(builder.isBootedFromRom());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withBootedFromRom(true));
        assertTrue(builder.isBootedFromRom());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withBootedFromRom(false));
        assertFalse(builder.isBootedFromRom());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void rdmSupport() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0b00000010, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.supportsRdm());

        builder.setRdmSupport(true);
        assertTrue(builder.supportsRdm());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setRdmSupport(false);
        assertFalse(builder.supportsRdm());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withRdmSupport(true));
        assertTrue(builder.supportsRdm());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withRdmSupport(false));
        assertFalse(builder.supportsRdm());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void ubeaPresent() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0b00000001, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.isUbeaPresent());

        builder.setUbeaPresent(true);
        assertTrue(builder.isUbeaPresent());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setUbeaPresent(false);
        assertFalse(builder.isUbeaPresent());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withUbeaPresent(true));
        assertTrue(builder.isUbeaPresent());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withUbeaPresent(false));
        assertFalse(builder.isUbeaPresent());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void status1() {

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder()
                .withIndicatorState(ArtPollReply.IndicatorState.Normal)
                .withPortAddressingAuthority(ArtPollReply.PortAddressingAuthority.NotUsed)
                .withBootedFromRom(true)
                .withRdmSupport(true)
                .withUbeaPresent(true);

        assertArrayEquals(getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0b11110111, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00),
                builder.build().getBytes());
    }

    @Test
    void estaManufacturer() {

        byte[] expectedDataWithEmptyManufacturer = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, new int[2], DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, new int[]{"o".getBytes()[0], "T".getBytes()[0]}, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals("D8", builder.getEstaManufacturer());

        builder.setEstaManufacturer("Too long!");
        assertEquals("To", builder.getEstaManufacturer());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setEstaManufacturer(null);
        assertNotNull(builder.getEstaManufacturer());
        assertTrue(builder.getEstaManufacturer().isEmpty());
        assertArrayEquals(expectedDataWithEmptyManufacturer, builder.build().getBytes());

        assertSame(builder, builder.withEstaManufacturer("To"));
        assertEquals("To", builder.getEstaManufacturer());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withEstaManufacturer(null));
        assertNotNull(builder.getEstaManufacturer());
        assertTrue(builder.getEstaManufacturer().isEmpty());
        assertArrayEquals(expectedDataWithEmptyManufacturer, builder.build().getBytes());
    }

    @Test
    void shortName() {

        byte[] expectedDataWithEmptyName = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, new byte[0], DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, "Too Long Name For\0".getBytes(), DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals("LibArtNet", builder.getShortName());

        builder.setShortName("Too Long Name For This!");
        assertEquals("Too Long Name For", builder.getShortName());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setShortName(null);
        assertNotNull(builder.getShortName());
        assertTrue(builder.getShortName().isEmpty());
        assertArrayEquals(expectedDataWithEmptyName, builder.build().getBytes());

        assertSame(builder, builder.withShortName("Too Long Name For This!"));
        assertEquals("Too Long Name For", builder.getShortName());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withShortName(null));
        assertNotNull(builder.getShortName());
        assertTrue(builder.getShortName().isEmpty());
        assertArrayEquals(expectedDataWithEmptyName, builder.build().getBytes());
    }

    @Test
    void longName() {

        String testString = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu";

        byte[] expectedDataWithEmptyName = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, new byte[0],
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES,
                (testString.substring(0, 63) + "\0").getBytes(), new byte[0], 0x00, new int[4], new int[4],
                new int[4], new int[4], new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6],
                new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals("LibArtNet", builder.getLongName());

        builder.setLongName(testString);
        assertEquals(testString.substring(0, 63), builder.getLongName());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setLongName(null);
        assertNotNull(builder.getLongName());
        assertTrue(builder.getLongName().isEmpty());
        assertArrayEquals(expectedDataWithEmptyName, builder.build().getBytes());

        assertSame(builder, builder.withLongName(testString));
        assertEquals(testString.substring(0, 63), builder.getLongName());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withLongName(null));
        assertNotNull(builder.getLongName());
        assertTrue(builder.getLongName().isEmpty());
        assertArrayEquals(expectedDataWithEmptyName, builder.build().getBytes());
    }

    @Test
    void nodeReport() {

        String testString = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonu";

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                (testString.substring(0, 63) + "\0").getBytes(), 0x00, new int[4], new int[4],
                new int[4], new int[4], new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6],
                new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertNotNull(builder.getNodeReport());
        assertTrue(builder.getNodeReport().isEmpty());

        builder.setNodeReport(testString);
        assertEquals(testString.substring(0, 63), builder.getNodeReport());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setNodeReport(null);
        assertNotNull(builder.getNodeReport());
        assertTrue(builder.getNodeReport().isEmpty());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withNodeReport(testString));
        assertEquals(testString.substring(0, 63), builder.getNodeReport());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withNodeReport(null));
        assertNotNull(builder.getNodeReport());
        assertTrue(builder.getNodeReport().isEmpty());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void portTypes() {

        ArtPollReply.PortType[] testValues = new ArtPollReply.PortType[]{
                new ArtPollReply.PortType(false, false, ArtPollReply.PortType.Protocol.ARTNET),
                new ArtPollReply.PortType(false, true, ArtPollReply.PortType.Protocol.MIDI),
                new ArtPollReply.PortType(true, false, ArtPollReply.PortType.Protocol.AVAB),
                new ArtPollReply.PortType(true, true, ArtPollReply.PortType.Protocol.ADB625)
        };

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x03, new int[]{0b00000101, 0b01000001, 0b10000010, 0b11000100}, new int[4],
                new int[4], new int[4], new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE,
                new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReply.PortType[] defaultTypes = new ArtPollReply.PortType[]{ArtPollReply.PortType.DEFAULT,
                ArtPollReply.PortType.DEFAULT, ArtPollReply.PortType.DEFAULT, ArtPollReply.PortType.DEFAULT};

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(defaultTypes, builder.getPortTypes());

        for (int i = 0; i < 4; i++) {
            builder.setPortType(i, testValues[i]);
            assertSame(testValues[i], builder.getPortType(i));
        }

        assertArrayEquals(testValues, builder.getPortTypes());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            builder.setPortType(i, null);
            assertSame(defaultTypes[i], builder.getPortType(i));
        }

        assertArrayEquals(defaultTypes, builder.getPortTypes());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withPortType(i, testValues[i]));
            assertSame(testValues[i], builder.getPortType(i));
        }

        assertArrayEquals(testValues, builder.getPortTypes());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withPortType(i, null));
            assertSame(defaultTypes[i], builder.getPortType(i));
        }

        assertArrayEquals(defaultTypes, builder.getPortTypes());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setPortType(-1, ArtPollReply.PortType.DEFAULT));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withPortType(4, ArtPollReply.PortType.DEFAULT));
    }

    @Test
    void inputStatuses() {

        ArtPollReply.InputStatus[] testValues = new ArtPollReply.InputStatus[]{
                new ArtPollReply.InputStatus(false, false, false, false, false, false),
                new ArtPollReply.InputStatus(false, false, false, false, false, true),
                new ArtPollReply.InputStatus(false, false, false, false, true, false),
                new ArtPollReply.InputStatus(false, false, false, false, true, true)
        };

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[]{0b00000000, 0b00000100, 0b00001000, 0b00001100},
                new int[4], new int[4], new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE,
                new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReply.InputStatus[] defaultStatuses = new ArtPollReply.InputStatus[]{ArtPollReply.InputStatus.DEFAULT,
                ArtPollReply.InputStatus.DEFAULT, ArtPollReply.InputStatus.DEFAULT, ArtPollReply.InputStatus.DEFAULT};

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(defaultStatuses, builder.getInputStatuses());

        for (int i = 0; i < 4; i++) {
            builder.setInputStatus(i, testValues[i]);
            assertSame(testValues[i], builder.getInputStatus(i));
        }

        assertArrayEquals(testValues, builder.getInputStatuses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            builder.setInputStatus(i, null);
            assertSame(defaultStatuses[i], builder.getInputStatus(i));
        }

        assertArrayEquals(defaultStatuses, builder.getInputStatuses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withInputStatus(i, testValues[i]));
            assertSame(testValues[i], builder.getInputStatus(i));
        }

        assertArrayEquals(testValues, builder.getInputStatuses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withInputStatus(i, null));
            assertSame(defaultStatuses[i], builder.getInputStatus(i));
        }

        assertArrayEquals(defaultStatuses, builder.getInputStatuses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setInputStatus(-1, ArtPollReply.InputStatus.DEFAULT));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withInputStatus(4, ArtPollReply.InputStatus.DEFAULT));
    }

    @Test
    void outputStatuses() {

        ArtPollReply.OutputStatus[] testValues = new ArtPollReply.OutputStatus[]{
                new ArtPollReply.OutputStatus(false, false, false, false, false, false, false, false),
                new ArtPollReply.OutputStatus(false, false, false, false, false, false, false, true),
                new ArtPollReply.OutputStatus(false, false, false, false, false, false, true, false),
                new ArtPollReply.OutputStatus(false, false, false, false, false, false, true, true)
        };

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[]{0b00000000, 0b00000001, 0b00000010, 0b00000011},
                new int[4], new int[4], 0x00, 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4],
                0x00, 0x00);

        ArtPollReply.OutputStatus[] defaultStatuses = new ArtPollReply.OutputStatus[]{ArtPollReply.OutputStatus.DEFAULT,
                ArtPollReply.OutputStatus.DEFAULT, ArtPollReply.OutputStatus.DEFAULT, ArtPollReply.OutputStatus.DEFAULT};

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(defaultStatuses, builder.getOutputStatuses());

        for (int i = 0; i < 4; i++) {
            assertSame(defaultStatuses[i], builder.getOutputStatus(i));
            builder.setOutputStatus(i, testValues[i]);
            assertSame(testValues[i], builder.getOutputStatus(i));
        }

        assertArrayEquals(testValues, builder.getOutputStatuses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            builder.setOutputStatus(i, null);
            assertSame(defaultStatuses[i], builder.getOutputStatus(i));
        }

        assertArrayEquals(defaultStatuses, builder.getOutputStatuses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withOutputStatus(i, testValues[i]));
            assertSame(testValues[i], builder.getOutputStatus(i));
        }

        assertArrayEquals(testValues, builder.getOutputStatuses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withOutputStatus(i, null));
            assertSame(defaultStatuses[i], builder.getOutputStatus(i));
        }

        assertArrayEquals(defaultStatuses, builder.getOutputStatuses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setOutputStatus(-1, ArtPollReply.OutputStatus.DEFAULT));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withOutputStatus(4, ArtPollReply.OutputStatus.DEFAULT));
    }

    @Test
    void inputUniverseAddresses() {

        int[] testValues = new int[]{0x00, 0x01, 0x02, 0x03};
        int[] defaultValues = new int[4];

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], testValues, new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(defaultValues, builder.getInputUniverseAddresses());

        for (int i = 0; i < 4; i++) {
            assertEquals(0x00, builder.getInputUniverseAddress(i));
            builder.setInputUniverseAddress(i, testValues[i]);
            assertEquals(testValues[i], builder.getInputUniverseAddress(i));
        }

        assertArrayEquals(testValues, builder.getInputUniverseAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            builder.setInputUniverseAddress(i, 0x00);
            assertEquals(0x00, builder.getInputUniverseAddress(i));
        }

        assertArrayEquals(defaultValues, builder.getInputUniverseAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withInputUniverseAddress(i, testValues[i]));
            assertEquals(testValues[i], builder.getInputUniverseAddress(i));
        }

        assertArrayEquals(testValues, builder.getInputUniverseAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withInputUniverseAddress(i, 0x00));
            assertEquals(0x00, builder.getInputUniverseAddress(i));
        }

        assertArrayEquals(defaultValues, builder.getInputUniverseAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setInputUniverseAddress(-1, 0x00));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withInputUniverseAddress(4, 0x00));
    }

    @Test
    void outputUniverseAddresses() {

        int[] testValues = new int[]{0x00, 0x01, 0x02, 0x03};
        int[] defaultValues = new int[4];

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], testValues, 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(defaultValues, builder.getOutputUniverseAddresses());

        for (int i = 0; i < 4; i++) {
            assertEquals(0x00, builder.getOutputUniverseAddress(i));
            builder.setOutputUniverseAddress(i, testValues[i]);
            assertEquals(testValues[i], builder.getOutputUniverseAddress(i));
        }

        assertArrayEquals(testValues, builder.getOutputUniverseAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            builder.setOutputUniverseAddress(i, 0x00);
            assertEquals(0x00, builder.getOutputUniverseAddress(i));
        }

        assertArrayEquals(defaultValues, builder.getOutputUniverseAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withOutputUniverseAddress(i, testValues[i]));
            assertEquals(testValues[i], builder.getOutputUniverseAddress(i));
        }

        assertArrayEquals(testValues, builder.getOutputUniverseAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        for (int i = 0; i < 4; i++) {
            assertSame(builder, builder.withOutputUniverseAddress(i, 0x00));
            assertEquals(0x00, builder.getOutputUniverseAddress(i));
        }

        assertArrayEquals(defaultValues, builder.getOutputUniverseAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setOutputUniverseAddress(-1, 0x00));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withOutputUniverseAddress(4, 0x00));
    }

    @Test
    void macrosActive() {

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        boolean[] currentMacrosActive = new boolean[8];

        assertArrayEquals(currentMacrosActive, builder.getMacrosActive());

        for (int i = 0; i < 8; i++) {

            byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                    DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                    new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                    (int) (Math.pow(2, i + 1) - 1), 0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4],
                    0x00, 0x00);

            currentMacrosActive[i] = true;

            builder.setMacroActive(i, true);
            assertTrue(builder.isMacroActive(i));
            assertArrayEquals(currentMacrosActive, builder.getMacrosActive());
            assertArrayEquals(expectedData, builder.build().getBytes());
        }

        for (int i = 0; i < 8; i++) {

            byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                    DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                    new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                    0xFF - (int) (Math.pow(2, i + 1) - 1), 0x00, DEFAULT_EQUIPMENT_STYLE,
                    new byte[6], new byte[4], 0x00, 0x00);

            currentMacrosActive[i] = false;

            assertSame(builder, builder.withMacroActive(i, false));
            assertFalse(builder.isMacroActive(i));
            assertArrayEquals(currentMacrosActive, builder.getMacrosActive());
            assertArrayEquals(expectedData, builder.build().getBytes());
        }

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setMacroActive(-1, true));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withMacroActive(8, true));
    }

    @Test
    void remotesActive() {

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        boolean[] currentRemotesActive = new boolean[8];

        assertArrayEquals(currentRemotesActive, builder.getRemotesActive());

        for (int i = 0; i < 8; i++) {

            byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                    DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                    new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                    0x00, (int) (Math.pow(2, i + 1) - 1), DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4],
                    0x00, 0x00);

            currentRemotesActive[i] = true;

            builder.setRemoteActive(i, true);
            assertTrue(builder.isRemoteActive(i));
            assertArrayEquals(currentRemotesActive, builder.getRemotesActive());
            assertArrayEquals(expectedData, builder.build().getBytes());
        }

        for (int i = 0; i < 8; i++) {

            byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                    DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                    new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4],
                    0x00,0xFF - (int) (Math.pow(2, i + 1) - 1), DEFAULT_EQUIPMENT_STYLE,
                    new byte[6], new byte[4], 0x00, 0x00);

            currentRemotesActive[i] = false;

            assertSame(builder, builder.withRemoteActive(i, false));
            assertFalse(builder.isRemoteActive(i));
            assertArrayEquals(currentRemotesActive, builder.getRemotesActive());
            assertArrayEquals(expectedData, builder.build().getBytes());
        }

        assertThrows(IndexOutOfBoundsException.class, () -> builder.setRemoteActive(-1, true));
        assertThrows(IndexOutOfBoundsException.class, () -> builder.withRemoteActive(8, true));
    }

    @Test
    void equipmentStyle() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, 0x01, new byte[6], new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertSame(EquipmentStyle.Config, builder.getEquipmentStyle());

        builder.setEquipmentStyle(EquipmentStyle.Controller);
        assertSame(EquipmentStyle.Controller, builder.getEquipmentStyle());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setEquipmentStyle(null);
        assertSame(EquipmentStyle.Config, builder.getEquipmentStyle());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withEquipmentStyle(EquipmentStyle.Controller));
        assertSame(EquipmentStyle.Controller, builder.getEquipmentStyle());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withEquipmentStyle(null));
        assertSame(EquipmentStyle.Config, builder.getEquipmentStyle());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void macAddress() {

        byte[] testValue = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05};

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, testValue, new byte[4], 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertArrayEquals(new byte[6], builder.getMacAddress());

        builder.setMacAddress(testValue);
        assertArrayEquals(testValue, builder.getMacAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setMacAddress(null);
        assertArrayEquals(new byte[6], builder.getMacAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withMacAddress(testValue));
        assertArrayEquals(testValue, builder.getMacAddress());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withMacAddress(null));
        assertArrayEquals(new byte[6], builder.getMacAddress());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void bindIp() throws UnknownHostException {

        Inet4Address testAddress = (Inet4Address) InetAddress.getByAddress(new byte[]{0x7F, 0x00, 0x00, 0x00});

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], testAddress.getAddress(), 0x00, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertNull(builder.getBindIp());

        builder.setBindIp(testAddress);
        assertEquals(testAddress, builder.getBindIp());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setBindIp(null);
        assertNull(builder.getBindIp());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withBindIp(testAddress));
        assertEquals(testAddress, builder.getBindIp());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withBindIp(null));
        assertNull(builder.getBindIp());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void bindIndex() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00,
                DEFAULT_OEM_BYTES, 0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES,
                new byte[0], 0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x01, 0x00);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertEquals(0, builder.getBindIndex());

        builder.setBindIndex(1);
        assertEquals(1, builder.getBindIndex());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setBindIndex(0);
        assertEquals(0, builder.getBindIndex());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withBindIndex(1));
        assertEquals(1, builder.getBindIndex());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withBindIndex(0));
        assertEquals(0, builder.getBindIndex());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertThrows(IllegalArgumentException.class, () -> builder.setBindIndex(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withBindIndex(256));
    }

    @Test
    void webBrowserConfigurationSupport() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00000001);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.supportsWebBrowserConfiguration());

        builder.setWebBrowserConfigurationSupport(true);
        assertTrue(builder.supportsWebBrowserConfiguration());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setWebBrowserConfigurationSupport(false);
        assertFalse(builder.supportsWebBrowserConfiguration());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withWebBrowserConfigurationSupport(true));
        assertTrue(builder.supportsWebBrowserConfiguration());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withWebBrowserConfigurationSupport(false));
        assertFalse(builder.supportsWebBrowserConfiguration());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void ipIsDhcpConfigured() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00000010);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.ipIsDhcpConfigured());

        builder.setIpIsDhcpConfigured(true);
        assertTrue(builder.ipIsDhcpConfigured());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setIpIsDhcpConfigured(false);
        assertFalse(builder.ipIsDhcpConfigured());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withIpIsDhcpConfigured(true));
        assertTrue(builder.ipIsDhcpConfigured());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withIpIsDhcpConfigured(false));
        assertFalse(builder.ipIsDhcpConfigured());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void dhcpSupport() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00000100);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.supportsDhcp());

        builder.setDhcpSupport(true);
        assertTrue(builder.supportsDhcp());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setDhcpSupport(false);
        assertFalse(builder.supportsDhcp());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withDhcpSupport(true));
        assertTrue(builder.supportsDhcp());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withDhcpSupport(false));
        assertFalse(builder.supportsDhcp());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void longPortAddressSupport() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00001000);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.supportsLongPortAddresses());

        builder.setLongPortAddressSupport(true);
        assertTrue(builder.supportsLongPortAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setLongPortAddressSupport(false);
        assertFalse(builder.supportsLongPortAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withLongPortAddressSupport(true));
        assertTrue(builder.supportsLongPortAddresses());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withLongPortAddressSupport(false));
        assertFalse(builder.supportsLongPortAddresses());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void canSwitchToSACN() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00010000);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.canSwitchToSACN());

        builder.setCanSwitchToSACN(true);
        assertTrue(builder.canSwitchToSACN());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setCanSwitchToSACN(false);
        assertFalse(builder.canSwitchToSACN());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withCanSwitchToSACN(true));
        assertTrue(builder.canSwitchToSACN());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withCanSwitchToSACN(false));
        assertFalse(builder.canSwitchToSACN());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void squawking() {

        byte[] expectedData = getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00100000);

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder();

        assertFalse(builder.isSquawking());

        builder.setSquawking(true);
        assertTrue(builder.isSquawking());
        assertArrayEquals(expectedData, builder.build().getBytes());

        builder.setSquawking(false);
        assertFalse(builder.isSquawking());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());

        assertSame(builder, builder.withSquawking(true));
        assertTrue(builder.isSquawking());
        assertArrayEquals(expectedData, builder.build().getBytes());

        assertSame(builder, builder.withSquawking(false));
        assertFalse(builder.isSquawking());
        assertArrayEquals(DEFAULT_PACKET, builder.build().getBytes());
    }

    @Test
    void status2() {

        ArtPollReplyBuilder builder = new ArtPollReplyBuilder()
                .withWebBrowserConfigurationSupport(true)
                .withIpIsDhcpConfigured(true)
                .withDhcpSupport(true)
                .withLongPortAddressSupport(true)
                .withCanSwitchToSACN(true)
                .withSquawking(true);

        assertArrayEquals(getExpectedData(new byte[4], new int[2], 0x00, 0x00, DEFAULT_OEM_BYTES,
                0x00, 0x00, DEFAULT_ESTA_MAN_BYTES, DEFAULT_NAMES, DEFAULT_NAMES, new byte[0],
                0x00, new int[4], new int[4], new int[4], new int[4], new int[4], 0x00,
                0x00, DEFAULT_EQUIPMENT_STYLE, new byte[6], new byte[4], 0x00, 0b00111111),
                builder.build().getBytes());
    }
}
