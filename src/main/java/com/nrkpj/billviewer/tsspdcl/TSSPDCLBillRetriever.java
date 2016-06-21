package com.nrkpj.billviewer.tsspdcl;

import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.nrkpj.billviewer.AreaWiseBillGenerator;
import com.nrkpj.billviewer.ServiceResponseToHtmlBuilder;
import com.nrkpj.billviewer.ServiceViewGenerator;

public class TSSPDCLBillRetriever implements ServiceViewGenerator {

    private final AreaWiseBillGenerator tsspdclAreaBillGenerator =
            new AreaWiseBillGenerator("tsspdcl", new TSSPDCLService(), new TSSPDCLResponseToHtmlBuilder());


    @Override
    public Element generate() {
        Element element = new Element(ServiceResponseToHtmlBuilder.DIV, "");
        for (String locality : System.getProperty("tsspdcl.area.keys", "").split(",")) {
            List<String> billNos = Arrays.asList(System.getProperty("tsspdcl." + locality + ".uscnos").split(","));
            element.appendChild(
                    new Element(ServiceResponseToHtmlBuilder.DIV, "").appendChild(new Element(Tag.valueOf("a"), "")
                    .attr("href", tsspdclAreaBillGenerator.generatePage(locality, billNos))
                            .text(System.getProperty("tsspdcl." + locality + ".name"))));
        }
        return element;
    }
}
