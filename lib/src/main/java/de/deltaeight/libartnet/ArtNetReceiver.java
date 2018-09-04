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

import de.deltaeight.libartnet.enums.ArtNet;
import de.deltaeight.libartnet.packet.ArtDmx;
import de.deltaeight.libartnet.packet.ArtNetPacket;
import de.deltaeight.libartnet.packet.ArtPoll;
import de.deltaeight.libartnet.packet.ArtPollReply;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class ArtNetReceiver {

    private final ExecutorService workingPool;
    private final DatagramSocket socket;
    private final byte[] buffer;
    private final ConcurrentHashMap<Class<? extends ArtNetPacket>, PacketReceiveDispatcher<? extends ArtNetPacket>> packetReceiveDispatcher;
    private final HashSet<PacketReceiveHandler<ArtPoll>> artPollReceiveHandlers;
    private final HashSet<PacketReceiveHandler<ArtPollReply>> artPollReplyReceiveHandlers;
    private final HashSet<PacketReceiveHandler<ArtDmx>> artDmxReceiveHandlers;
    private State state;
    private Thread workerThread;
    private ExceptionHandler exceptionHandler;

    public ArtNetReceiver(ExecutorService workingPool, DatagramSocket socket) {

        if (socket.getLocalPort() != 0x1936) {
            throw new IllegalArgumentException("Illegal socket port " + socket.getLocalPort() + "!");
        }

        this.workingPool = workingPool;
        this.socket = socket;

        buffer = new byte[530];
        packetReceiveDispatcher = new ConcurrentHashMap<>();
        artPollReceiveHandlers = new HashSet<>();
        artPollReplyReceiveHandlers = new HashSet<>();
        artDmxReceiveHandlers = new HashSet<>();

        workerThread = new Thread() {

            @Override
            public void run() {

                while (!isInterrupted()) {

                    try {
                        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                        socket.receive(datagramPacket);

                        workingPool.execute(() -> {

                            if (datagramPacket.getPort() == 0x1936
                                    && datagramPacket.getLength() > 10
                                    && Arrays.equals(ArtNet.HEADER.getBytes(), Arrays.copyOfRange(datagramPacket.getData(), 0, 8))) {

                                for (PacketReceiveDispatcher<? extends ArtNetPacket> dispatcher : packetReceiveDispatcher.values()) {

                                    if (dispatcher.handleReceive(datagramPacket.getData())) {
                                        break;
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        if (!isInterrupted()) {
                            if (exceptionHandler != null) {
                                exceptionHandler.handleException(e);
                            } else {
                                interrupt();
                            }
                        }
                    }
                }
            }
        };

        workerThread.setName("ArtNetReceiver");
        workerThread.setDaemon(true);

        state = State.Initialized;
    }

    public ArtNetReceiver(ExecutorService workingPool) throws SocketException {
        this(workingPool, new DatagramSocket(0x1936));
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
    }

    public ArtNetReceiver(DatagramSocket socket) {
        this(ForkJoinPool.commonPool(), socket);
    }

    public ArtNetReceiver() throws SocketException {
        this(ForkJoinPool.commonPool(), new DatagramSocket(0x1936));
        socket.setBroadcast(true);
        socket.setReuseAddress(true);
    }

    public void start() {
        if (state == State.Initialized) {
            workerThread.start();
            state = State.Running;
        } else if (state == State.Running) {
            throw new IllegalStateException("ArtNetReceiver already running!");
        } else {
            throw new IllegalStateException("ArtNetReceiver not initialized!");
        }
    }

    public void stop() {
        if (state == State.Running) {
            workerThread.interrupt();
            socket.close();
            state = State.Stopped;
        } else {
            throw new IllegalStateException("ArtNetReceiver not Running!");
        }
    }

    public State getState() {
        return state;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public ArtNetReceiver withExceptionHandler(ExceptionHandler exceptionHandler) {
        setExceptionHandler(exceptionHandler);
        return this;
    }

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

    public ArtNetReceiver withArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        addArtPollReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtPollReceiveHandler(PacketReceiveHandler<ArtPoll> handler) {
        removeArtPollReceiveHandler(handler);
        return this;
    }

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

    public ArtNetReceiver withArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        addArtPollReplyReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtPollReplyReceiveHandler(PacketReceiveHandler<ArtPollReply> handler) {
        removeArtPollReplyReceiveHandler(handler);
        return this;
    }

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

    public ArtNetReceiver withArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        addArtDmxReceiveHandler(handler);
        return this;
    }

    public ArtNetReceiver withoutArtDmxReceiveHandler(PacketReceiveHandler<ArtDmx> handler) {
        removeArtDmxReceiveHandler(handler);
        return this;
    }

    public enum State {
        Initialized, Running, Stopped
    }
}
