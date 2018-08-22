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

import de.deltaeight.libartnet.enums.Priority;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArtPollBuilderTest {

    private byte[] getExpectedData(int talkToMe, int priority) {
        return new byte[]{
                0x41, 0x72, 0x74, 0x2D, 0x4E, 0x65, 0x74, 0x00, // Header
                0x00, 0x20,                                     // OpCode
                0x00, 0x0E,                                     // Protocol version
                (byte) talkToMe, (byte) priority
        };
    }

    @Test
    void build() {
        assertArrayEquals(getExpectedData(0x00, 0x10), new ArtPollBuilder().build().getBytes());
    }

    @Test
    void disableVlcTransmission() {

        ArtPollBuilder builder = new ArtPollBuilder();

        assertFalse(builder.vlcTransmissionDisabled());

        builder.setDisableVlcTransmission(true);
        assertTrue(builder.vlcTransmissionDisabled());
        assertArrayEquals(getExpectedData(0x10, 0x10), builder.build().getBytes());

        builder.setDisableVlcTransmission(false);
        assertFalse(builder.vlcTransmissionDisabled());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withDisableVlcTransmission(true));
        assertTrue(builder.vlcTransmissionDisabled());
        assertArrayEquals(getExpectedData(0x10, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withDisableVlcTransmission(false));
        assertFalse(builder.vlcTransmissionDisabled());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());
    }

    @Test
    void unicastDiagnosticMessages() {

        ArtPollBuilder builder = new ArtPollBuilder();

        assertFalse(builder.unicastDiagnosticMessages());

        builder.setUnicastDiagnosticMessages(true);
        assertTrue(builder.unicastDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x08, 0x10), builder.build().getBytes());

        builder.setUnicastDiagnosticMessages(false);
        assertFalse(builder.unicastDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withUnicastDiagnosticMessages(true));
        assertTrue(builder.unicastDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x08, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withUnicastDiagnosticMessages(false));
        assertFalse(builder.unicastDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());
    }

    @Test
    void sendDiagnosticMessages() {

        ArtPollBuilder builder = new ArtPollBuilder();

        assertFalse(builder.sendDiagnosticMessages());

        builder.setSendDiagnosticMessages(true);
        assertTrue(builder.sendDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x04, 0x10), builder.build().getBytes());

        builder.setSendDiagnosticMessages(false);
        assertFalse(builder.sendDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withSendDiagnosticMessages(true));
        assertTrue(builder.sendDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x04, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withSendDiagnosticMessages(false));
        assertFalse(builder.sendDiagnosticMessages());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());
    }

    @Test
    void sendArtPollReplyOnChanges() {

        ArtPollBuilder builder = new ArtPollBuilder();

        assertFalse(builder.sendArtPollReplyOnChanges());

        builder.setSendArtPollReplyOnChanges(true);
        assertTrue(builder.sendArtPollReplyOnChanges());
        assertArrayEquals(getExpectedData(0x02, 0x10), builder.build().getBytes());

        builder.setSendArtPollReplyOnChanges(false);
        assertFalse(builder.sendArtPollReplyOnChanges());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withSendArtPollReplyOnChanges(true));
        assertTrue(builder.sendArtPollReplyOnChanges());
        assertArrayEquals(getExpectedData(0x02, 0x10), builder.build().getBytes());

        assertSame(builder, builder.withSendArtPollReplyOnChanges(false));
        assertFalse(builder.sendArtPollReplyOnChanges());
        assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());
    }

    @Test
    void booleanProperties() {

        ArtPollBuilder builder = new ArtPollBuilder()
                .withDisableVlcTransmission(true)
                .withUnicastDiagnosticMessages(true)
                .withSendDiagnosticMessages(true)
                .withSendArtPollReplyOnChanges(true);

        assertArrayEquals(getExpectedData(0x1E, 0x10), builder.build().getBytes());
    }

    @Test
    void priority() {

        Priority[] testValues = {Priority.Medium, Priority.High, Priority.Critical, Priority.Volatile, Priority.Low};
        byte[] testBytes = {0x40, (byte) 0x80, (byte) 0xE0, (byte) 0xF0, 0x10};

        ArtPollBuilder builder = new ArtPollBuilder();

        assertSame(Priority.Low, builder.getPriority());

        for (int i = 0; i < testValues.length; i++) {

            Priority testValue = testValues[i];
            byte testByte = testBytes[i];

            builder.setPriority(testValue);
            assertSame(builder.getPriority(), testValue);
            assertArrayEquals(getExpectedData(0x00, testByte), builder.build().getBytes());

            assertSame(builder, builder.withPriority(null));
            assertSame(builder.getPriority(), Priority.Low);
            assertArrayEquals(getExpectedData(0x00, 0x10), builder.build().getBytes());
        }
    }
}