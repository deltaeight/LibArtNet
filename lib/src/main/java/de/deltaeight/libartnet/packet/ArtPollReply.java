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

package de.deltaeight.libartnet.packet;

import de.deltaeight.libartnet.enums.EquipmentStyle;
import de.deltaeight.libartnet.enums.OemCode;

import java.net.Inet4Address;

/**
 * Represents an {@code ArtPollReply} packet containing node information about the sender. This is sent periodically or
 * as an answer to {@link ArtPoll} packets.
 * <p>
 * See the <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a> for details.
 *
 * @author Julian Rabe
 * @see ArtPoll
 * @see de.deltaeight.libartnet.ArtPollReplyBuilder
 * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
 */
public class ArtPollReply extends ArtNetPacket {

    private final Inet4Address ipAddress;
    private final int nodeVersion;
    private final int netAddress;
    private final int subnetAddress;
    private final OemCode oemCode;
    private final int ubeaVersion;
    private final IndicatorState indicatorState;
    private final PortAddressingAuthority portAddressingAuthority;
    private final boolean bootedFromRom;
    private final boolean rdmSupport;
    private final boolean ubeaPresent;
    private final String estaManufacturer;
    private final String shortName;
    private final String longName;
    private final String nodeReport;
    private final PortType[] portTypes;
    private final InputStatus[] inputStatuses;
    private final OutputStatus[] outputStatuses;
    private final int[] inputUniverseAddresses;
    private final int[] outputUniverseAddresses;
    private final boolean[] macrosActive;
    private final boolean[] remotesActive;
    private final EquipmentStyle equipmentStyle;
    private final byte[] macAddress;
    private final int bindIndex;
    private final boolean webBrowserConfigurationSupport;
    private final boolean ipIsDhcpConfigured;
    private final boolean dhcpSupport;
    private final boolean longPortAddressSupport;
    private final boolean canSwitchToSACN;
    private final boolean squawking;

    public ArtPollReply(Inet4Address ipAddress,
                        int nodeVersion,
                        int netAddress,
                        int subnetAddress,
                        OemCode oemCode,
                        int ubeaVersion,
                        IndicatorState indicatorState,
                        PortAddressingAuthority portAddressingAuthority,
                        boolean bootedFromRom,
                        boolean rdmSupport,
                        boolean ubeaPresent,
                        String estaManufacturer,
                        String shortName,
                        String longName,
                        String nodeReport,
                        PortType[] portTypes,
                        InputStatus[] inputStatuses,
                        OutputStatus[] outputStatuses,
                        int[] inputUniverseAddresses,
                        int[] outputUniverseAddresses,
                        boolean[] macrosActive,
                        boolean[] remotesActive,
                        EquipmentStyle equipmentStyle,
                        byte[] macAddress,
                        int bindIndex,
                        boolean webBrowserConfigurationSupport,
                        boolean ipIsDhcpConfigured,
                        boolean dhcpSupport,
                        boolean longPortAddressSupport,
                        boolean canSwitchToSACN,
                        boolean squawking,
                        byte[] bytes) {

        super(bytes);

        this.ipAddress = ipAddress;
        this.nodeVersion = nodeVersion;
        this.netAddress = netAddress;
        this.subnetAddress = subnetAddress;
        this.oemCode = oemCode;
        this.ubeaVersion = ubeaVersion;
        this.indicatorState = indicatorState;
        this.portAddressingAuthority = portAddressingAuthority;
        this.bootedFromRom = bootedFromRom;
        this.rdmSupport = rdmSupport;
        this.ubeaPresent = ubeaPresent;
        this.estaManufacturer = estaManufacturer.substring(0, Math.min(estaManufacturer.length(), 2));
        this.shortName = shortName.substring(0, Math.min(shortName.length(), 17));
        this.longName = longName.substring(0, Math.min(longName.length(), 63));
        this.nodeReport = nodeReport;
        this.portTypes = portTypes;
        this.inputStatuses = inputStatuses;
        this.outputStatuses = outputStatuses;
        this.inputUniverseAddresses = inputUniverseAddresses;
        this.outputUniverseAddresses = outputUniverseAddresses;
        this.macrosActive = macrosActive;
        this.remotesActive = remotesActive;
        this.equipmentStyle = equipmentStyle;
        this.macAddress = macAddress;
        this.bindIndex = bindIndex;
        this.webBrowserConfigurationSupport = webBrowserConfigurationSupport;
        this.ipIsDhcpConfigured = ipIsDhcpConfigured;
        this.dhcpSupport = dhcpSupport;
        this.longPortAddressSupport = longPortAddressSupport;
        this.canSwitchToSACN = canSwitchToSACN;
        this.squawking = squawking;
    }

