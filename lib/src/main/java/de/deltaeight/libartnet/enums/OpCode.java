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

package de.deltaeight.libartnet.enums;

public enum OpCode {

    OpPoll(0x2000),
    OpPollReply(0x2100),
    OpDiagData(0x2300),
    OpCommand(0x2400),
    OpOutput(0x5000),
    OpDmx(OpOutput),
    OpNzs(0x5100),
    OpSync(0x5200),
    OpAddress(0x6000),
    OpInput(0x7000),
    OpTodRequest(0x8000),
    OpTodData(0x8100),
    OpTodControl(0x8200),
    OpRdm(0x8300),
    OpRdmSub(0x8400),
    OpVideoSetup(0xA010),
    OpVideoPalette(0xA020),
    OpVideoData(0xA040),
    OpFirmwareMaster(0xF200),
    OpFirmwareReply(0xF300),
    OpFileTnMaster(0xF400),
    OpFileFnMaster(0xF500),
    OpFileFnReply(0xF600),
    OpIpProg(0xF800),
    OpIpProgReply(0xF900),
    OpMedia(0x9000),
    OpMediaPatch(0x9100),
    OpMediaControl(0x9200),
    OpMediaControlReply(0x9300),
    OpTimeCode(0x9700),
    OpTimeSync(0x9800),
    OpTrigger(0x9900),
    OpDirectory(0x9A00),
    OpDirectoryReply(0x9B00);

    private final byte[] bytes;

    OpCode(int value) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >> 8);
        bytes[1] = (byte) value;
        this.bytes = bytes;
    }

    OpCode(OpCode opCode) {
        this.bytes = opCode.bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getBytesLittleEndian() {
        byte[] result = new byte[2];
        result[0] = bytes[1];
        result[1] = bytes[0];
        return result;
    }
}
