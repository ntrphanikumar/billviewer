package com.nrkpj.billviewer.tsspdcl;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import com.nrkpj.billviewer.BillService;

class TSSPDCLService implements BillService {
    private final HttpClient client;

    TSSPDCLService() {
        client = HttpClientBuilder.create().build();
        try {
            client.execute(new HttpGet(System.getProperty("tsspdcl.ping.url")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to connect to TSSPDCL service", e);
        }
    }

    @Override
    public String billDetailsFor(String uscno) {
        try {
            HttpResponse response = client.execute(new HttpPost(tsspdclBillLink(uscno)));
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed to retrieve bill details for uscno: " + uscno);
            }
            return IOUtils.toString(response.getEntity().getContent(), Charset.forName("utf-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String tsspdclBillLink(String uscno) {
        return String.format(System.getProperty("tsspdcl.uscno.url"), uscno);
    }
}
