/*
 * LibArtNet
 *
 * Art-Net(TM) Designed by and Copyright Artistic Licence Holdings Ltd
 *
 * Copyright (c) 2020 Julian Rabe
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

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Imports products with OEM codes as assigned by Artistic License Ltd. and makes them searchable.
 *
 * @author Julian Rabe
 * @see de.deltaeight.libartnet.packets.ArtPollReply
 * @see <a href="http://artisticlicence.com/WebSiteMaster/Software/Art-Net/Art-NetOemCodes.h">Art-NetOemCodes.h</a>
 */
public class OemCode {

    private static final HashMap<Integer, Product> productsByProductCode;
    private static final HashMap<String, Product> productsByOemCode;
    private static final Product unknownProduct;

    static {

        productsByProductCode = new HashMap<>();
        productsByOemCode = new HashMap<>();
        unknownProduct = new Product("Unknown", 32767, "N/A",
                "N/A", 0, 0, false, false,
                "N/A", "N/A");

        addProduct(unknownProduct);

        Product[] products = null;
        try {
            products = new Gson().fromJson(new String(Files.readAllBytes(Paths.get(OemCode.class
                    .getResource("OemCodes.json").toURI()))), Product[].class);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        if (products != null) {
            for (Product product : products) {
                addProduct(product);
            }
        }
    }

    private static void addProduct(Product product) {
        productsByProductCode.put(product.getProductCode(), product);
        productsByOemCode.put(product.getOemCode(), product);
    }

    /**
     * @param productCode The code to search for.
     * @return The {@link Product} matching {@code productCode} or the unknown product if none exists.
     */
    public static Product getProductByProductCode(int productCode) {
        return productsByProductCode.getOrDefault(productCode, unknownProduct);
    }

    /**
     * @param oemCode The code to search for.
     * @return The {@link Product} matching {@code oemCode} or the unknown product if none exists.
     */
    public static Product getProductByOemCode(String oemCode) {
        return productsByOemCode.getOrDefault(oemCode, unknownProduct);
    }

    /**
     * @return The unknown product.
     */
    public static Product getUnknownProduct() {
        return unknownProduct;
    }
}
