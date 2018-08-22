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

public class OemEnumGenerator {

    private static final Pattern pattern = Pattern.compile("#define (Oem[a-zA-Z0-9]+)[ \\xA0]+" +
            "0x([a-fA-F0-9]{4})[ \\xA0]+" +
            "//Manufacturer: (.+)[ \\xA0]+" +
            "ProductName: (.+)[ \\xA0]+" +
            "NumDmxIn: (\\d+)[ \\xA0]+" +
            "NumDmxOut: (\\d+)[ \\xA0]+" +
            "DmxPortPhysical: ((?:[nyNY])|(?:[nN]o)|(?:[yY]es)|(?:[pP]hysical))[ \\xA0]+" +
            "RdmSupported: ((?:[nyNY])|(?:[nN]o)|(?:[yY]es))[ \\xA0]+" +
            "SupportEmail: (.*)[ \\xA0]+" +
            "SupportName: (.*)[ \\xA0]+" +
            "CoWeb:.*");

    public static void main(String... args) throws IOException, URISyntaxException {

        String template = new String(Files.readAllBytes(Paths.get(OemEnumGenerator.class
                .getResource("OemCodeTemplate.java").toURI())));

        List<String> lines = Files.readAllLines(Paths.get(OemEnumGenerator.class
                .getResource("Art-NetOemCodes.h").toURI()));

        StringJoiner parsedLines = new StringJoiner(",\n");
        LinkedList<String> unparsedLines = new LinkedList<>();

        int productCounter = 0;

        for (String line : lines) {

            String enumEntry = createEnumEntry(line);

            if (enumEntry != null) {
                parsedLines.add(enumEntry);
                productCounter++;
            } else {
                unparsedLines.add(line);
            }
        }

        Files.write(Paths.get("lib/src/main/java/de/deltaeight/libartnet/enums/OemCode.java"),
                template.replace("$ENTRIES$", parsedLines.toString()).getBytes());

        System.out.println("Result written to OemCode.java!");
        System.out.println("Products parsed: " + productCounter);
        System.out.println("Unparsed lines:  " + unparsedLines.size());
        System.out.println();
        System.out.println("Unparsed lines follow:");
        System.out.println("######################");

        for (String line : unparsedLines) {
            System.out.println(line);
        }
    }

    private static String createEnumEntry(String line) {

        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {

            boolean dmxPortsPhysical = matcher.group(7).equalsIgnoreCase("y")
                    || matcher.group(7).equalsIgnoreCase("yes")
                    || matcher.group(7).equalsIgnoreCase("physical");

            boolean supportsRdm = matcher.group(8).equalsIgnoreCase("y")
                    || matcher.group(8).equalsIgnoreCase("yes");

            return matcher.group(1) + "(new Product(" + Integer.parseInt(matcher.group(2), 16) + ", \""
                    + matcher.group(3) + "\", \"" + matcher.group(4) + "\", " + Integer.parseInt(matcher.group(5)) + ", "
                    + Integer.parseInt(matcher.group(6)) + ", " + dmxPortsPhysical + ", " + supportsRdm + ", \""
                    + matcher.group(9) + "\", \"" + matcher.group(10) + "\"))";
        }

        return null;
    }
}
