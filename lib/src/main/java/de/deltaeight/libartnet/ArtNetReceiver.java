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

import de.deltaeight.libartnet.descriptors.ArtNet;
import de.deltaeight.libartnet.packets.ArtDmx;
import de.deltaeight.libartnet.packets.ArtNetPacket;
import de.deltaeight.libartnet.packets.ArtPoll;
import de.deltaeight.libartnet.packets.ArtPollReply;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * Provides a multi-threaded receiver which listens to UDP network traffic and uses {@link PacketReceiveHandler}
 * instances to react to known packets.
 *
 * @author Julian Rabe
 */
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

    /**
     * Initializes an instance for use.
     *
     * @param workingPool The {@link ExecutorService} to use.
     * @param socket      The {@link DatagramSocket} to use.
     */
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

    /**
     * Starts listening to UDP packets. Only possible if not already running or stopped.
     * <p>
     * It is recommended, to check the state of the {@link ArtNetReceiver} using {@link #getState()} before calling.
     *
     * @throws IllegalStateException When {@link #getState()} {@code != } {@link State#Initialized}.
     * @see #stop()
     * @see State
     */
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

    /**
     * Stops listening to UDP packets and closes the underlying socket. Only possible if running.
     * <p>
     * It is recommended, to check the state of the {@link ArtNetReceiver} using {@link #getState()} before calling.
     *
     * @throws IllegalStateException When {@link #getState()} {@code != } {@link State#Running}.
     * @see #start()
     * @see State
     */
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

    /**
     * Sets the {@link ExceptionHandler} which is called when exceptions are thrown while listeninng to the UDP port.
     *
     * @param exceptionHandler The {@link ExceptionHandler} to use.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * @param exceptionHandler The {@link ExceptionHandler} to use.
     * @see #setExceptionHandler(ExceptionHandler)
     */
    public ArtNetReceiver withExceptionHandler(ExceptionHandler exceptionHandler) {
        setExceptionHandler(exceptionHandler);
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
     * Represents the state of the {@link ArtNetReceiver}.
     * <p>
     * During the lifecycle of an {@link ArtNetReceiver}, it passes three states: {@link #Initialized}, {@link #Running}
     * and {@link #Stopped}. Once stopped, an {@link ArtNetReceiver} cannot be started again.
     *
     * @author Julian Rabe
     */
    public enum State {

        /**
         * After an instance of {@link ArtNetReceiver} is initialized, the socket is open and the instance is ready to
         * be used.
         * <p>
         * When in this state, calling {@link ArtNetReceiver#stop()} is illegal.
         */
        Initialized,

        /**
         * When {@link ArtNetReceiver#start()} is called, the {@link ArtNetReceiver} starts it's listener- and
         * worker-threads and starts listening to UDP packets.
         * <p>
         * When in this state, calling {@link ArtNetReceiver#start()} is illegal.
         */
        Running,

        /**
         * When {@link ArtNetReceiver#stop()} is called, the {@link ArtNetReceiver} stops it's listener- and
         * worker-threads and closes the underlying socket.
         * <p>
         * When in this state, calling both {@link ArtNetReceiver#start()} and {@link ArtNetReceiver#stop()} is illegal.
         */
        Stopped
    }
}
