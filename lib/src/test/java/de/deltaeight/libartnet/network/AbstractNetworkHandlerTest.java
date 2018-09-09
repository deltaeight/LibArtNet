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

import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.SocketException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractNetworkHandlerTest<T extends NetworkHandler> {

    abstract T getNewInstance(DatagramSocket datagramSocket);

    @Test
    final void constructor() {
        assertThrows(IllegalArgumentException.class, () -> getNewInstance(new DatagramSocketMockup(666)));
    }

    @Test
    final void states() throws SocketException {

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();
        T networkHandler = getNewInstance(datagramSocketMockup);

        assertSame(NetworkHandler.State.Initialized, networkHandler.getState());
        assertThrows(IllegalStateException.class, networkHandler::stop);

        networkHandler.start();

        assertSame(NetworkHandler.State.Running, networkHandler.getState());
        assertThrows(IllegalStateException.class, networkHandler::start);

        networkHandler.stop();

        assertSame(NetworkHandler.State.Stopped, networkHandler.getState());
        assertThrows(IllegalStateException.class, networkHandler::start);
        assertThrows(IllegalStateException.class, networkHandler::stop);
    }
}
