package com.jordanfulawka.parsewell.service.jobposting;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;

@Service
public class JobPostingFetchServiceImpl implements JobPostingFetchService {
    @Override
    public String fetchWebpageHtml(String url) {

        Document webDoc;
        try {
            webDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(10000)
                    .get();

        } catch (Exception e) {
            return "";
        }

        webDoc.select("script, style, noscript, svg, iframe, nav, footer, header, form").remove();
        webDoc.select("*").forEach(el ->
                el.childNodesCopy().stream()
                        .filter(node -> node.nodeName().equals("#comment"))
                        .forEach(Node::remove));

        String text = webDoc.body().text();
        if(text.length() > 20000) {
            text = text.substring(0, 20000);
        }

        return (webDoc.title() + "\n" + "URL: " + url + "\n" + text);
    }
}
