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
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

import de.deltaeight.libartnet.builders.ArtDmxBuilder;
import de.deltaeight.libartnet.builders.ArtPollBuilder;
import de.deltaeight.libartnet.builders.ArtPollReplyBuilder;
import de.deltaeight.libartnet.builders.ArtTimeCodeBuilder;
import de.deltaeight.libartnet.descriptors.ArtNet;
import de.deltaeight.libartnet.packets.ArtDmx;
import de.deltaeight.libartnet.packets.ArtNetPacket;
import de.deltaeight.libartnet.packets.ArtPoll;
import de.deltaeight.libartnet.packets.ArtPollReply;
import de.deltaeight.libartnet.packets.ArtTimeCode;

/**
 * Provides a multi-threaded receiver which listens to UDP network traffic and uses {@link PacketReceiveHandler}
 * instances to react to known packets.
 *
 * @author Julian Rabe
 * @see ArtNetSender
 */
public class ArtNetReceiver extends NetworkHandler {

    private final ExecutorService workingPool;
    private final byte[] buffer;
    private final ConcurrentHashMap<Class<? extends ArtNetPacket>, PacketReceiveDispatcher<? extends ArtNetPacket>> packetReceiveDispatcher;
    private final HashSet<PacketReceiveHandler<ArtDmx>> artDmxReceiveHandlers;
    private final HashSet<PacketReceiveHandler<ArtPoll>> artPollReceiveHandlers;
    private final HashSet<PacketReceiveHandler<ArtPollReply>> artPollReplyReceiveHandlers;
    private final HashSet<PacketReceiveHandler<ArtTimeCode>> artTimeCodeReceiveHandlers;

    /**
     * Initializes an instance for use.
     *
     * @param workingPool The {@link ExecutorService} to use.
     * @param socket      The {@link DatagramSocket} to use.
     */
    public ArtNetReceiver(ExecutorService workingPool, DatagramSocket socket) {
        super(socket);

        if (socket.getLocalPort() != 0x1936) {
            throw new IllegalArgumentException("Illegal socket port " + socket.getLocalPort() + "!");
        }

        this.workingPool = workingPool;

        buffer = new byte[530];
        packetReceiveDispatcher = new ConcurrentHashMap<>();

        artDmxReceiveHandlers = new HashSet<>();
        artPollReceiveHandlers = new HashSet<>();
        artPollReplyReceiveHandlers = new HashSet<>();
        artTimeCodeReceiveHandlers = new HashSet<>();
    }

    /**
     * Initializes an instance for use but creates a default {@link DatagramSocket} bound to Port {@code 0x1936}.
     *
     * @param workingPool The {@link ExecutorService} to use.
     * @throws SocketException When it was not possible to bind to Port {@code 0x1936}, the socket is not able to switch
     *                         to broadcast or is not able to bind multiple addresses.
     */
    public ArtNetReceiver(ExecutorService workingPool) throws SocketException {
        this(workingPool, new DatagramSocket(0x1936));
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
    }

    /**
     * Initializes an instance for use but uses the common {@link ForkJoinPool} shared across the JVM.
     *
     * @param socket The {@link DatagramSocket} to use.
     * @see ForkJoinPool#commonPool()
     */
    public ArtNetReceiver(DatagramSocket socket) {
        this(ForkJoinPool.commonPool(), socket);
    }

    /**
     * Initializes an instance for use but creates a default {@link DatagramSocket} bound to Port {@code 0x1936} and
     * uses the common {@link ForkJoinPool} shared across the JVM.
     *
     * @throws SocketException When it was not possible to bind to Port {@code 0x1936}, the socket is not able to switch
     *                         to broadcast or is not able to bind multiple addresses.
     * @see ForkJoinPool#commonPool()
     */
    public ArtNetReceiver() throws SocketException {
        this(ForkJoinPool.commonPool(), new DatagramSocket(0x1936));
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
    }

