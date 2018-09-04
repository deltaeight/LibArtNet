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

import java.util.Objects;

/**
 * Represents a port type.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.packets.ArtPollReply
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class PortType {

    public static final PortType DEFAULT = new PortType(false, false, Protocol.DMX512);

    private final boolean outputSupported;
    private final boolean inputSupported;
    private final Protocol protocol;

    public PortType(boolean outputSupported, boolean inputSupported, Protocol protocol) {
        this.outputSupported = outputSupported;
        this.inputSupported = inputSupported;
        this.protocol = protocol;
    }

    public static PortType buildFromByte(byte b) {
        return new PortType((b & 0b10000000) > 0, (b & 0b01000000) > 0, Protocol.getProtocol(b & 0b00000111));
    }

    public byte getByte() {
        byte result = 0x00;
        if (outputSupported) {
            result |= 0b10000000;
        }
        if (inputSupported) {
            result |= 0b01000000;
        }
        result |= protocol.getValue();
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputSupported, inputSupported, protocol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortType portType = (PortType) o;
        return outputSupported == portType.outputSupported &&
                inputSupported == portType.inputSupported &&
                protocol == portType.protocol;
    }

    public boolean isOutputSupported() {
        return outputSupported;
    }

    public boolean isInputSupported() {
        return inputSupported;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Represents a Protocol for input/output ports.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public enum Protocol {

        DMX512(0b00000000),
        MIDI(0b00000001),
        AVAB(0b00000010),
        COLORTRAN_CMX(0b00000011),
        ADB625(0b00000100),
        ARTNET(0b00000101);

        private final byte value;

        Protocol(int value) {
            this.value = (byte) value;
        }

        public static Protocol getProtocol(int value) {
            if (value > values().length - 1) {
                return DMX512;
            }
            return values()[value];
        }

        public byte getValue() {
            return value;
        }
    }
}
