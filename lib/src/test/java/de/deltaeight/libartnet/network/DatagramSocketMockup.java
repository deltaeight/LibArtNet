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

package de.deltaeight.libartnet.network;

import de.deltaeight.libartnet.packets.ArtNetPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

class DatagramSocketMockup extends DatagramSocket {

    private final int localPort;
    private boolean throwIoException;
    private ArtNetPacket injectArtNetPacket;
    private boolean closed;
    private PacketSentHandler onPacketSent;

    DatagramSocketMockup() throws SocketException {
        this.localPort = 0x1936;
    }

    DatagramSocketMockup(int localPort) throws SocketException {
        this.localPort = localPort;
    }

    DatagramSocketMockup(boolean throwIoException) throws SocketException {
        this();
        this.throwIoException = throwIoException;
    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        if (throwIoException) {
            throw new IOException();
        } else if (onPacketSent != null) {
            onPacketSent.handle(p);
        }
    }

    @Override
    public synchronized void receive(DatagramPacket packet) throws IOException {
        boolean wait = true;
        while (!closed && wait) {
            try {
                if (throwIoException) {
                    throwIoException = false;
                    throw new IOException();
                } else if (injectArtNetPacket != null) {
                    packet.setData(injectArtNetPacket.getBytes());
                    packet.setPort(0x1936);
                    packet.setLength(injectArtNetPacket.getBytes().length);
                    injectArtNetPacket = null;
                    wait = false;
                }
                if (wait) {
                    Thread.sleep(10);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public int getLocalPort() {
        return localPort;
    }

    @Override
    public void close() {
        closed = true;
    }

    void injectPacket(ArtNetPacket packet) {
        injectArtNetPacket = packet;
    }

    void setOnPacketSent(PacketSentHandler onPacketSent) {
        this.onPacketSent = onPacketSent;
    }

    @FunctionalInterface
    interface PacketSentHandler {

        void handle(DatagramPacket packet);
    }
}
