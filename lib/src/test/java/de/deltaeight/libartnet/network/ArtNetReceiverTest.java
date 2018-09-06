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
import de.deltaeight.libartnet.builders.ArtNetPacketBuilder;
import de.deltaeight.libartnet.builders.ArtPollBuilder;
import de.deltaeight.libartnet.builders.ArtPollReplyBuilder;
import de.deltaeight.libartnet.packets.ArtNetPacket;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class ArtNetReceiverTest {

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new ArtNetReceiver(new DatagramSocketMockup(666)));
    }

    @Test
    void states() throws SocketException {

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();
        ArtNetReceiver artNetReceiver = new ArtNetReceiver(datagramSocketMockup);

        assertSame(NetworkHandler.State.Initialized, artNetReceiver.getState());
        assertThrows(IllegalStateException.class, artNetReceiver::stop);

        artNetReceiver.start();

        assertSame(NetworkHandler.State.Running, artNetReceiver.getState());
        assertThrows(IllegalStateException.class, artNetReceiver::start);

        artNetReceiver.stop();

        assertSame(NetworkHandler.State.Stopped, artNetReceiver.getState());
        assertThrows(IllegalStateException.class, artNetReceiver::start);
        assertThrows(IllegalStateException.class, artNetReceiver::stop);
    }

    @Test
    void exceptionHandler() throws SocketException, InterruptedException {

        AtomicReference<Throwable> handledException = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        new ArtNetReceiver(new DatagramSocketMockup(true))
                .withExceptionHandler(exception -> {
                    handledException.set(exception);
                    latch.countDown();
                })
                .start();

        latch.await(3, TimeUnit.SECONDS);
        assertTrue(handledException.get() instanceof IOException);
    }

    private <T extends ArtNetPacket> void testReceiveHandler(ArtNetReceiverPreparation<T> artNetReceiverPreparation,
                                                             ArtNetPacketBuilder<T> packetBuilder)

            throws SocketException, InterruptedException {

        AtomicReference<T> packetReference = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();

        artNetReceiverPreparation.prepare(new ArtNetReceiver(datagramSocketMockup), packet -> {
            packetReference.set(packet);
            latch.countDown();
        }).start();

        T packet = packetBuilder.build();

        datagramSocketMockup.injectPacket(packet);

        latch.await(3, TimeUnit.SECONDS);
        assertEquals(packet, packetReference.get());
    }

    @Test
    void receiveHandlers() throws SocketException, InterruptedException {
        testReceiveHandler(ArtNetReceiver::withArtPollReceiveHandler, new ArtPollBuilder());
        testReceiveHandler(ArtNetReceiver::withArtPollReplyReceiveHandler, new ArtPollReplyBuilder());
        testReceiveHandler(ArtNetReceiver::withArtDmxReceiveHandler, new ArtDmxBuilder());
    }

    @FunctionalInterface
    private interface ArtNetReceiverPreparation<T extends ArtNetPacket> {

        ArtNetReceiver prepare(ArtNetReceiver artNetReceiver, PacketReceiveHandler<T> packetReceiveHandler);
    }
}
