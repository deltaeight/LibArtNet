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

import de.deltaeight.libartnet.enums.ArtNet;
import de.deltaeight.libartnet.enums.OpCode;
import de.deltaeight.libartnet.enums.Priority;
import de.deltaeight.libartnet.packet.ArtPoll;

/**
 * Builds instances of {@link ArtPoll}.
 * <p>
 * Default diagnose data priority is {@link Priority#Low}.
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtPoll
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtPollBuilder extends ArtNetPacketBuilder<ArtPoll> {

    private static final byte[] OP_CODE_BYTES = OpCode.OpPoll.getBytesLittleEndian();

    private boolean disableVlcTransmission;
    private boolean unicastDiagnosticMessages;
    private boolean sendDiagnosticMessages;
    private boolean sendArtPollReplyOnChanges;
    private Priority priority;

    private boolean changed;
    private ArtPoll artPoll;

    public ArtPollBuilder() {
        priority = Priority.Low;
        changed = true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtPoll} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    public ArtPoll build() {
        if (changed) {

            byte[] bytes = new byte[14];
            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);
            System.arraycopy(ArtNet.PROTOCOL_REVISION.getBytes(), 0, bytes, 10, 2);

            if (disableVlcTransmission) {
                bytes[12] |= 0b00010000;
            }
            if (unicastDiagnosticMessages) {
                bytes[12] |= 0b00001000;
            }
            if (sendDiagnosticMessages) {
                bytes[12] |= 0b00000100;
            }
            if (sendArtPollReplyOnChanges) {
                bytes[12] |= 0b00000010;
            }

            bytes[13] = priority.getValue();

            artPoll = new ArtPoll(vlcTransmissionDisabled(), unicastDiagnosticMessages(), sendDiagnosticMessages(),
                    sendArtPollReplyOnChanges(), getPriority(), bytes.clone());

            changed = false;
        }

        return artPoll;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtPoll} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    ArtPoll buildFromBytes(byte[] packetData) {
        if (packetData[8] == OP_CODE_BYTES[0] && packetData[9] == OP_CODE_BYTES[1]) {

            return new ArtPoll((packetData[12] & 0b00010000) > 0, (packetData[12] & 0b00001000) > 0,
            (packetData[12] & 0b00000100) > 0, (packetData[12] & 0b00000010) > 0, Priority.getPriority(packetData[13]),
                    packetData.clone());
        }
        return null;
    }

    public boolean vlcTransmissionDisabled() {
        return disableVlcTransmission;
    }

    public void setDisableVlcTransmission(boolean disableVlcTransmission) {
        if (this.disableVlcTransmission != disableVlcTransmission) {
            this.disableVlcTransmission = disableVlcTransmission;
            changed = true;
        }
    }

    public ArtPollBuilder withDisableVlcTransmission(boolean disableVlcTransmission) {
        setDisableVlcTransmission(disableVlcTransmission);
        return this;
    }

    public boolean unicastDiagnosticMessages() {
        return unicastDiagnosticMessages;
    }

    public void setUnicastDiagnosticMessages(boolean unicastDiagnosticMessages) {
        if (this.unicastDiagnosticMessages != unicastDiagnosticMessages) {
            this.unicastDiagnosticMessages = unicastDiagnosticMessages;
            changed = true;
        }
    }

    public ArtPollBuilder withUnicastDiagnosticMessages(boolean unicastDiagnosticMessages) {
        setUnicastDiagnosticMessages(unicastDiagnosticMessages);
        return this;
    }

    public boolean sendDiagnosticMessages() {
        return sendDiagnosticMessages;
    }

    public void setSendDiagnosticMessages(boolean sendDiagnosticMessages) {
        if (this.sendDiagnosticMessages != sendDiagnosticMessages) {
            this.sendDiagnosticMessages = sendDiagnosticMessages;
            changed = true;
        }
    }

    public ArtPollBuilder withSendDiagnosticMessages(boolean sendDiagnosticMessages) {
        setSendDiagnosticMessages(sendDiagnosticMessages);
        return this;
    }

    public boolean sendArtPollReplyOnChanges() {
        return sendArtPollReplyOnChanges;
    }

    public void setSendArtPollReplyOnChanges(boolean sendArtPollReplyOnChanges) {
        if (this.sendArtPollReplyOnChanges != sendArtPollReplyOnChanges) {
            this.sendArtPollReplyOnChanges = sendArtPollReplyOnChanges;
            changed = true;
        }
    }

    public ArtPollBuilder withSendArtPollReplyOnChanges(boolean sendArtPollReplyOnChanges) {
        setSendArtPollReplyOnChanges(sendArtPollReplyOnChanges);
        return this;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {

        if (priority == null) {
            priority = Priority.Low;
        }

        if (this.priority != priority) {
            this.priority = priority;
            changed = true;
        }
    }

    public ArtPollBuilder withPriority(Priority priority) {
        setPriority(priority);
        return this;
    }
}
