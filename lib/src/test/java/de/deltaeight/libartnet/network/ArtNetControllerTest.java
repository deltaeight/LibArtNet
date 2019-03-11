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

import de.deltaeight.libartnet.builders.ArtDmxBuilder;
import de.deltaeight.libartnet.packets.ArtDmx;
import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.fail;

class ArtNetControllerTest extends AbstractNetworkHandlerTest<ArtNetController> {

    @Override
    ArtNetController getNewInstance(DatagramSocket datagramSocket) {
        try {
            return new ArtNetController(datagramSocket, new DatagramSocketMockup(0x1936));
        } catch (SocketException e) {
            fail(e);
        }
        return null;
    }

    @Override
    void provokeException(ArtNetController networkHandler) {
        // Do nothing
    }

    @Test
    void send() throws SocketException, InterruptedException {

        // Prepare receiving

        AtomicReference<ArtDmx> packetReference = new AtomicReference<>();
        AtomicReference<Instant> receivedAt = new AtomicReference<>();
        AtomicReference<CountDownLatch> latchReference = new AtomicReference<>();

        latchReference.set(new CountDownLatch(1));

        DatagramSocketMockup datagramSocketMockup = new DatagramSocketMockup();
        datagramSocketMockup.setOnPacketSent(packet ->
                datagramSocketMockup.injectPacket(new ArtDmxBuilder().buildFromBytes(packet.getData())));

        ArtNetReceiver receiver = new ArtNetReceiver(datagramSocketMockup)
                .withArtDmxReceiveHandler(packet -> {
                    packetReference.set(packet);
                    receivedAt.set(Instant.now());
                    latchReference.get().countDown();
                });

        receiver.start();

        // Start sending

        ArtNetController controller = getNewInstance(datagramSocketMockup);
        controller.start();

        byte[] universeData = new byte[]{Byte.MAX_VALUE, 0, Byte.MAX_VALUE, 0};

        controller.setUniverseData(0, 0, 0, universeData);

        assertThat(latchReference.get().await(1, TimeUnit.SECONDS), is(true));

        assertThat(packetReference.get(), is(instanceOf(ArtDmx.class)));
        assertThat(packetReference.get().getData(), is(equalToObject(universeData)));

        // Test resend after four seconds

        Instant lastReceived = receivedAt.get();
        latchReference.set(new CountDownLatch(1));

        boolean await = latchReference.get().await(6, TimeUnit.SECONDS);
        assertThat(await, is(true));

        assertThat(packetReference.get(), is(instanceOf(ArtDmx.class)));
        assertThat(packetReference.get().getData(), is(equalToObject(universeData)));
        assertThat(receivedAt.get().minusSeconds(4),
                is(allOf(lessThan(lastReceived.plusSeconds(1)), greaterThan(lastReceived.minusSeconds(1)))));

        // Test resend on change

        lastReceived = Instant.now();
        latchReference.set(new CountDownLatch(1));

        universeData = new byte[]{0, 0, 0, 0};

        controller.setUniverseData(0, 0, 0, universeData);

        assertThat(latchReference.get().await(1, TimeUnit.SECONDS), is(true));

        assertThat(packetReference.get(), is(instanceOf(ArtDmx.class)));
        assertThat(packetReference.get().getData(), is(equalToObject(universeData)));
        assertThat(lastReceived.plusSeconds(1).isAfter(receivedAt.get()), is(true));

        controller.stop();
    }

    // TODO Test setUniverseData() exceptions
}
