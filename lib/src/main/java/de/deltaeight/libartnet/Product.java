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

/**
 * Represents a product defined by Artistic License Ltd.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.descriptors.OemCode
 */
public class Product {

    private final int productCode;
    private final String manufacturer;
    private final String name;
    private final int dmxOutputs;
    private final int dmxInputs;
    private final boolean dmxPortsPhysical;
    private final boolean supportsRdm;
    private final String supportEmail;
    private final String supportName;

    /**
     * @param productCode      The product code assigned by Artistic License Ltd.
     * @param manufacturer     The manufacturer of the product
     * @param name             The name of the product
     * @param dmxOutputs       The amount of DMX outputs
     * @param dmxInputs        The amount of DMX inputs
     * @param dmxPortsPhysical Whether the product has physical DMX ports or not.
     * @param supportsRdm      Whether the product supports RDM or not.
     * @param supportEmail     The Email address of the customer support.
     * @param supportName      The name of the customer support.
     */
    public Product(int productCode,
                   String manufacturer,
                   String name,
                   int dmxOutputs,
                   int dmxInputs,
                   boolean dmxPortsPhysical,
                   boolean supportsRdm,
                   String supportEmail,
                   String supportName) {

        this.productCode = productCode;
        this.manufacturer = manufacturer;
        this.name = name;
        this.dmxOutputs = dmxOutputs;
        this.dmxInputs = dmxInputs;
        this.dmxPortsPhysical = dmxPortsPhysical;
        this.supportsRdm = supportsRdm;
        this.supportEmail = supportEmail;
        this.supportName = supportName;
    }

    public int getProductCode() {
        return productCode;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getName() {
        return name;
    }

    public int getDmxOutputs() {
        return dmxOutputs;
    }

    public int getDmxInputs() {
        return dmxInputs;
    }

    public boolean isDmxPortsPhysical() {
        return dmxPortsPhysical;
    }

    public boolean supportsRdm() {
        return supportsRdm;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public String getSupportName() {
        return supportName;
    }
}
