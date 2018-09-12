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

import de.deltaeight.libartnet.builders.ArtDmxBuilder;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Provides an abstraction layer which makes use of {@link ArtNetReceiver} and {@link ArtNetSender} to provide Art-Net
 * controller functionality (Lighting desk...).
 *
 * @author Julian Rabe
 */
public class ArtNetController extends NetworkHandler {

    private final ArtNetReceiver receiver;
    private final ArtNetSender sender;
    private final HashMap<Integer, ArtDmxBuilder> universes;

    /**
     * Initializes an instance for use but creates a default {@link DatagramSocket} bound to Port {@code 0x1936}.
     *
     * @throws SocketException When it was not possible to bind to Port {@code 0x1936}, the socket is not able to switch
     *                         to broadcast or is not able to bind multiple addresses.
     */
    public ArtNetController() throws SocketException {
        this(new DatagramSocket(0x1936));
    }

    /**
     * Initializes an instance for use.
     *
     * @param socket The {@link DatagramSocket} to use.
     */
    public ArtNetController(DatagramSocket socket) {
        super(socket);
        this.receiver = new ArtNetReceiver(socket);
        this.sender = new ArtNetSender(socket);
        universes = new HashMap<>(32768);
    }

    @Override
    void run() throws Exception {
        // TODO Manage nodes
        // TODO Send ArtDmx
        // TODO Send ArtPoll continuously
        // TODO Send ArtPollReply
        // TODO Add listener support (maybe)
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        super.start();
        receiver.start();
        sender.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
        receiver.start();
        sender.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        super.setExceptionHandler(exceptionHandler);
        receiver.setExceptionHandler(exceptionHandler);
        sender.setExceptionHandler(exceptionHandler);
    }

    /**
     * Sets the DMX data for the desired universe identified by net number, subnet number and universe.
     *
     * @param net      The net number ({@code 0 <= net <= 127}).
     * @param subnet   The subnet number ({@code 0 <= subnet <= 15}).
     * @param universe The universe number ({@code 0 <= universe <= 15}).
     * @param data     The DMX data this universe contains. Set to {@code null} to remove universe from the controller.
     */
    public void setUniverseData(int net, int subnet, int universe, byte[] data) {

        if (0 > net || net > 127) {
            throw new IllegalArgumentException("Illegal net number!");
        }

        if (0 > subnet || subnet > 15) {
            throw new IllegalArgumentException("Illegal subnet number!");
        }

        if (0 > universe || universe > 15) {
            throw new IllegalArgumentException("Illegal universe number!");
        }

        if (data != null && data.length > 512) {
            throw new IllegalArgumentException("Data length > 512 bytes!");
        }

        int uid = (net & 0x7f << 8) | (subnet & 0xf << 4) | (universe & 0xf);

        if (data == null) {
            universes.remove(uid);
        } else {
            ArtDmxBuilder builder = universes.get(uid);

            if (builder == null) {
                builder = new ArtDmxBuilder()
                        .withNetAddress(net)
                        .withSubnetAddress(subnet)
                        .withUniverseAddress(universe);
            }

            builder.withData(data);
        }
    }
}