    public Inet4Address getIpAddress() {
        return ipAddress;
    }

    public int getNodeVersion() {
        return nodeVersion;
    }

    public int getNetAddress() {
        return netAddress;
    }

    public int getSubnetAddress() {
        return subnetAddress;
    }

    public OemCode getOemCode() {
        return oemCode;
    }

    public int getUbeaVersion() {
        return ubeaVersion;
    }

    public IndicatorState getIndicatorState() {
        return indicatorState;
    }

    public PortAddressingAuthority getPortAddressingAuthority() {
        return portAddressingAuthority;
    }

    public boolean isBootedFromRom() {
        return bootedFromRom;
    }

    public boolean supportsRdm() {
        return rdmSupport;
    }

    public boolean isUbeaPresent() {
        return ubeaPresent;
    }

    public String getEstaManufacturer() {
        return estaManufacturer;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public String getNodeReport() {
        return nodeReport;
    }

    public PortType[] getPortTypes() {
        return portTypes.clone();
    }

    public InputStatus[] getInputStatuses() {
        return inputStatuses.clone();
    }

    public OutputStatus[] getOutputStatuses() {
        return outputStatuses.clone();
    }

    public int[] getInputUniverseAddresses() {
        return inputUniverseAddresses.clone();
    }

    public int[] getOutputUniverseAddresses() {
        return outputUniverseAddresses.clone();
    }

    public boolean[] getMacrosActive() {
        return macrosActive.clone();
    }

    public boolean[] getRemotesActive() {
        return remotesActive.clone();
    }

    public EquipmentStyle getEquipmentStyle() {
        return equipmentStyle;
    }

    public byte[] getMacAddress() {
        return macAddress.clone();
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public boolean supportsWebBrowserConfiguration() {
        return webBrowserConfigurationSupport;
    }

    public boolean ipIsDhcpConfigured() {
        return ipIsDhcpConfigured;
    }

    public boolean supportsDhcp() {
        return dhcpSupport;
    }

    public boolean supportsLongAddresses() {
        return longPortAddressSupport;
    }

    public boolean canSwitchToSACN() {
        return canSwitchToSACN;
    }

    public boolean isSquawking() {
        return squawking;
    }

    /**
     * Represents an indicator state.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public enum IndicatorState {

        Unknown(0b00000000),
        LocateIdentify(0b00000001),
        Mute(0b00000010),
        Normal(0b00000011);

        private final byte value;

        IndicatorState(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * Represents the port addressing authority.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public enum PortAddressingAuthority {

        Unknown(0b00000000),
        FrontPanel(0b00000001),
        Network(0b00000010),
        NotUsed(0b00000011);

        private final byte value;

        PortAddressingAuthority(int value) {
            this.value = (byte) value;
        }

        public byte getValue() {
            return value;
        }
    }

    /**
     * Represents a port type.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public static class PortType {

        public static final PortType DEFAULT = new PortType(false, false, Protocol.DMX512);

        private final boolean outputSupported;
        private final boolean inputSupported;
        private final Protocol protocol;

        public PortType(boolean outputSupported, boolean inputSupported, Protocol protocol) {
            this.outputSupported = outputSupported;
            this.inputSupported = inputSupported;
            this.protocol = protocol;
        }

        public byte getByte() {
            byte result = 0x00;
            if (outputSupported) {
                result |= 0b10000000;
            }
            if (inputSupported) {
                result |= 0b01000000;
            }
            result |= protocol.getValue();
            return result;
        }

        public boolean isOutputSupported() {
            return outputSupported;
        }

        public boolean isInputSupported() {
            return inputSupported;
        }

        public Protocol getProtocol() {
            return protocol;
        }

        /**
         * Represents a Protocol for input/output ports.
         *
         * @author Julian Rabe
         * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
         */
        public enum Protocol {

            DMX512(0b00000000),
            MIDI(0b00000001),
            AVAB(0b00000010),
            COLORTRAN_CMX(0b00000011),
            ADB625(0b00000100),
            ARTNET(0b00000101);

            private final byte value;

            Protocol(int value) {
                this.value = (byte) value;
            }

            public byte getValue() {
                return value;
            }
        }
    }

    private static abstract class PortStatus {

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

    /**
     * Represents the status of an input port.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public static class InputStatus extends PortStatus {

        public static final InputStatus DEFAULT = new InputStatus(false, false, false, false, false, false);

        private final boolean dataReceived;
        private final boolean inputDisabled;
        private final boolean errorsDetected;

        public InputStatus(boolean dataReceived,
                           boolean includesTestPackets,
                           boolean includesSIPs,
                           boolean includesTextPackets,
                           boolean inputDisabled,
                           boolean errorsDetected) {

            super(includesTestPackets, includesSIPs, includesTextPackets);

            this.dataReceived = dataReceived;
            this.inputDisabled = inputDisabled;
            this.errorsDetected = errorsDetected;
        }

        public byte getByte() {
            byte result = 0x00;
            if (dataReceived) {
                result |= 0b10000000;
            }
            if (includesTestPackets()) {
                result |= 0b01000000;
            }
            if (includesSIPs()) {
                result |= 0b00100000;
            }
            if (includesTextPackets()) {
                result |= 0b00010000;
            }
            if (inputDisabled) {
                result |= 0b00001000;
            }
            if (errorsDetected) {
                result |= 0b00000100;
            }
            return result;
        }

        public boolean isDataReceived() {
            return dataReceived;
        }

        public boolean isInputDisabled() {
            return inputDisabled;
        }

        public boolean isErrorsDetected() {
            return errorsDetected;
        }
    }

    /**
     * Represents the status of an output port.
     *
     * @author Julian Rabe
     * @see <a href="https://art-net.org.uk/resources/art-net-specification/">Art-Net Specification</a>
     */
    public static class OutputStatus extends PortStatus {

        public static final OutputStatus DEFAULT = new OutputStatus(false, false, false, false, false, false, false, false);

        private final boolean dataTransmitted;
        private final boolean merging;
        private final boolean outputShort;
        private final boolean mergeModeLTP;
        private final boolean transmittingSACN;

        public OutputStatus(boolean dataTransmitted,
                            boolean includesTestPackets,
                            boolean includesSIPs,
                            boolean includesTextPackets,
                            boolean merging,
                            boolean outputShort,
                            boolean mergeModeLTP,
                            boolean transmittingSACN) {

            super(includesTestPackets, includesSIPs, includesTextPackets);

            this.dataTransmitted = dataTransmitted;
            this.merging = merging;
            this.outputShort = outputShort;
            this.mergeModeLTP = mergeModeLTP;
            this.transmittingSACN = transmittingSACN;
        }

        public byte getByte() {
            byte result = 0x00;
            if (dataTransmitted) {
                result |= 0b10000000;
            }
            if (includesTestPackets()) {
                result |= 0b01000000;
            }
            if (includesSIPs()) {
                result |= 0b00100000;
            }
            if (includesTextPackets()) {
                result |= 0b00010000;
            }
            if (merging) {
                result |= 0b00001000;
            }
            if (outputShort) {
                result |= 0b00000100;
            }
            if (mergeModeLTP) {
                result |= 0b00000010;
            }
            if (transmittingSACN) {
                result |= 0b00000001;
            }
            return result;
        }

        public boolean isDataTransmitted() {
            return dataTransmitted;
        }

        public boolean isMerging() {
            return merging;
        }

        public boolean isOutputShort() {
            return outputShort;
        }

        public boolean isMergeModeLTP() {
            return mergeModeLTP;
        }

        public boolean isTransmittingSACN() {
            return transmittingSACN;
        }
    }
}