    @Override
    void run() throws Exception {
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(datagramPacket);

        if (datagramPacket.getLength() > 10
                && Arrays.equals(ArtNet.HEADER.getBytes(), Arrays.copyOfRange(datagramPacket.getData(), 0, 8))) {

            for (PacketReceiveDispatcher<? extends ArtNetPacket> dispatcher : packetReceiveDispatcher.values()) {

                if (dispatcher.handleReceive(datagramPacket.getData().clone())) {
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
        if (getState() == State.Running) {
            workingPool.shutdown();
        }
    }

    /**
     * Adds a {@link PacketReceiveHandler} which is called when {@link ArtDmx} packets are received.
     *
     * @param handler The {@link PacketReceiveHandler} to use.
     */
    public void addArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        if (!packetReceiveDispatcher.containsKey(ArtDmx.class)) {
            packetReceiveDispatcher.put(ArtDmx.class, new PacketReceiveDispatcher<>(workingPool,
                    new ArtDmxBuilder(), artDmxReceiveHandlers));
        }
        artDmxReceiveHandlers.add(handler);

    }

    public void removeArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        artDmxReceiveHandlers.remove(handler);
        if (artDmxReceiveHandlers.isEmpty()) {
            packetReceiveDispatcher.remove(ArtDmx.class);
        }
    }

    /**
     * @param handler The {@link PacketReceiveHandler} to use.
     * @return Current {@link ArtNetReceiver} instance for fluent code style.
     * @see #addArtDmxReceiveHandler(PacketReceiveHandler)
     */
    public ArtNetReceiver withArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        addArtDmxReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        removeArtDmxReceiveHandler(handler);
        return this;
    }

    /**
     * Adds a {@link PacketReceiveHandler} which is called when {@link ArtPoll} packets are received.
     *
     * @param handler The {@link PacketReceiveHandler} to use.
     */
    public void addArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        if (!packetReceiveDispatcher.containsKey(ArtPoll.class)) {
            packetReceiveDispatcher.put(ArtPoll.class, new PacketReceiveDispatcher<>(workingPool,
                    new ArtPollBuilder(), artPollReceiveHandlers));
        }
        artPollReceiveHandlers.add(handler);

    }

    public void removeArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        artPollReceiveHandlers.remove(handler);
        if (artPollReceiveHandlers.isEmpty()) {
            packetReceiveDispatcher.remove(ArtPoll.class);
        }
    }

    /**
     * @param handler The {@link PacketReceiveHandler} to use.
     * @return Current {@link ArtNetReceiver} instance for fluent code style.
     * @see #addArtPollReceiveHandler(PacketReceiveHandler)
     */
    public ArtNetReceiver withArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        addArtPollReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        removeArtPollReceiveHandler(handler);
        return this;
    }

    /**
     * Adds a {@link PacketReceiveHandler} which is called when {@link ArtPollReply} packets are received.
     *
     * @param handler The {@link PacketReceiveHandler} to use.
     */
    public void addArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        if (!packetReceiveDispatcher.containsKey(ArtPollReply.class)) {
            packetReceiveDispatcher.put(ArtPollReply.class, new PacketReceiveDispatcher<>(workingPool,
                    new ArtPollReplyBuilder(), artPollReplyReceiveHandlers));
        }
        artPollReplyReceiveHandlers.add(handler);

    }

    public void removeArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        artPollReplyReceiveHandlers.remove(handler);
        if (artPollReplyReceiveHandlers.isEmpty()) {
            packetReceiveDispatcher.remove(ArtPollReply.class);
        }
    }

    /**
     * @param handler The {@link PacketReceiveHandler} to use.
     * @return Current {@link ArtNetReceiver} instance for fluent code style.
     * @see #addArtPollReplyReceiveHandler(PacketReceiveHandler)
     */
    public ArtNetReceiver withArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        addArtPollReplyReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        removeArtPollReplyReceiveHandler(handler);
        return this;
    }

    /**
     * Adds a {@link PacketReceiveHandler} which is called when {@link ArtTimeCode} packets are received.
     *
     * @param handler The {@link PacketReceiveHandler} to use.
     */
    public void addArtTimeCodeReceiveHandler(PacketReceiveHandler<ArtTimeCode> handler) {
        if (!packetReceiveDispatcher.containsKey(ArtTimeCode.class)) {
            packetReceiveDispatcher.put(ArtTimeCode.class, new PacketReceiveDispatcher<>(workingPool,
                    new ArtTimeCodeBuilder(), artTimeCodeReceiveHandlers));
        }
        artTimeCodeReceiveHandlers.add(handler);

    }

    public void removeArtTimeCodeReceiveHandler(PacketReceiveHandler<ArtTimeCode> handler) {
        artTimeCodeReceiveHandlers.remove(handler);
        if (artTimeCodeReceiveHandlers.isEmpty()) {
            packetReceiveDispatcher.remove(ArtTimeCode.class);
        }
    }

    /**
     * @param handler The {@link PacketReceiveHandler} to use.
     * @return Current {@link ArtNetReceiver} instance for fluent code style.
     * @see #addArtTimeCodeReceiveHandler(PacketReceiveHandler)
     */
    public ArtNetReceiver withArtTimeCodeReceiveHandler(PacketReceiveHandler<ArtTimeCode> handler) {
        addArtTimeCodeReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtTimeCodeReceiveHandler(PacketReceiveHandler<ArtTimeCode> handler) {
        removeArtTimeCodeReceiveHandler(handler);
        return this;
    }
}
