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

import de.deltaeight.libartnet.descriptors.TimeCodeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtTimeCodeBuilderTest extends AbstractPacketBuilderTest {

    private static final byte[] DEFAULT_PACKET = getExpectedData(0, 0, 0, 0, 0);

    private static byte[] getExpectedData(int type, int hours, int minutes, int seconds, int frames) {
        return new byte[]{
                0x41, 0x72, 0x74, 0x2D, 0x4E, 0x65, 0x74, 0x00, // Header
                0x00, (byte) 0x97,                              // OpCode
                0x00, 0x0E,                                     // Protocol version
                0x00, 0x00,                                     // Filler
                (byte) frames, (byte) seconds, (byte) minutes,
                (byte) hours, (byte) type
        };
    }

    @Test
    void build() {
        assertPackets(DEFAULT_PACKET, new ArtTimeCodeBuilder());
    }

    @Test
    void type() {

        byte[] expectedData = getExpectedData(1, 0, 0, 0, 0);

        ArtTimeCodeBuilder builder = new ArtTimeCodeBuilder();

        assertSame(TimeCodeType.Film, builder.getType());

        builder.setType(TimeCodeType.EBU);
        assertSame(TimeCodeType.EBU, builder.getType());
        assertPackets(expectedData, builder);

        builder.setType(null);
        assertSame(TimeCodeType.Film, builder.getType());
        assertPackets(DEFAULT_PACKET, builder);

        assertSame(builder, builder.withType(TimeCodeType.EBU));
        assertSame(TimeCodeType.EBU, builder.getType());
        assertPackets(expectedData, builder);

        assertSame(builder, builder.withType(null));
        assertSame(TimeCodeType.Film, builder.getType());
        assertPackets(DEFAULT_PACKET, builder);
    }

    @Test
    void hours() {

        ArtTimeCodeBuilder builder = new ArtTimeCodeBuilder();

        assertEquals(0, builder.getHours());

        builder.setHours(1);
        assertEquals(1, builder.getHours());
        assertPackets(getExpectedData(0, 1, 0, 0, 0), builder);

        assertSame(builder, builder.withHours(0));
        assertEquals(0, builder.getHours());
        assertPackets(DEFAULT_PACKET, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setHours(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withHours(24));
    }

    @Test
    void minutes() {

        ArtTimeCodeBuilder builder = new ArtTimeCodeBuilder();

        assertEquals(0, builder.getMinutes());

        builder.setMinutes(1);
        assertEquals(1, builder.getMinutes());
        assertPackets(getExpectedData(0, 0, 1, 0, 0), builder);

        assertSame(builder, builder.withMinutes(0));
        assertEquals(0, builder.getMinutes());
        assertPackets(DEFAULT_PACKET, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setMinutes(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withMinutes(60));
    }

    @Test
    void seconds() {

        ArtTimeCodeBuilder builder = new ArtTimeCodeBuilder();

        assertEquals(0, builder.getSeconds());

        builder.setSeconds(1);
        assertEquals(1, builder.getSeconds());
        assertPackets(getExpectedData(0, 0, 0, 1, 0), builder);

        assertSame(builder, builder.withSeconds(0));
        assertEquals(0, builder.getSeconds());
        assertPackets(DEFAULT_PACKET, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setSeconds(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withSeconds(60));
    }

    @Test
    void frames() {

        ArtTimeCodeBuilder builder = new ArtTimeCodeBuilder();

        assertEquals(0, builder.getFrames());

        builder.setFrames(1);
        assertEquals(1, builder.getFrames());
        assertPackets(getExpectedData(0, 0, 0, 0, 1), builder);

        assertSame(builder, builder.withFrames(0));
        assertEquals(0, builder.getFrames());
        assertPackets(DEFAULT_PACKET, builder);

        assertThrows(IllegalArgumentException.class, () -> builder.setFrames(-1));
        assertThrows(IllegalArgumentException.class, () -> builder.withFrames(24));

        builder.setType(TimeCodeType.EBU);
        assertDoesNotThrow(() -> builder.setFrames(24));
        assertThrows(IllegalArgumentException.class, () -> builder.setFrames(25));

        builder.setType(TimeCodeType.DF);
        assertDoesNotThrow(() -> builder.setFrames(25));
        assertDoesNotThrow(() -> builder.setFrames(29));
        assertThrows(IllegalArgumentException.class, () -> builder.setFrames(30));

        builder.setType(TimeCodeType.SMPTE);
        assertDoesNotThrow(() -> builder.setFrames(29));
        assertThrows(IllegalArgumentException.class, () -> builder.setFrames(30));

        builder.setFrames(28);
        builder.setType(TimeCodeType.Film);
        assertEquals(23, builder.getFrames());
    }
}
