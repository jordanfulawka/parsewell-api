package com.jordanfulawka.parsewell.service.jobposting;

import com.jordanfulawka.parsewell.exception.JobFetchException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class JobPostingFetchServiceImpl implements JobPostingFetchService {

    private static final Logger log = LoggerFactory.getLogger(JobPostingFetchServiceImpl.class);
    private static final int MAX_CHARS = 30_000;
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";



    @Override
    public String fetchWebpageHtml(String url) {

        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent(UA)
                    .header("Accept-Language", "en-CA,en;q=0.9")
                    .referrer("https://www.google.com/")
                    .timeout(20_000)
                    .maxBodySize(0)
                    .followRedirects(true)
                    .get();
        } catch (HttpStatusException e) {
            int status = e.getStatusCode();
            JobFetchException.Reason reason = switch(status) {
                case 401, 403, 429 -> JobFetchException.Reason.BLOCKED;
                case 404, 410 -> JobFetchException.Reason.NOT_FOUND;
                default -> JobFetchException.Reason.UNREACHABLE;
            };
            log.warn("Fetch failed for {} - HTTP {}", url, e.getStatusCode());
            throw new JobFetchException(reason, "Site returned HTTP " + status, e);
        } catch (IOException e) {
            log.warn("Fetch failed for {}", url, e);
            throw new JobFetchException(JobFetchException.Reason.UNREACHABLE, "Could not reach the URL", e);
        }

        String jsonLd = extractJobPostingJsonLd(doc);

        doc.select("style, noscript, svg, iframe, nav, footer, header").remove();
        doc.select("script").remove();

        Element body = doc.body();
        String bodyText = body == null ? "" : structuredText(body);

        StringBuilder sb = new StringBuilder()
                .append(doc.title()).append("\n")
                .append("URL: ").append(url).append("\n");

        if (!jsonLd.isEmpty()) {
            sb.append("\n--- STRUCTURED JOB DATA (schema.org JobPosting) ---\n")
                    .append(jsonLd)
                    .append("\n--- END STRUCTURED DATA ---\n");
        }

        sb.append("\n--- PAGE BODY ---\n").append(bodyText);

        String out = sb.toString();
        return out.length() > MAX_CHARS ? out.substring(0, MAX_CHARS) : out;
    }

    private String extractJobPostingJsonLd(Document doc) {
        for (Element script : doc.select("script[type=application/ld+json]")) {
            String data = script.data();
            if (data.contains("JobPosting")) {
                return data.length() > 15_000 ? data.substring(0, 15_000) : data;
            }
        }
        return "";
    }

    private String structuredText(Element root) {
        StringBuilder sb = new StringBuilder();
        NodeTraversor.traverse(new NodeVisitor() {
            @Override
            public void head(Node node, int depth) {
                if(node instanceof TextNode tn) {
                    sb.append(tn.text());
                } else if (node instanceof Element el) {
                    if("br".equals(el.tagName())) {
                        sb.append("\n");
                    } else if ("li".equals(el.tagName())) {
                        newline(sb);
                        sb.append("- ");
                    } else if (el.isBlock()) {
                        newline(sb);
                    }
                }
            }

            @Override
            public void tail(Node node, int depth) {
                if(node instanceof Element el && el.isBlock()) {
                    newline(sb);
                }
            }

            private void newline(StringBuilder b) {
                if (b.length() > 0 && b.charAt(b.length() - 1) != '\n') {
                    b.append("\n");
                }
            }
        }, root);

        return sb.toString()
                .replaceAll("[ \\t\\u00A0]+", " ")
                .replaceAll(" *\\n *", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }
}
