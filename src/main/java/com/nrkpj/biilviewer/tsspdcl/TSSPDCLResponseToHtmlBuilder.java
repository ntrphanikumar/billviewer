package com.nrkpj.biilviewer.tsspdcl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.nrkpj.biilviewer.ServiceResponseToHtmlBuilder;

class TSSPDCLResponseToHtmlBuilder implements ServiceResponseToHtmlBuilder {
    @Override
    public Element htmlFor(String response) {
        return new Element(DIV, "").attr("style", "padding-bottom:10px")
                .appendChild(Jsoup.parse(response).select("table.Table_Border1").first());
    }
}
