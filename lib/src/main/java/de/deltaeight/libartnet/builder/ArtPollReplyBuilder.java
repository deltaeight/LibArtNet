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

package de.deltaeight.libartnet.builder;

import de.deltaeight.libartnet.enums.ArtNet;
import de.deltaeight.libartnet.enums.EquipmentStyle;
import de.deltaeight.libartnet.enums.OemCode;
import de.deltaeight.libartnet.enums.OpCode;
import de.deltaeight.libartnet.packet.ArtPollReply;

import java.net.Inet4Address;
import java.util.Arrays;

public class ArtPollReplyBuilder implements ArtNetPacketBuilder<ArtPollReply> {

    public static final EquipmentStyle DEFAULT_EQUIPMENT_STYLE = EquipmentStyle.Config;

    private static final byte[] OP_CODE_BYTES = OpCode.OpPollReply.getBytesLittleEndian();

    private Inet4Address ipAddress;
    private int nodeVersion;
    private int netAddress;
    private int subnetAddress;
    private OemCode oemCode;
    private int ubeaVersion;
    private ArtPollReply.IndicatorState indicatorState;
    private ArtPollReply.PortAddressingAuthority portAddressingAuthority;
    private boolean bootedFromRom;
    private boolean rdmSupport;
    private boolean ubeaPresent;
    private String estaManufacturer;
    private String shortName;
    private String longName;
    private String nodeReport;
    private final ArtPollReply.PortType[] portTypes;
    private final ArtPollReply.InputStatus[] inputStatuses;
    private final ArtPollReply.OutputStatus[] outputStatuses;
    private final int[] inputUniverseAddresses;
    private final int[] outputUniverseAddresses;
    private final boolean[] macrosActive;
    private final boolean[] remotesActive;
    private EquipmentStyle equipmentStyle;
    private byte[] macAddress;
    private Inet4Address bindIp;
    private int bindIndex;
    private boolean webBrowserConfigurationSupport;
    private boolean ipIsDhcpConfigured;
    private boolean dhcpSupport;
    private boolean longPortAddressSupport;
    private boolean canSwitchToSACN;
    private boolean squawking;

    private boolean changed;
    private ArtPollReply artPollReply;

    public ArtPollReplyBuilder() {

        oemCode = OemCode.UNKNOWN;
        indicatorState = ArtPollReply.IndicatorState.Unknown;
        portAddressingAuthority = ArtPollReply.PortAddressingAuthority.Unknown;
        estaManufacturer = "D8";
        shortName = "LibArtNet";
        longName = "LibArtNet";
        nodeReport = "";
        portTypes = new ArtPollReply.PortType[]{ArtPollReply.PortType.DEFAULT, ArtPollReply.PortType.DEFAULT,
                ArtPollReply.PortType.DEFAULT, ArtPollReply.PortType.DEFAULT};
        inputStatuses = new ArtPollReply.InputStatus[]{ArtPollReply.InputStatus.DEFAULT,
                ArtPollReply.InputStatus.DEFAULT, ArtPollReply.InputStatus.DEFAULT, ArtPollReply.InputStatus.DEFAULT};
        outputStatuses = new ArtPollReply.OutputStatus[]{ArtPollReply.OutputStatus.DEFAULT,
                ArtPollReply.OutputStatus.DEFAULT, ArtPollReply.OutputStatus.DEFAULT, ArtPollReply.OutputStatus.DEFAULT};
        inputUniverseAddresses = new int[4];
        outputUniverseAddresses = new int[4];
        macrosActive = new boolean[8];
        remotesActive = new boolean[8];
        equipmentStyle = DEFAULT_EQUIPMENT_STYLE;
        macAddress = new byte[6];

        changed = true;
    }

