package com.nrkpj.biilviewer.tsspdcl;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import com.nrkpj.biilviewer.BillService;

class TSSPDCLService implements BillService {
    private final HttpClient client;

    TSSPDCLService() {
        client = HttpClientBuilder.create().build();
        try {
            client.execute(new HttpGet(
                    "https://www.tssouthernpower.com/CPDCL_Home.portal?_nfpb=true&_pageLabel=CPDCL_Home_portal_page_109"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to TSSPDCL service", e);
        }
    }

    @Override
    public String billDetailsFor(String uscno) {
        try {
            System.out.println("Fetching Bill details for: " + uscno);
            HttpResponse response = client.execute(new HttpPost(tsspdclBillLink(uscno)));
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to retrieve bill details for uscno: " + uscno);
            }
            return IOUtils.toString(response.getEntity().getContent(), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("Completed Fetching details for: " + uscno);
        }
    }

    private String tsspdclBillLink(String uscno) {
        return "https://www.tssouthernpower.com/CPDCL_Home.portal?_nfpb=true&_windowLabel=OnlineBillEnquiry_LeftPanel_3&OnlineBillEnquiry_LeftPanel_3_actionOverride=%2Fpages%2FBillingInfo%2FOnlineBillEnquiryDetailsBilling&OnlineBillEnquiry_LeftPanel_3circle=Select&OnlineBillEnquiry_LeftPanel_3ero=Select&OnlineBillEnquiry_LeftPanel_3eroHidden=Select&OnlineBillEnquiry_LeftPanel_3scno=&OnlineBillEnquiry_LeftPanel_3uscno="
                + uscno;
    }
}
