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

package de.deltaeight.libartnet.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is a nifty tool to auto-generate the {@code OemCode} enum. All you need is {@code Art-NetOemCodes.h} from
 * Artistic License Ltd. which you can obtain
 * <a href="http://artisticlicence.com/WebSiteMaster/Software/Art-Net/Art-NetOemCodes.h">here</a>. You will then need
 * to put it into the resources of this package. {@link #main(String...)} will then generate the enum under
 * {@code lib/src/main/java/de/deltaeight/libartnet/descriptors/OemCode.java}.
 *
 * @author Julian Rabe
 */
public class OemJsonGenerator {

    private static final Pattern pattern = Pattern.compile("#define (Oem[a-zA-Z0-9_]+)[ \\xA0]+" +
            "0x([a-fA-F0-9]{4})[ \\xA0]+" +
            "//Manufacturer:[ \\xA0](.+)[ \\xA0]+" +
            "ProductName:[ \\xA0](.+)[ \\xA0]+" +
            "NumDmxIn:[ \\xA0](\\d+)[ \\xA0]+" +
            "NumDmxOut:[ \\xA0](\\d+)[ \\xA0]+" +
            "DmxPortPhysical:[ \\xA0]((?:[jnyJNY])|(?:[nN]o)|(?:[yY]es)|(?:[pP]hysical)|\\d).*[ \\xA0]+" +
            "RdmSupported:[ \\xA0]((?:[jnyJNY])|(?:[nN]o)|(?:[yY]es)|(?:RDM))[ \\xA0]+" +
            "SupportEmail:[ \\xA0](.*)[ \\xA0]+" +
            "SupportName:[ \\xA0](.*)[ \\xA0]+" +
            "CoWeb:.*");

    public static void main(String... args) throws IOException, URISyntaxException {

        List<String> lines = Files.readAllLines(Paths.get(OemJsonGenerator.class
                .getResource("Art-NetOemCodes.h").toURI()));

        StringJoiner parsedLines = new StringJoiner(",\n");
        LinkedList<String> unparsedLines = new LinkedList<>();

        int productCounter = 0;

        for (String line : lines) {

            String codeEntry = createCodeEntry(line);

            if (codeEntry != null) {
                parsedLines.add(codeEntry);
                productCounter++;
            } else {
                unparsedLines.add(line);
            }
        }

        Files.write(Paths.get("lib/src/main/resources/de/deltaeight/libartnet/descriptors/OemCodes.json"),
                ("[\n" + parsedLines.toString() + "\n]").getBytes());

        System.out.println("Result written to OemCodes.json!");
        System.out.println("Products parsed: " + productCounter);
        System.out.println("Unparsed lines:  " + unparsedLines.size());
        System.out.println();
        System.out.println("Unparsed lines follow:");
        System.out.println("######################");

        for (String line : unparsedLines) {
            if (!line.replaceAll("\\s", "").isEmpty()) {
                System.out.println(line);
            }
        }
    }

    private static String createCodeEntry(String line) {

        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {

            boolean dmxPortsPhysical;
            try {
                dmxPortsPhysical = Integer.parseInt(matcher.group(7)) > 0;
            } catch (NumberFormatException e) {
                dmxPortsPhysical = matcher.group(7).equalsIgnoreCase("y")
                        || matcher.group(7).equalsIgnoreCase("yes")
                        || matcher.group(7).equalsIgnoreCase("physical");
            }

            boolean supportsRdm = matcher.group(8).equalsIgnoreCase("y")
                    || matcher.group(8).equalsIgnoreCase("yes")
                    || matcher.group(8).equalsIgnoreCase("rdm");

            return "    { " +
                    "\"oemCode\": \"" + matcher.group(1) + "\", " +
                    "\"productCode\": " + Integer.parseInt(matcher.group(2), 16) + ", " +
                    "\"manufacturer\": \"" + matcher.group(3) + "\", " +
                    "\"name\": \"" + matcher.group(4) + "\", " +
                    "\"dmxOutputs\": " + Integer.parseInt(matcher.group(5)) + ", " +
                    "\"dmxInputs\": " + Integer.parseInt(matcher.group(6)) + ", " +
                    "\"dmxPortsPhysical\": \"" + dmxPortsPhysical + "\", " +
                    "\"supportsRdm\": \"" + supportsRdm + "\", " +
                    "\"supportEmail\": \"" + matcher.group(9) + "\", " +
                    "\"supportName\": \"" + matcher.group(10) + "\" " +
                    "}";
        }

        return null;
    }
}
