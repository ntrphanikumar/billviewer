package com.nrkpj.biilviewer;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public interface ServiceResponseToHtmlBuilder {
    Tag DIV = Tag.valueOf("div");

    Element htmlFor(String response);
}
