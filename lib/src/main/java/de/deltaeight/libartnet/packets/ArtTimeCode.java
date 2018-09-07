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

import de.deltaeight.libartnet.builders.ArtTimeCodeBuilder;
import de.deltaeight.libartnet.descriptors.TimeCodeType;

import java.util.Objects;

/**
 * Represents an {@code ArtTimeCode} packet containing timecode information.
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtTimeCodeBuilder
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtTimeCode extends ArtNetPacket {

    private final TimeCodeType type;
    private final int hours;
    private final int minutes;
    private final int seconds;
    private final int frames;

    public ArtTimeCode(TimeCodeType type, int hours, int minutes, int seconds, int frames, byte[] bytes) {

        super(bytes);
        this.type = type;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.frames = frames;
    }

    @Override
    public int hashCode() {
        return Objects.hash(frames, seconds, minutes, hours, type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtTimeCode that = (ArtTimeCode) o;
        return frames == that.frames &&
                seconds == that.seconds &&
                minutes == that.minutes &&
                hours == that.hours &&
                type == that.type;
    }

    public TimeCodeType getType() {
        return type;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getFrames() {
        return frames;
    }
}
