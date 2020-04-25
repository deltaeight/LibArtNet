/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2020 Julian Rabe
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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import de.deltaeight.libartnet.packets.ArtNetPacket;

/**
 * Provides a sender which queues packets to send.
 *
 * @author Julian Rabe
 * @see ArtNetReceiver
 */
public class ArtNetSender extends NetworkHandler {

    private final LinkedBlockingQueue<DatagramPacket> packetQueue;

    /**
     * Initializes an instance for use.
     *
     * @param socket The {@link DatagramSocket} to use.
     */
    public ArtNetSender(DatagramSocket socket) {
        super(socket);
        packetQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Initializes an instance for use but creates a default {@link DatagramSocket} bound to Port {@code 0x1936}.
     *
     * @throws SocketException When socket is not usable.
     */
    public ArtNetSender() throws SocketException {
        this(new DatagramSocket());
    }

    @Override
    void run() throws Exception {
        socket.send(packetQueue.take());
    }

    /**
     * Queues the desired {@link ArtNetPacket} for sending.
     *
     * @param address The {@link InetAddress} to send to.
     * @param packet  The {@link ArtNetPacket} to send.
     * @return {@code true} if queueing was successful, {@code false} if not.
     */
    public boolean send(InetAddress address, ArtNetPacket packet) {
        return packetQueue.offer(new DatagramPacket(packet.getBytes(), packet.getBytes().length, address, 0x1936));
    }
}
