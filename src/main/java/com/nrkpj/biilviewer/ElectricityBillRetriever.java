package com.nrkpj.biilviewer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class ElectricityBillRetriever {
    private static final Tag DIV = Tag.valueOf("div");
    private final HttpClient client = HttpClientBuilder.create().build();

    public ElectricityBillRetriever() {
        try {
            client.execute(new HttpGet(
                    "https://www.tssouthernpower.com/CPDCL_Home.portal?_nfpb=true&_pageLabel=CPDCL_Home_portal_page_109"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Element retrieveBillsFor(String area, Collection<String> scnos) {
        Element element = new Element(DIV, "").appendChild(new Element(DIV, "").text(area)
                .attr("style", "font-size:20px;font-weight:bold;padding:5px 0 15px 300px"));
        scnos.forEach(scno -> element.appendChild(retrieveBillFor(scno)));
        return element;
    }

    private Element retrieveBillFor(String uscno) {
        HttpPost post = new HttpPost(
                "https://www.tssouthernpower.com/CPDCL_Home.portal?_nfpb=true&_windowLabel=OnlineBillEnquiry_LeftPanel_3&OnlineBillEnquiry_LeftPanel_3_actionOverride=%2Fpages%2FBillingInfo%2FOnlineBillEnquiryDetailsBilling&OnlineBillEnquiry_LeftPanel_3circle=Select&OnlineBillEnquiry_LeftPanel_3ero=Select&OnlineBillEnquiry_LeftPanel_3eroHidden=Select&OnlineBillEnquiry_LeftPanel_3scno=&OnlineBillEnquiry_LeftPanel_3uscno="
                        + uscno);
        Element element =
                new Element(DIV, "").addClass("billdiv").addClass(uscno + "").attr("style",
                "padding-bottom:10px");
        try {
            return client.execute(post, new ResponseHandler<Element>() {
                public Element handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                    return element.appendChild(Jsoup.parse(IOUtils.toString(response.getEntity().getContent()))
                            .select("table.Table_Border1").first());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return element;
    }

    public static void main(String[] args) throws ClientProtocolException, IOException {
        Properties properties = new Properties();
        InputStream stream =
                ElectricityBillRetriever.class.getClassLoader().getSystemResourceAsStream("electricity.properties");
        properties.load(stream);
        ElectricityBillRetriever retriever = new ElectricityBillRetriever();
        String[] localityKeys = properties.getProperty("electricity.area.keys", "").split(",");
        Element body = new Element(Tag.valueOf("body"), "");
        for (String key : localityKeys) {
            body.appendChild(retriever.retrieveBillsFor(properties.getProperty("area." + key + ".name"),
                    Arrays.asList(properties.getProperty("area." + key + ".uscnos").split(","))));
        }
        FileWriter writer = new FileWriter(properties.getProperty("electricity.summary.html"));
        writer.write(new Element(Tag.valueOf("html"), "").appendChild(body).toString());
        writer.flush();
        writer.close();
    }
}
