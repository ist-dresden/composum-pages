package com.composum.pages.commons.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/** A (very) quick and dirty generator for composum tables in XML format from a markdown format. */
public class TableXMLGeneratorFromMarkdown {

    public static void main(String[] args) throws IOException {
        StringBuffer buf = new StringBuffer();
        System.out.println("Paste the table in markdown-format into this and finish with an empty line. Example:" +
                "H1 | H2 | H3\n" +
                "--- | --- | ---\n" +
                "Text1 | Text2 | Text3\n");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean isHeader = true;
        String tablesuffix = rndsuffix();
        buf.append("                <table" + tablesuffix + "\n" +
                "                        jcr:primaryType=\"cpp:Container\"\n" +
                "                        sling:resourceType=\"composum/pages/components/composed/table\"\n" +
                "                        bordered=\"{Boolean}true\"\n" +
                "                        striped=\"{Boolean}true\">\n");
        String line;
        while (StringUtils.isNotBlank(line = in.readLine().trim())) {
            List<String> fields = Arrays.asList(line.split("\\s\\|\\s"));
            if (line.matches(".*---.*")) {
                isHeader = false;
                continue;
            }
            if (line.trim().isEmpty()) {
                break;
            }
            String suffix = rndsuffix();
            buf.append(
                    "                    <row" + suffix + " jcr:primaryType=\"cpp:Container\" " +
                            "sling:resourceType=\"composum/pages/components/composed/table/row\" head=\"{Boolean}" + isHeader + "\">\n");
            for (String field : fields) {
                buf.append("                        <cell" + rndsuffix() + " jcr:primaryType=\"cpp:Element\" " +
                        "sling:resourceType=\"composum/pages/components/composed/table/cell\" head=\"{Boolean}" + isHeader + "\"\n" +
                        "                                text=\"" + StringEscapeUtils.escapeXml11(field.trim()) +
                        "\"/>\n");
            }
            buf.append("                    </row" + suffix + ">\n");
            isHeader = false;
        }
        buf.append("                </table" + tablesuffix + ">\n");
        System.out.println();
        System.out.println();
        System.out.println(buf);
    }

    static String rndsuffix() {
        return "_" + RandomStringUtils.randomNumeric(5);
    }

}