    @Override
    public synchronized ArtPollReply build() {

        if (changed) {

            byte[] bytes = new byte[239];

            System.arraycopy(ArtNet.HEADER.getBytes(), 0, bytes, 0, 8);
            System.arraycopy(OP_CODE_BYTES, 0, bytes, 8, 2);

            if (ipAddress != null) {
                System.arraycopy(ipAddress.getAddress(), 0, bytes, 10, 4);
            }

            System.arraycopy(ArtNet.PORT.getBytesLittleEndian(), 0, bytes, 14, 2);

            bytes[16] = (byte) (nodeVersion >> 8);
            bytes[17] = (byte) nodeVersion;

            bytes[18] = (byte) netAddress;
            bytes[19] = (byte) subnetAddress;

            bytes[20] = (byte) (oemCode.getProduct().getProductCode() >> 8);
            bytes[21] = (byte) oemCode.getProduct().getProductCode();

            bytes[22] = (byte) ubeaVersion;
            bytes[23] |= indicatorState.getValue() << 6;
            bytes[23] |= portAddressingAuthority.getValue() << 4;

            if (bootedFromRom) {
                bytes[23] |= 0b00000100;
            }

            if (rdmSupport) {
                bytes[23] |= 0b00000010;
            }

            if (ubeaPresent) {
                bytes[23] |= 0b00000001;
            }

            if (estaManufacturer.length() > 0) {

                if (estaManufacturer.length() == 2) {
                    bytes[24] = estaManufacturer.getBytes()[1];
                }

                bytes[25] = estaManufacturer.getBytes()[0];
            }

            byte[] shortNameBytes = shortName.getBytes();
            System.arraycopy(shortNameBytes, 0, bytes, 26, shortNameBytes.length);

            byte[] longNameBytes = longName.getBytes();
            System.arraycopy(longNameBytes, 0, bytes, 44, longNameBytes.length);

            byte[] nodeReportBytes = nodeReport.getBytes();
            System.arraycopy(nodeReportBytes, 0, bytes, 108, nodeReportBytes.length);

            int portCounter = 0;
            for (ArtPollReply.PortType portType : portTypes) {
                if (portType.isOutputSupported() || portType.isInputSupported()) {
                    portCounter++;
                }
            }

            bytes[173] = (byte) portCounter;

            for (int i = 0; i < 4; i++) {
                bytes[174 + i] = portTypes[i].getByte();
                bytes[178 + i] = inputStatuses[i].getByte();
                bytes[182 + i] = outputStatuses[i].getByte();
                bytes[186 + i] = (byte) (int) inputUniverseAddresses[i];
                bytes[190 + i] = (byte) (int) outputUniverseAddresses[i];
            }

            for (int i = 0; i < 8; i++) {
                if (macrosActive[i]) {
                    bytes[195] |= 0b00000001 << i;
                }
                if (remotesActive[i]) {
                    bytes[196] |= 0b00000001 << i;
                }
            }

            bytes[200] = equipmentStyle.getValue();

            System.arraycopy(macAddress, 0, bytes, 201, 6);

            if (bindIp != null) {
                System.arraycopy(bindIp.getAddress(), 0, bytes, 207, 4);
            }

            bytes[211] = (byte) bindIndex;

            if (webBrowserConfigurationSupport) {
                bytes[212] |= 0b00000001;
            }

            if (ipIsDhcpConfigured) {
                bytes[212] |= 0b00000010;
            }

            if (dhcpSupport) {
                bytes[212] |= 0b00000100;
            }

            if (longPortAddressSupport) {
                bytes[212] |= 0b00001000;
            }

            if (canSwitchToSACN) {
                bytes[212] |= 0b00010000;
            }

            if (squawking) {
                bytes[212] |= 0b00100000;
            }

            artPollReply = new ArtPollReply(getIpAddress(), getNodeVersion(), getNetAddress(), getSubnetAddress(),
                    getOemCode(), getUbeaVersion(), getIndicatorState(), getPortAddressingAuthority(), isBootedFromRom(),
                    supportsRdm(), isUbeaPresent(), getEstaManufacturer(), getShortName(), getLongName(),
                    getNodeReport(), getPortTypes(), getInputStatuses(), getOutputStatuses(),
                    getInputUniverseAddresses(), getOutputUniverseAddresses(), getMacrosActive(), getRemotesActive(),
                    getEquipmentStyle(), getMacAddress(), getBindIndex(), supportsWebBrowserConfiguration(),
                    ipIsDhcpConfigured(), supportsDhcp(), supportsLongPortAddresses(), canSwitchToSACN(), isSquawking(),
                    bytes.clone());

            changed = false;
        }

        return artPollReply;
    }

