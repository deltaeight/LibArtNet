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

import de.deltaeight.libartnet.packet.ArtNetPacket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ArtNetPacketBuilder<T extends ArtNetPacket> {

    private HashSet<PacketReceiveHandler<T>> receiveHandlers;

    abstract T build();

    abstract T buildFromBytes(byte[] packetData);

    boolean handleReceive(byte[] packetData) {
        T packet = buildFromBytes(packetData);
        if (packet != null) {
            if (receiveHandlers != null) {
                receiveHandlers.forEach(handler -> handler.handle(packet));
            }
            return true;
        }
        return false;
    }

    public Set<PacketReceiveHandler<T>> getReceiveHandlers() {
        return Collections.unmodifiableSet(receiveHandlers);
    }

    void setReceiveHandlers(HashSet<PacketReceiveHandler<T>> receiveHandlers) {
        this.receiveHandlers = receiveHandlers;
    }

    ArtNetPacketBuilder withReceiveHandlers(HashSet<PacketReceiveHandler<T>> receiveHandlers) {
        setReceiveHandlers(receiveHandlers);
        return this;
    }
}
