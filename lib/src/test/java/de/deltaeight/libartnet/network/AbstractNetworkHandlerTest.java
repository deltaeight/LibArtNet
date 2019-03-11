/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2019 Julian Rabe
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

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

abstract class AbstractNetworkHandlerTest<T extends NetworkHandler> {

    abstract T getNewInstance(DatagramSocket datagramSocket);

    abstract void provokeException(T networkHandler) throws Exception;

    @Test
    final void constructor() {
        assertThrows(IllegalArgumentException.class, () -> getNewInstance(new DatagramSocketMockup(666)));
    }

    @Test
    final void states() throws SocketException {

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();
        T networkHandler = getNewInstance(datagramSocketMockup);

        assertThat(networkHandler.getState(), is(sameInstance(NetworkHandler.State.Initialized)));
        assertThrows(IllegalStateException.class, networkHandler::stop);

        assertDoesNotThrow(networkHandler::start);

        assertThat(networkHandler.getState(), is(sameInstance(NetworkHandler.State.Running)));
        assertThrows(IllegalStateException.class, networkHandler::start);

        assertDoesNotThrow(networkHandler::stop);

        assertThat(networkHandler.getState(), is(sameInstance(NetworkHandler.State.Stopped)));
        assertThrows(IllegalStateException.class, networkHandler::start);
        assertThrows(IllegalStateException.class, networkHandler::stop);
    }

    @Test
    final void exceptionHandler() throws Exception {

        AtomicReference<Throwable> handledException = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        T networkHandler = getNewInstance(new DatagramSocketMockup(true));
        networkHandler.setExceptionHandler(exception -> {
            handledException.set(exception);
            latch.countDown();
        });

        networkHandler.start();

        provokeException(networkHandler);

        assertThat(latch.await(3, TimeUnit.SECONDS), is(true));
        assertThat(handledException.get(), is(instanceOf(IOException.class)));

        networkHandler.stop();
    }
}
