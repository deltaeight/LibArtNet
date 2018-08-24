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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtDmxBuilder implements ArtNetPacketBuilder<ArtDmx> {

    private static final byte[] OP_CODE_BYTES = OpCode.OpDmx.getBytesLittleEndian();

    private int sequence;
    private int physical;
    private int subnetAddress;
    private int universeAddress;
    private int netAddress;
    private final ArrayList<Byte> data;

    private boolean changed;
    private ArtDmx artDmx;

    public ArtDmxBuilder() {
        data = new ArrayList<>(512);
    }

    @Override
    public ArtDmx build() {

        if (changed) {

            if (data.size() % 2 > 0) {
                data.add((byte) 0x00);
            }

            byte[] tmpData = new byte[data.size()];
            byte[] bytes = new byte[18 + data.size()];

            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);
            System.arraycopy(ArtNet.PROTOCOL_REVISION.getBytes(), 0, bytes, 10, 2);

            bytes[12] = (byte) sequence;
            bytes[13] = (byte) physical;
            bytes[14] = (byte) (subnetAddress << 4 | universeAddress);
            bytes[15] = (byte) netAddress;
            bytes[16] = (byte) (data.size() >> 8);
            bytes[17] = (byte) data.size();

            for (int i = 0; i < data.size(); i++) {
                Byte currentData = data.get(i);

                tmpData[i] = currentData;
                bytes[18 + i] = currentData;
            }

            artDmx = new ArtDmx(sequence, physical, netAddress, subnetAddress, universeAddress, tmpData, bytes);

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

    public List<Byte> getData() {
        return Collections.unmodifiableList(data);
    }

    public byte getData(int index) {
        return data.get(index);
    }

    public void setData(byte[] data) {
        if (data != null) {
            if (data.length > 512) {
                throw new IllegalArgumentException("Payload too large!");
            }
            for (int i = 0; i < data.length; i++) {
                if (this.data.get(i) != data[i]) {
                    this.data.set(i, data[i]);
                    changed = true;
                }
            }
        }
    }

    public void setData(List<Byte> data) {
        if (data != null) {
            if (data.size() > 512) {
                throw new IllegalArgumentException("Payload too large!");
            }
            for (int i = 0; i < data.size(); i++) {
                byte currentData = data.get(i);
                if (this.data.get(i) != currentData) {
                    this.data.set(i, currentData);
                    changed = true;
                }
            }
        }
    }

    public void setData(int index, byte data) {
        if (0 > index || index > 511) {
            throw new IllegalArgumentException("Illegal data index!");
        }
        if (this.data.get(index) != data) {
            this.data.set(index, data);
            changed = true;
        }
    }

    public ArtDmxBuilder withData(byte[] data) {
        setData(data);
        return this;
    }

    public ArtDmxBuilder withData(List<Byte> data) {
        setData(data);
        return this;
    }

    public ArtDmxBuilder withData(int index, byte data) {
        setData(index, data);
        return this;
    }
}
