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
 * Represents the status of an input port.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.packets.ArtPollReply
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class InputStatus extends PortStatus {

    public static final InputStatus DEFAULT = new InputStatus(false, false, false, false, false, false);

    private final boolean dataReceived;
    private final boolean inputDisabled;
    private final boolean errorsDetected;

    public InputStatus(boolean dataReceived,
                       boolean includesTestPackets,
                       boolean includesSIPs,
                       boolean includesTextPackets,
                       boolean inputDisabled,
                       boolean errorsDetected) {

        super(includesTestPackets, includesSIPs, includesTextPackets);

        this.dataReceived = dataReceived;
        this.inputDisabled = inputDisabled;
        this.errorsDetected = errorsDetected;
    }

    public static InputStatus buildFromByte(byte b) {
        return new InputStatus((b & 0b10000000) > 0, (b & 0b01000000) > 0, (b & 0b00100000) > 0,
                (b & 0b00010000) > 0, (b & 0b00001000) > 0, (b & 0b00000100) > 0);
    }

    public byte getByte() {
        byte result = 0x00;
        if (dataReceived) {
            result |= 0b10000000;
        }
        if (includesTestPackets()) {
            result |= 0b01000000;
        }
        if (includesSIPs()) {
            result |= 0b00100000;
        }
        if (includesTextPackets()) {
            result |= 0b00010000;
        }
        if (inputDisabled) {
            result |= 0b00001000;
        }
        if (errorsDetected) {
            result |= 0b00000100;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InputStatus that = (InputStatus) o;
        return dataReceived == that.dataReceived &&
                inputDisabled == that.inputDisabled &&
                errorsDetected == that.errorsDetected;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataReceived, inputDisabled, errorsDetected);
    }

    public boolean isDataReceived() {
        return dataReceived;
    }

    public boolean isInputDisabled() {
        return inputDisabled;
    }

    public boolean isErrorsDetected() {
        return errorsDetected;
    }
}
