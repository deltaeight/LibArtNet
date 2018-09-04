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
 * Represents the status of an output port.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.packets.ArtPollReply
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class OutputStatus extends PortStatus {

    public static final OutputStatus DEFAULT = new OutputStatus(false, false, false, false, false, false, false, false);

    private final boolean dataTransmitted;
    private final boolean merging;
    private final boolean outputShort;
    private final boolean mergeModeLTP;
    private final boolean transmittingSACN;

    public OutputStatus(boolean dataTransmitted,
                        boolean includesTestPackets,
                        boolean includesSIPs,
                        boolean includesTextPackets,
                        boolean merging,
                        boolean outputShort,
                        boolean mergeModeLTP,
                        boolean transmittingSACN) {

        super(includesTestPackets, includesSIPs, includesTextPackets);

        this.dataTransmitted = dataTransmitted;
        this.merging = merging;
        this.outputShort = outputShort;
        this.mergeModeLTP = mergeModeLTP;
        this.transmittingSACN = transmittingSACN;
    }

    public static OutputStatus buildFromByte(byte b) {
        return new OutputStatus((b & 0b10000000) > 0, (b & 0b01000000) > 0, (b & 0b00100000) > 0,
                (b & 0b000100000) > 0, (b & 0b00001000) > 0, (b & 0b00000100) > 0,
                (b & 0b00000010) > 0, (b & 0b00000001) > 0);
    }

    public byte getByte() {
        byte result = 0x00;
        if (dataTransmitted) {
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
        if (merging) {
            result |= 0b00001000;
        }
        if (outputShort) {
            result |= 0b00000100;
        }
        if (mergeModeLTP) {
            result |= 0b00000010;
        }
        if (transmittingSACN) {
            result |= 0b00000001;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataTransmitted, merging, outputShort, mergeModeLTP, transmittingSACN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OutputStatus that = (OutputStatus) o;
        return dataTransmitted == that.dataTransmitted &&
                merging == that.merging &&
                outputShort == that.outputShort &&
                mergeModeLTP == that.mergeModeLTP &&
                transmittingSACN == that.transmittingSACN;
    }

    public boolean isDataTransmitted() {
        return dataTransmitted;
    }

    public boolean isMerging() {
        return merging;
    }

    public boolean isOutputShort() {
        return outputShort;
    }

    public boolean isMergeModeLTP() {
        return mergeModeLTP;
    }

    public boolean isTransmittingSACN() {
        return transmittingSACN;
    }
}