    public Inet4Address getIpAddress() {
        return ipAddress;
    }

    public synchronized void setIpAddress(Inet4Address ipAddress) {
        if (this.ipAddress != ipAddress || this.ipAddress != null && !this.ipAddress.equals(ipAddress)) {
            this.ipAddress = ipAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIpAddress(Inet4Address ipAddress) {
        setIpAddress(ipAddress);
        return this;
    }

    public int getNodeVersion() {
        return nodeVersion;
    }

    public synchronized void setNodeVersion(int nodeVersion) {
        if (this.nodeVersion != nodeVersion) {
            this.nodeVersion = nodeVersion;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNodeVersion(int nodeVersion) {
        setNodeVersion(nodeVersion);
        return this;
    }

    public int getNetAddress() {
        return netAddress;
    }

    public synchronized void setNetAddress(int netAddress) {
        if (this.netAddress != netAddress) {
            if (0 > netAddress || netAddress > 127) {
                throw new IllegalArgumentException("Illegal net address!");
            }
            this.netAddress = netAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNetAddress(int net) {
        setNetAddress(net);
        return this;
    }

    public int getSubnetAddress() {
        return subnetAddress;
    }

    public synchronized void setSubnetAddress(int subnetAddress) {
        if (this.subnetAddress != subnetAddress) {
            if (0 > subnetAddress || subnetAddress > 15) {
                throw new IllegalArgumentException("Illegal subnet address!");
            }
            this.subnetAddress = subnetAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withSubnetAddress(int subnet) {
        setSubnetAddress(subnet);
        return this;
    }

    public OemCode getOemCode() {
        return oemCode;
    }

    public synchronized void setOemCode(OemCode oemCode) {

        if (oemCode == null) {
            oemCode = OemCode.UNKNOWN;
        }

        if (this.oemCode != oemCode) {
            this.oemCode = oemCode;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withOemCode(OemCode oemCode) {
        setOemCode(oemCode);
        return this;
    }

    public int getUbeaVersion() {
        return ubeaVersion;
    }

    public synchronized void setUbeaVersion(int ubeaVersion) {
        if (this.ubeaVersion != ubeaVersion) {
            if (0 > ubeaVersion || ubeaVersion > 255) {
                throw new IllegalArgumentException("Illegal UBEA version!");
            }
            this.ubeaVersion = ubeaVersion;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withUbeaVersion(int ubeaVersion) {
        setUbeaVersion(ubeaVersion);
        return this;
    }

    public ArtPollReply.IndicatorState getIndicatorState() {
        return indicatorState;
    }

    public synchronized void setIndicatorState(ArtPollReply.IndicatorState indicatorState) {

        if (indicatorState == null) {
            indicatorState = ArtPollReply.IndicatorState.Unknown;
        }

        if (this.indicatorState != indicatorState) {
            this.indicatorState = indicatorState;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIndicatorState(ArtPollReply.IndicatorState indicatorState) {
        setIndicatorState(indicatorState);
        return this;
    }

    public ArtPollReply.PortAddressingAuthority getPortAddressingAuthority() {
        return portAddressingAuthority;
    }

    public synchronized void setPortAddressingAuthority(ArtPollReply.PortAddressingAuthority portAddressingAuthority) {

        if (portAddressingAuthority == null) {
            portAddressingAuthority = ArtPollReply.PortAddressingAuthority.Unknown;
        }

        if (this.portAddressingAuthority != portAddressingAuthority) {
            this.portAddressingAuthority = portAddressingAuthority;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withPortAddressingAuthority(ArtPollReply.PortAddressingAuthority portAddressingAuthority) {
        setPortAddressingAuthority(portAddressingAuthority);
        return this;
    }

    public boolean isBootedFromRom() {
        return bootedFromRom;
    }

    public synchronized void setBootedFromRom(boolean bootedFromRom) {
        if (this.bootedFromRom != bootedFromRom) {
            this.bootedFromRom = bootedFromRom;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBootedFromRom(boolean bootedFromRom) {
        setBootedFromRom(bootedFromRom);
        return this;
    }

    public boolean supportsRdm() {
        return rdmSupport;
    }

    public synchronized void setRdmSupport(boolean rdmSupport) {
        if (this.rdmSupport != rdmSupport) {
            this.rdmSupport = rdmSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withRdmSupport(boolean rdmSupported) {
        setRdmSupport(rdmSupported);
        return this;
    }

    public boolean isUbeaPresent() {
        return ubeaPresent;
    }

    public synchronized void setUbeaPresent(boolean ubeaPresent) {
        if (this.ubeaPresent != ubeaPresent) {
            this.ubeaPresent = ubeaPresent;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withUbeaPresent(boolean ubeaPresent) {
        setUbeaPresent(ubeaPresent);
        return this;
    }

    public String getEstaManufacturer() {
        return estaManufacturer;
    }

    public synchronized void setEstaManufacturer(String estaManufacturer) {
        if (!this.estaManufacturer.equals(estaManufacturer)) {
            if (estaManufacturer == null) {
                estaManufacturer = "";
            }
            this.estaManufacturer = estaManufacturer.substring(0, Math.min(estaManufacturer.length(), 2));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withEstaManufacturer(String estaManufacturer) {
        setEstaManufacturer(estaManufacturer);
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public synchronized void setShortName(String shortName) {
        if (!this.shortName.equals(shortName)) {
            if (shortName == null) {
                shortName = "";
            }
            this.shortName = shortName.substring(0, Math.min(shortName.length(), 17));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withShortName(String shortName) {
        setShortName(shortName);
        return this;
    }

    public String getLongName() {
        return longName;
    }

    public synchronized void setLongName(String longName) {
        if (!this.longName.equals(longName)) {
            if (longName == null) {
                longName = "";
            }
            this.longName = longName.substring(0, Math.min(longName.length(), 63));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withLongName(String longName) {
        setLongName(longName);
        return this;
    }

    public String getNodeReport() {
        return nodeReport;
    }

    public synchronized void setNodeReport(String nodeReport) {
        if (!this.nodeReport.equals(nodeReport)) {
            if(nodeReport == null) {
                nodeReport = "";
            }
            this.nodeReport = nodeReport.substring(0, Math.min(nodeReport.length(), 63));
            changed = true;
        }
    }

    public ArtPollReplyBuilder withNodeReport(String nodeReport) {
        setNodeReport(nodeReport);
        return this;
    }

    public ArtPollReply.PortType[] getPortTypes() {
        return portTypes.clone();
    }

    public ArtPollReply.PortType getPortType(int index) {
        return portTypes[index];
    }

    public synchronized void setPortType(int index, ArtPollReply.PortType portType) {

        if (portType == null) {
            portType = ArtPollReply.PortType.DEFAULT;
        }

        if (!this.portTypes[index].equals(portType)) {
            portTypes[index] = portType;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withPortType(int index, ArtPollReply.PortType portType) {
        setPortType(index, portType);
        return this;
    }

    public ArtPollReply.InputStatus[] getInputStatuses() {
        return inputStatuses.clone();
    }

    public ArtPollReply.InputStatus getInputStatus(int index) {
        return inputStatuses[index];
    }

    public synchronized void setInputStatus(int index, ArtPollReply.InputStatus inputStatus) {

        if (inputStatus == null) {
            inputStatus = ArtPollReply.InputStatus.DEFAULT;
        }

        if (!this.inputStatuses[index].equals(inputStatus)) {
            inputStatuses[index] = inputStatus;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withInputStatus(int index, ArtPollReply.InputStatus inputStatus) {
        setInputStatus(index, inputStatus);
        return this;
    }

    public ArtPollReply.OutputStatus[] getOutputStatuses() {
        return outputStatuses.clone();
    }

    public ArtPollReply.OutputStatus getOutputStatus(int index) {
        return outputStatuses[index];
    }

    public synchronized void setOutputStatus(int index, ArtPollReply.OutputStatus outputStatus) {

        if (outputStatus == null) {
            outputStatus = ArtPollReply.OutputStatus.DEFAULT;
        }

        if (!this.outputStatuses[index].equals(outputStatus)) {
            outputStatuses[index] = outputStatus;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withOutputStatus(int index, ArtPollReply.OutputStatus outputStatus) {
        setOutputStatus(index, outputStatus);
        return this;
    }

    public int[] getInputUniverseAddresses() {
        return inputUniverseAddresses.clone();
    }

    public int getInputUniverseAddress(int index) {
        return inputUniverseAddresses[index];
    }

    public synchronized void setInputUniverseAddress(int index, int inputUniverseAddress) {
        if (inputUniverseAddresses[index] != inputUniverseAddress) {
            if (0 > inputUniverseAddress || inputUniverseAddress > 15) {
                throw new IllegalArgumentException("Illegal universe address!");
            }
            inputUniverseAddresses[index] = inputUniverseAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withInputUniverseAddress(int index, int inputUniverseAddress) {
        setInputUniverseAddress(index, inputUniverseAddress);
        return this;
    }

    public int[] getOutputUniverseAddresses() {
        return outputUniverseAddresses.clone();
    }

    public int getOutputUniverseAddress(int index) {
        return outputUniverseAddresses[index];
    }

    public synchronized void setOutputUniverseAddress(int index, int outputUniverseAddress) {
        if (outputUniverseAddresses[index] != outputUniverseAddress) {
            if (0 > outputUniverseAddress || outputUniverseAddress > 15) {
                throw new IllegalArgumentException("Illegal universe address!");
            }
            outputUniverseAddresses[index] = outputUniverseAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withOutputUniverseAddress(int index, int outputUniverseAddress) {
        setOutputUniverseAddress(index, outputUniverseAddress);
        return this;
    }

    public boolean[] getMacrosActive() {
        return macrosActive.clone();
    }

    public boolean isMacroActive(int index) {
        return macrosActive[index];
    }

    public void setMacroActive(int index, boolean isActive) {
        if (macrosActive[index] != isActive) {
            macrosActive[index] = isActive;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withMacroActive(int index, boolean isActive) {
        setMacroActive(index, isActive);
        return this;
    }

    public boolean[] getRemotesActive() {
        return remotesActive.clone();
    }

    public boolean isRemoteActive(int index) {
        return remotesActive[index];
    }

    public void setRemoteActive(int index, boolean isActive) {
        if (remotesActive[index] != isActive) {
            remotesActive[index] = isActive;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withRemoteActive(int index, boolean isActive) {
        setRemoteActive(index, isActive);
        return this;
    }

    public EquipmentStyle getEquipmentStyle() {
        return equipmentStyle;
    }

    public synchronized void setEquipmentStyle(EquipmentStyle equipmentStyle) {

        if (equipmentStyle == null) {
            equipmentStyle = DEFAULT_EQUIPMENT_STYLE;
        }

        if (this.equipmentStyle != equipmentStyle) {
            this.equipmentStyle = equipmentStyle;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withEquipmentStyle(EquipmentStyle equipmentStyle) {
        setEquipmentStyle(equipmentStyle);
        return this;
    }

    public byte[] getMacAddress() {
        return macAddress.clone();
    }

    public synchronized void setMacAddress(byte[] macAddress) {

        if (macAddress == null) {
            macAddress = new byte[6];
        }

        if (macAddress.length != 6) {
            throw new IllegalArgumentException("Illegal MAC address!");
        }

        if (!Arrays.equals(this.macAddress, macAddress)) {
            this.macAddress = macAddress;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withMacAddress(byte[] macAddress) {
        setMacAddress(macAddress);
        return this;
    }

    public Inet4Address getBindIp() {
        return bindIp;
    }

    public synchronized void setBindIp(Inet4Address bindIp) {
        if (this.bindIp != bindIp || this.bindIp != null && !this.bindIp.equals(bindIp)) {
            this.bindIp = bindIp;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBindIp(Inet4Address bindIp) {
        setBindIp(bindIp);
        return this;
    }

    public int getBindIndex() {
        return bindIndex;
    }

    public synchronized void setBindIndex(int bindIndex) {
        if (this.bindIndex != bindIndex) {
            if (0 > bindIndex || bindIndex > 255) {
                throw new IllegalArgumentException("Illegal bind index!");
            }
            this.bindIndex = bindIndex;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withBindIndex(int bindIndex) {
        setBindIndex(bindIndex);
        return this;
    }

    public boolean supportsWebBrowserConfiguration() {
        return webBrowserConfigurationSupport;
    }

    public synchronized void setWebBrowserConfigurationSupport(boolean webBrowserConfigurationSupport) {
        if (this.webBrowserConfigurationSupport != webBrowserConfigurationSupport) {
            this.webBrowserConfigurationSupport = webBrowserConfigurationSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withWebBrowserConfigurationSupport(boolean webBrowserConfigurationSupport) {
        setWebBrowserConfigurationSupport(webBrowserConfigurationSupport);
        return this;
    }

    public boolean ipIsDhcpConfigured() {
        return ipIsDhcpConfigured;
    }

    public synchronized void setIpIsDhcpConfigured(boolean ipIsDhcpConfigured) {
        if (this.ipIsDhcpConfigured != ipIsDhcpConfigured) {
            this.ipIsDhcpConfigured = ipIsDhcpConfigured;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withIpIsDhcpConfigured(boolean ipIsDhcpConfigured) {
        setIpIsDhcpConfigured(ipIsDhcpConfigured);
        return this;
    }

    public boolean supportsDhcp() {
        return dhcpSupport;
    }

    public synchronized void setDhcpSupport(boolean dhcpSupport) {
        if (this.dhcpSupport != dhcpSupport) {
            this.dhcpSupport = dhcpSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withDhcpSupport(boolean dhcpSupport) {
        setDhcpSupport(dhcpSupport);
        return this;
    }

    public boolean supportsLongPortAddresses() {
        return longPortAddressSupport;
    }

    public synchronized void setLongPortAddressSupport(boolean longPortAddressSupport) {
        if (this.longPortAddressSupport != longPortAddressSupport) {
            this.longPortAddressSupport = longPortAddressSupport;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withLongPortAddressSupport(boolean longPortAddressSupport) {
        setLongPortAddressSupport(longPortAddressSupport);
        return this;
    }

    public boolean canSwitchToSACN() {
        return canSwitchToSACN;
    }

    public synchronized void setCanSwitchToSACN(boolean canSwitchToSACN) {
        if (this.canSwitchToSACN != canSwitchToSACN) {
            this.canSwitchToSACN = canSwitchToSACN;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withCanSwitchToSACN(boolean canSwitchToSACN) {
        setCanSwitchToSACN(canSwitchToSACN);
        return this;
    }

    public boolean isSquawking() {
        return squawking;
    }

    public synchronized void setSquawking(boolean squawking) {
        if (this.squawking != squawking) {
            this.squawking = squawking;
            changed = true;
        }
    }

    public ArtPollReplyBuilder withSquawking(boolean squawking) {
        setSquawking(squawking);
        return this;
    }
}
