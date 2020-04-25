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

import java.net.DatagramSocket;

/**
 * Provides a basic template for network handlers with a worker thread and states.
 *
 * @author Julian Rabe
 */
abstract class NetworkHandler {

    final DatagramSocket socket;
    private final Thread workerThread;
    private State state;
    private ExceptionHandler exceptionHandler;

    /**
     * @param socket The {@link DatagramSocket} to use.
     * @throws IllegalArgumentException if {@code socket} is on a different port than {@code 0x1936}.
     */
    NetworkHandler(DatagramSocket socket) {

        if (socket.getLocalPort() != 0x1936) {
            throw new IllegalArgumentException("Illegal socket port " + socket.getLocalPort() + "!");
        }

        this.socket = socket;

        workerThread = new Thread() {

            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        NetworkHandler.this.run();
                    } catch (Exception e) {
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

        workerThread.setName("ArtNet Network Worker");
        workerThread.setDaemon(true);

        state = State.Initialized;
    }

    /**
     * Is run in {@link #workerThread}.
     *
     * @throws Exception Can be anything and is forwarded to {@link #exceptionHandler} if set.
     */
    abstract void run() throws Exception;

    /**
     * Starts working. Only possible if not already running or stopped.
     * <p>
     * It is recommended, to check the state using {@link #getState()} before calling.
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
            throw new IllegalStateException("Already running!");
        } else {
            throw new IllegalStateException("Not initialized!");
        }
    }

    /**
     * Stops working and closes the underlying socket. Only possible if running.
     * <p>
     * It is recommended, to check the state using {@link #getState()} before calling.
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
            throw new IllegalStateException("Not Running!");
        }
    }

    public State getState() {
        return state;
    }

    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * Sets the {@link ExceptionHandler} which is called when exceptions are thrown while listening to the UDP port.
     *
     * @param exceptionHandler The {@link ExceptionHandler} to use.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * @param exceptionHandler The {@link ExceptionHandler} to use.
     * @return Current {@link NetworkHandler} instance for fluent code style.
     * @see #setExceptionHandler(ExceptionHandler)
     */
    public NetworkHandler withExceptionHandler(ExceptionHandler exceptionHandler) {
        setExceptionHandler(exceptionHandler);
        return this;
    }

    /**
     * Represents the state of the {@link NetworkHandler}.
     * <p>
     * During the lifecycle of a {@link NetworkHandler}, it passes three states: {@link #Initialized}, {@link #Running}
     * and {@link #Stopped}. Once stopped, a {@link NetworkHandler} cannot be started again.
     *
     * @author Julian Rabe
     */
    public enum State {

        /**
         * After a {@link NetworkHandler} is initialized, the socket is open and the handler is ready to be used.
         * <p>
         * When in this state, calling {@link NetworkHandler#stop()} is illegal.
         */
        Initialized,

        /**
         * When {@link NetworkHandler#start()} is called, the {@link NetworkHandler} starts its worker-threads.
         * <p>
         * When in this state, calling {@link NetworkHandler#start()} is illegal.
         */
        Running,

        /**
         * When {@link NetworkHandler#stop()} is called, the {@link NetworkHandler} stops its worker-threads and closes
         * the underlying socket.
         * <p>
         * When in this state, calling both {@link NetworkHandler#start()} and {@link NetworkHandler#stop()} is illegal.
         */
        Stopped
    }
}
