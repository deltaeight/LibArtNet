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
import de.deltaeight.libartnet.packets.ArtDmx;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArtNetSenderTest extends AbstractNetworkHandlerTest<ArtNetSender> {

    @Override
    ArtNetSender getNewInstance(DatagramSocket datagramSocket) {
        return new ArtNetSender(datagramSocket);
    }

    @Test
    final void exceptionHandler() throws SocketException, InterruptedException, UnknownHostException {

        AtomicReference<Throwable> handledException = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        ArtNetSender artNetSender = (ArtNetSender) new ArtNetSender(new DatagramSocketMockup(true))
                .withExceptionHandler(exception -> {
                    handledException.set(exception);
                    latch.countDown();
                });

        artNetSender.start();
        artNetSender.send(InetAddress.getByName("127.0.0.1"), new ArtDmxBuilder().build());

        latch.await(3, TimeUnit.SECONDS);
        assertTrue(handledException.get() instanceof IOException);

        artNetSender.stop();
    }

    @Test
    final void send() throws SocketException, UnknownHostException, InterruptedException {

        AtomicReference<DatagramPacket> receivedPacket = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        ArtDmx artDmx = new ArtDmxBuilder().build();

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();
        datagramSocketMockup.setOnPacketSent(packet -> {
            receivedPacket.set(packet);
            latch.countDown();
        });

        ArtNetSender artNetSender = new ArtNetSender(datagramSocketMockup);
        artNetSender.start();
        artNetSender.send(InetAddress.getByName("127.0.0.1"), artDmx);

        latch.await(3, TimeUnit.SECONDS);
        assertArrayEquals(artDmx.getBytes(), receivedPacket.get().getData());

        artNetSender.stop();
    }
}
