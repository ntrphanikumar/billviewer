package com.nrkpj.biilviewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.nrkpj.biilviewer.tsspdcl.TSSPDCLBillRetriever;

public class Main {
    public static void main(String[] args) throws IOException {
        readProperties().forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

        File htmlPage = new File("index.html");
        try (FileWriter writer = new FileWriter(htmlPage)) {
            Element body = new Element(Tag.valueOf("body"), "")
                    .appendChild(new Element(Tag.valueOf("div"), "").attr("style",
                            "font-weight:bold;font-size:15px;padding:5px 0 10px 200px").text("Bill Details"))
                    .appendChild(new Element(Tag.valueOf("table"), "").attr("border", "1").attr("cellpadding", "10")
                            .appendChild(
                                    serviceRow(System.getProperty("tsspdcl.shortname"), new TSSPDCLBillRetriever())));
            writer.write(new Element(Tag.valueOf("html"), "").appendChild(body).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Element serviceRow(String name, ServiceViewGenerator generator) {
        return new Element(Tag.valueOf("tr"), "").appendChild(new Element(Tag.valueOf("td"), "").text(name))
                .appendChild(new Element(Tag.valueOf("td"), "").appendChild(generator.generate()));
    }

    private static Properties readProperties() throws IOException {
        Properties properties = new Properties();
        Main.class.getClassLoader();
        InputStream stream = ClassLoader.getSystemResourceAsStream("bills.properties");
        properties.load(stream);
        return properties;
    }
}
