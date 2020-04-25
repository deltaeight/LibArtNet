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

import org.junit.jupiter.api.Test;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.deltaeight.libartnet.builders.ArtDmxBuilder;
import de.deltaeight.libartnet.packets.ArtDmx;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class NetworkPacketIntegrityTest {

    @Test
    void networkPacketIntegrityTest()
            throws SocketException, UnknownHostException, InterruptedException {

        // Adjust number of test packets. 512 * 256 is maximum for discrete packet numbering.
        // Too many packets may increase packet loss.
        int numberOfTestPackets = 10000;

        ArrayList<ArtDmxBuilder> builders = new ArrayList<>(128 * 16 * 16);
        for (int i = 0; i < 128; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    builders.add(new ArtDmxBuilder()
                            .withNetAddress(i)
                            .withSubnetAddress(j)
                            .withUniverseAddress(k));
                }
            }
        }

        byte[][] dataSources = new byte[numberOfTestPackets][];
        for (int i = 0; i < numberOfTestPackets; i++) {
            dataSources[i] = getDataArray(i);
        }

        ConcurrentLinkedQueue<ArtDmx> receivedPackets = new ConcurrentLinkedQueue<>();
        ArtNetReceiver receiver = new ArtNetReceiver()
                .withArtDmxReceiveHandler(receivedPackets::add);
        receiver.start();

        Thread.sleep(2000);

        HashMap<String, ArtDmx> sentPackets = new HashMap<>();
        ArtNetSender sender = new ArtNetSender(new DatagramSocket());
        sender.start();
        InetAddress ip = InetAddress.getByName("127.0.0.1");
        int builderCounter = 0;
        for (int i = 0; i < numberOfTestPackets; i++) {
            byte[] data = dataSources[i];
            ArtDmx packet = builders.get(builderCounter)
                    .withData(data)
                    .build();
            sentPackets.put(getHash(packet), packet);
            sender.send(ip, packet);

            if (++builderCounter == builders.size()) {
                builderCounter = 0;
            }
        }

        int receivedPacketCounter = 0;
        long end = System.currentTimeMillis() + 2000;
        while (System.currentTimeMillis() < end) {
            ArtDmx receivedPacket = receivedPackets.poll();
            if (receivedPacket != null) {
                ArtDmx sentPacket = sentPackets.get(getHash(receivedPacket));
                assertThat(receivedPacket, is(equalTo(sentPacket)));
                assertThat(receivedPacket.getData(), is(equalTo(sentPacket.getData())));
                assertThat(receivedPacket.getBytes(), is(equalTo(sentPacket.getBytes())));
                receivedPacketCounter++;
            }
        }

        System.out.println("Sent packets:     " + sentPackets.size());
        System.out.println("Received packets: " + receivedPacketCounter);
        System.out.println("Loss:             " + (1 - ((receivedPacketCounter * 1d) / sentPackets.size())));

        sender.stop();
        receiver.stop();
    }

    private String getHash(byte[] data, int netAddress, int subnetAddress, int universeAddress) {
        int sum = 0;
        for (byte datum : data) {
            sum += datum & 0xFF;
        }
        return sum + " " + netAddress + " " + subnetAddress + " " + universeAddress;
    }

    private String getHash(ArtDmx packet) {
        return getHash(packet.getData(), packet.getNetAddress(), packet.getSubnetAddress(),
                packet.getUniverseAddress());
    }

    private byte[] getDataArray(int index) {
        byte[] result = new byte[512];
        for (int i = 0; i < 512; i++) {
            result[i] = (byte) (index >>> 8 * i);
            if (result[i] == 0) {
                break;
            }
        }
        return result;
    }
}
