package com.nrkpj.billviewer;

import static com.nrkpj.billviewer.ServiceResponseToHtmlBuilder.DIV;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class AreaWiseBillGenerator {

    private final String serviceName;
    private final BillService service;
    private final ServiceResponseToHtmlBuilder responseToHtml;

    public AreaWiseBillGenerator(String serviceName, BillService service, ServiceResponseToHtmlBuilder responseToHtml) {
        this.serviceName = serviceName;
        this.service = service;
        this.responseToHtml = responseToHtml;
    }

    public String generatePage(String area, Collection<String> serviceNos) {
        File htmlPage = new File(directory(serviceName), area + ".html");
        try (FileWriter writer = new FileWriter(htmlPage)) {
            Element body = new Element(Tag.valueOf("body"), "");
            serviceNos.stream().map(this::billHtmlView).forEach(body::appendChild);
            writer.write(new Element(Tag.valueOf("html"), "").appendChild(body).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return htmlPage.getPath();
    }

    private Element billHtmlView(String billNo) {
        try {
            Element element = responseToHtml.htmlFor(service.billDetailsFor(billNo));
            if (element == null) {
                element = new Element(DIV, "")
                        .text("Failed to retreive bill details for: " + billNo);
            }
            return element;
        } catch (Exception e) {
            return new Element(DIV, "")
                    .text("Failed to retreive bill details for: " + billNo + "\n Error: " + e.getMessage());
        }
    }

    private File directory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return file;
    }
}
