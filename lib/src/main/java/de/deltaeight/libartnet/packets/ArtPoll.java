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

package de.deltaeight.libartnet.packets;

import de.deltaeight.libartnet.descriptors.Priority;

import java.util.Objects;

/**
 * Represents an {@code ArtPoll} packet which requests other Art-Net nodes on the network to reply with
 * {@link ArtPollReply}.
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtPollReply
 * @see de.deltaeight.libartnet.ArtPollBuilder
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtPoll extends ArtNetPacket {

    private final boolean enableVlcTransmission;
    private final boolean unicastDiagnosticMessages;
    private final boolean sendDiagnosticMessages;
    private final boolean sendArtPollReplyOnChanges;
    private final Priority priority;

    public ArtPoll(boolean enableVlcTransmission,
                   boolean unicastDiagnosticMessages,
                   boolean sendDiagnosticMessages,
                   boolean sendArtPollReplyOnChanges,
                   Priority priority,
                   byte[] bytes) {

        super(bytes);

        this.enableVlcTransmission = enableVlcTransmission;
        this.unicastDiagnosticMessages = unicastDiagnosticMessages;
        this.sendDiagnosticMessages = sendDiagnosticMessages;
        this.sendArtPollReplyOnChanges = sendArtPollReplyOnChanges;
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enableVlcTransmission, unicastDiagnosticMessages, sendDiagnosticMessages,
                sendArtPollReplyOnChanges, priority);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtPoll artPoll = (ArtPoll) o;
        return enableVlcTransmission == artPoll.enableVlcTransmission &&
                unicastDiagnosticMessages == artPoll.unicastDiagnosticMessages &&
                sendDiagnosticMessages == artPoll.sendDiagnosticMessages &&
                sendArtPollReplyOnChanges == artPoll.sendArtPollReplyOnChanges &&
                priority == artPoll.priority;
    }

    public boolean isEnableVlcTransmission() {
        return enableVlcTransmission;
    }

    public boolean isUnicastDiagnosticMessages() {
        return unicastDiagnosticMessages;
    }

    public boolean isSendDiagnosticMessages() {
        return sendDiagnosticMessages;
    }

    public boolean isSendArtPollReplyOnChanges() {
        return sendArtPollReplyOnChanges;
    }

    public Priority getPriority() {
        return priority;
    }
}