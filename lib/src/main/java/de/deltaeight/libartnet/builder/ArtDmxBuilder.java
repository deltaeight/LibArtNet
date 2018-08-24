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

import de.deltaeight.libartnet.enums.ArtNet;
import de.deltaeight.libartnet.enums.OpCode;
import de.deltaeight.libartnet.packet.ArtDmx;

public class ArtDmxBuilder implements ArtNetPacketBuilder<ArtDmx> {

    private static final byte[] OP_CODE_BYTES = OpCode.OpDmx.getBytesLittleEndian();

    private int sequence;
    private int physical;
    private int subnetAddress;
    private int universeAddress;
    private int netAddress;
    private byte[] data;

    private boolean changed;
    private ArtDmx artDmx;

    public ArtDmxBuilder() {
        data = new byte[0];
    }

    @Override
    public ArtDmx build() {

        if (changed) {

            int dataLength = data.length;
            if (dataLength % 2 > 0) {
                dataLength++;
            }

            byte[] tmpData = new byte[dataLength];
            System.arraycopy(data, 0, tmpData, 0, data.length);

            byte[] bytes = new byte[18 + dataLength];

            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);
            System.arraycopy(ArtNet.PROTOCOL_REVISION.getBytes(), 0, bytes, 10, 2);

            bytes[12] = (byte) sequence;
            bytes[13] = (byte) physical;
            bytes[14] = (byte) (subnetAddress << 4 | universeAddress);
            bytes[15] = (byte) netAddress;
            bytes[16] = (byte) (dataLength >> 8);
            bytes[17] = (byte) dataLength;

            System.arraycopy(tmpData, 0, bytes, 18, dataLength);

            artDmx = new ArtDmx(sequence, physical, netAddress, subnetAddress, universeAddress, data.clone(), bytes);

            changed = false;
        }

        return artDmx;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        if (this.sequence != sequence) {
            if (0 > sequence || sequence > 255) {
                throw new IllegalArgumentException("Illegal sequence number!");
            }
            this.sequence = sequence;
            changed = true;
        }
    }

    public ArtDmxBuilder withSequence(int sequence) {
        setSequence(sequence);
        return this;
    }

    public int getPhysical() {
        return physical;
    }

    public void setPhysical(int physical) {
        if (this.physical != physical) {
            if (0 > physical || physical > 255) {
                throw new IllegalArgumentException("Illegal physical port number!");
            }
            this.physical = physical;
            changed = true;
        }
    }

    public ArtDmxBuilder withPhysical(int physical) {
        setPhysical(physical);
        return this;
    }

    public int getSubnetAddress() {
        return subnetAddress;
    }

    public void setSubnetAddress(int subnetAddress) {
        if (this.subnetAddress != subnetAddress) {
            if (0 > subnetAddress || subnetAddress > 15) {
                throw new IllegalArgumentException("Illegal subnet address!");
            }
            this.subnetAddress = subnetAddress;
            changed = true;
        }
    }

    public ArtDmxBuilder withSubnetAddress(int subnetAddress) {
        setSubnetAddress(subnetAddress);
        return this;
    }

    public int getUniverseAddress() {
        return universeAddress;
    }

    public void setUniverseAddress(int universeAddress) {
        if (this.universeAddress != universeAddress) {
            if (0 > universeAddress || universeAddress > 15) {
                throw new IllegalArgumentException("Illegal universe address!");
            }
            this.universeAddress = universeAddress;
            changed = true;
        }
    }

    public ArtDmxBuilder withUniverseAddress(int universeAddress) {
        setUniverseAddress(universeAddress);
        return this;
    }

    public int getNetAddress() {
        return netAddress;
    }

    public void setNetAddress(int netAddress) {
        if (this.netAddress != netAddress) {
            if (0 > netAddress || netAddress > 127) {
                throw new IllegalArgumentException("Illegal net address!");
            }
            this.netAddress = netAddress;
            changed = true;
        }
    }

    public ArtDmxBuilder withNetAddress(int netAddress) {
        setNetAddress(netAddress);
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        if (this.data != data) {

            if (data == null) {
                data = new byte[0];
            }

            if (data.length > 512) {
                throw new IllegalArgumentException("Payload too large!");
            }

            this.data = data.clone();
            changed = true;
        }
    }

    public ArtDmxBuilder withData(byte[] data) {
        setData(data);
        return this;
    }
}
