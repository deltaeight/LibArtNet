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

import de.deltaeight.libartnet.Product;

import java.util.TreeMap;

public enum OemCode {

    $ENTRIES$,

    UNKNOWN(new Product(32767, "N/A", "N/A", 0, 0, false, false, "N/A", "N/A"));

    private static final TreeMap<Integer, OemCode> products = new TreeMap<>();

    static {
        for (OemCode value : OemCode.values()) {
            products.put(value.getProduct().getProductCode(), value);
        }
    }

    private final Product product;

    OemCode(Product product) {
        this.product = product;
    }

    public static OemCode getOemCode(int productCode) {
        return products.getOrDefault(productCode, UNKNOWN);
    }

    public static OemCode getOemCode(byte productCode) {
        return getOemCode((int) productCode);
    }

    public Product getProduct() {
        return product;
    }
}
