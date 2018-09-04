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

package de.deltaeight.libartnet.descriptors;

import java.util.Objects;

/**
 * Describes the status of a data port.
 *
 * @author Julian Rabe
 */
public abstract class PortStatus {

    private final boolean includesTestPackets;
    private final boolean includesSIPs;
    private final boolean includesTextPackets;

    PortStatus(boolean includesTestPackets,
               boolean includesSIPs,
               boolean includesTextPackets) {

        this.includesTestPackets = includesTestPackets;
        this.includesSIPs = includesSIPs;
        this.includesTextPackets = includesTextPackets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(includesTestPackets, includesSIPs, includesTextPackets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortStatus that = (PortStatus) o;
        return includesTestPackets == that.includesTestPackets &&
                includesSIPs == that.includesSIPs &&
                includesTextPackets == that.includesTextPackets;
    }

    public boolean includesTestPackets() {
        return includesTestPackets;
    }

    public boolean includesSIPs() {
        return includesSIPs;
    }

    public boolean includesTextPackets() {
        return includesTextPackets;
    }
}
