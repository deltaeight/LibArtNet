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

package de.deltaeight.libartnet.builders;

import de.deltaeight.libartnet.descriptors.ArtNet;
import de.deltaeight.libartnet.descriptors.OpCode;
import de.deltaeight.libartnet.descriptors.TimeCodeType;
import de.deltaeight.libartnet.packets.ArtPoll;
import de.deltaeight.libartnet.packets.ArtTimeCode;

/**
 * Builds instances of {@link ArtTimeCode}.
 * <p>
 * Default {@link TimeCodeType} is {@link TimeCodeType#Film}.
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtTimeCode
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtTimeCodeBuilder extends ArtNetPacketBuilder<ArtTimeCode> {

    private static final byte[] OP_CODE_BYTES = OpCode.OpTimeCode.getBytesLittleEndian();

    private TimeCodeType type;
    private int hours;
    private int minutes;
    private int seconds;
    private int frames;

    private boolean changed;
    private ArtTimeCode artTimeCode;

    public ArtTimeCodeBuilder() {
        type = TimeCodeType.Film;
        changed = true;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtTimeCode} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    public ArtTimeCode build() {
        if (changed) {

            byte[] bytes = new byte[19];
            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);
            System.arraycopy(ArtNet.PROTOCOL_REVISION.getBytes(), 0, bytes, 10, 2);

            bytes[14] = (byte) frames;
            bytes[15] = (byte) seconds;
            bytes[16] = (byte) minutes;
            bytes[17] = (byte) hours;
            bytes[18] = type.getByte();

            artTimeCode = new ArtTimeCode(type, hours, minutes, seconds, frames, bytes.clone());

            changed = false;
        }

        return artTimeCode;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link ArtPoll} instance.
     * @see ArtNetPacketBuilder#build()
     */
    @Override
    public ArtTimeCode buildFromBytes(byte[] packetData) {
        if (packetData[8] == OP_CODE_BYTES[0] && packetData[9] == OP_CODE_BYTES[1]) {

            return new ArtTimeCode(TimeCodeType.getTimeCodeType(packetData[18]), packetData[17], packetData[16],
                    packetData[15], packetData[14], packetData.clone());
        }
        return null;
    }

    public TimeCodeType getType() {
        return type;
    }

    public void setType(TimeCodeType type) {
        if (this.type != type) {

            if (type == null) {
                type = TimeCodeType.Film;
            }

            if (frames > Math.ceil(type.getFramerate())) {
                frames = (int) (Math.ceil(type.getFramerate()) - 1);
            }

            this.type = type;
            changed = true;
        }
    }

    public ArtTimeCodeBuilder withType(TimeCodeType type) {
        setType(type);
        return this;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        if (this.hours != hours) {

            if (0 > hours || hours > 23) {
                throw new IllegalArgumentException("Illegal hours value!");
            }

            this.hours = hours;
            changed = true;
        }
    }

    public ArtTimeCodeBuilder withHours(int hours) {
        setHours(hours);
        return this;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (this.minutes != minutes) {

            if (0 > minutes || minutes > 59) {
                throw new IllegalArgumentException("Illegal minutes value!");
            }

            this.minutes = minutes;
            changed = true;
        }
    }

    public ArtTimeCodeBuilder withMinutes(int minutes) {
        setMinutes(minutes);
        return this;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        if (this.seconds != seconds) {

            if (0 > seconds || seconds > 59) {
                throw new IllegalArgumentException("Illegal seconds value!");
            }

            this.seconds = seconds;
            changed = true;
        }
    }

    public ArtTimeCodeBuilder withSeconds(int seconds) {
        setSeconds(seconds);
        return this;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        if (this.frames != frames) {

            if (0 > frames || frames > Math.ceil(type.getFramerate()) - 1) {
                throw new IllegalArgumentException("Illegal frames value!");
            }

            this.frames = frames;
            changed = true;
        }
    }

    public ArtTimeCodeBuilder withFrames(int frames) {
        setFrames(frames);
        return this;
    }
}
