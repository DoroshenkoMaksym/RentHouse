package com.renthouse.olx;

//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OlxProcessing {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        String OLX = "http://lvov.lv.olx.ua/nedvizhimost/arenda-kvartir/dolgosrochnaya-arenda-kvartir/?page=";
        OlxProcessing olx = new OlxProcessing();
        Set<String> linksSet = olx.getPagesForProcessing(OLX);
        olx.writeCollectionToFile(linksSet, "/home/wilson/linksList.txt");
        long endTime = System.currentTimeMillis();
        System.out.println("Size - " + linksSet.size());
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }

    private Set<String> getPagesForProcessing(String siteUrl) throws IOException {
        Set<String> pagesForProcessing = new HashSet<String>();
        int pageNumber = 1;
        String currentUrl;
        do {
            currentUrl = siteUrl + pageNumber;
            System.out.println(currentUrl);
            Document doc = Jsoup.connect(currentUrl).timeout(10000).get();
            Elements tableToParse = doc.select("table#offers_table");
            Elements links = tableToParse.select("a[href]");
            for (Element link : links) {
                String linkText = link.attr("abs:href");
                if (linkText.startsWith("http://lvov.lv.olx.ua/obyavlenie/")) {
                    pagesForProcessing.add(linkText);
                }
            }
            pageNumber++;
        } while (pageNumber != 346);
        return pagesForProcessing;
    }

    private void writeCollectionToFile(Collection collection, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            out.write(it.next() + "\n");
        }
        out.close();
    }

//    private int getURLStatus(String url) throws IOException {
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(url);
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//        int code = response.getStatusLine().getStatusCode();
//        return code;
//    }

}
