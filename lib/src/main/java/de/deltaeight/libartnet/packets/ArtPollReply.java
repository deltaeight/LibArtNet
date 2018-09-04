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

import de.deltaeight.libartnet.descriptors.*;

import java.util.Arrays;
import java.util.Objects;

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

    private final byte[] ipAddress;
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
    private final byte[] bindIp;
    private final int bindIndex;
    private final boolean webBrowserConfigurationSupport;
    private final boolean ipIsDhcpConfigured;
    private final boolean dhcpSupport;
    private final boolean longPortAddressSupport;
    private final boolean canSwitchToSACN;
    private final boolean squawking;

    public ArtPollReply(byte[] ipAddress,
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
                        byte[] bindIp,
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
        this.bindIp = bindIp;
        this.bindIndex = bindIndex;
        this.webBrowserConfigurationSupport = webBrowserConfigurationSupport;
        this.ipIsDhcpConfigured = ipIsDhcpConfigured;
        this.dhcpSupport = dhcpSupport;
        this.longPortAddressSupport = longPortAddressSupport;
        this.canSwitchToSACN = canSwitchToSACN;
        this.squawking = squawking;
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(nodeVersion, netAddress, subnetAddress, oemCode, ubeaVersion, indicatorState,
                portAddressingAuthority, bootedFromRom, rdmSupport, ubeaPresent, estaManufacturer, shortName, longName,
                nodeReport, equipmentStyle, bindIndex, webBrowserConfigurationSupport, ipIsDhcpConfigured, dhcpSupport,
                longPortAddressSupport, canSwitchToSACN, squawking);

        result = 31 * result + Arrays.hashCode(ipAddress);
        result = 31 * result + Arrays.hashCode(portTypes);
        result = 31 * result + Arrays.hashCode(inputStatuses);
        result = 31 * result + Arrays.hashCode(outputStatuses);
        result = 31 * result + Arrays.hashCode(inputUniverseAddresses);
        result = 31 * result + Arrays.hashCode(outputUniverseAddresses);
        result = 31 * result + Arrays.hashCode(macrosActive);
        result = 31 * result + Arrays.hashCode(remotesActive);
        result = 31 * result + Arrays.hashCode(macAddress);
        result = 31 * result + Arrays.hashCode(bindIp);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtPollReply that = (ArtPollReply) o;
        return nodeVersion == that.nodeVersion &&
                netAddress == that.netAddress &&
                subnetAddress == that.subnetAddress &&
                ubeaVersion == that.ubeaVersion &&
                bootedFromRom == that.bootedFromRom &&
                rdmSupport == that.rdmSupport &&
                ubeaPresent == that.ubeaPresent &&
                bindIndex == that.bindIndex &&
                webBrowserConfigurationSupport == that.webBrowserConfigurationSupport &&
                ipIsDhcpConfigured == that.ipIsDhcpConfigured &&
                dhcpSupport == that.dhcpSupport &&
                longPortAddressSupport == that.longPortAddressSupport &&
                canSwitchToSACN == that.canSwitchToSACN &&
                squawking == that.squawking &&
                Arrays.equals(ipAddress, that.ipAddress) &&
                oemCode == that.oemCode &&
                indicatorState == that.indicatorState &&
                portAddressingAuthority == that.portAddressingAuthority &&
                Objects.equals(estaManufacturer, that.estaManufacturer) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(longName, that.longName) &&
                Objects.equals(nodeReport, that.nodeReport) &&
                Arrays.equals(portTypes, that.portTypes) &&
                Arrays.equals(inputStatuses, that.inputStatuses) &&
                Arrays.equals(outputStatuses, that.outputStatuses) &&
                Arrays.equals(inputUniverseAddresses, that.inputUniverseAddresses) &&
                Arrays.equals(outputUniverseAddresses, that.outputUniverseAddresses) &&
                Arrays.equals(macrosActive, that.macrosActive) &&
                Arrays.equals(remotesActive, that.remotesActive) &&
                equipmentStyle == that.equipmentStyle &&
                Arrays.equals(macAddress, that.macAddress) &&
                Arrays.equals(bindIp, that.bindIp);
    }

    public byte[] getIpAddress() {
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

    public byte[] getBindIp() {
        return bindIp;
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
}
