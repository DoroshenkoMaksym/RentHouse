package com.renthouse.olx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class OlxProcessing {

    private final String OLX_BASE_URL = "http://lvov.lv.olx.ua/nedvizhimost/arenda-kvartir/dolgosrochnaya-arenda-kvartir/?page=";
    private final String PHANTOMJS_PATH = "/usr/bin/phantomjs";
    private final int PAGE_COUNT = 2;
    private WebDriver driver = new PhantomJSDriver();
    private Actions action = new Actions(driver);

//    public static void main(String[] args) throws IOException {
//        long startTime = System.currentTimeMillis();
//        String OLX = "http://lvov.lv.olx.ua/nedvizhimost/arenda-kvartir/dolgosrochnaya-arenda-kvartir/?page=";
//        OlxProcessing olx = new OlxProcessing();
//        Set<String> linksSet = olx.getPagesForProcessing(OLX);
//        olx.writeCollectionToFile(linksSet, "/home/wilson/linksList.txt");
//        long endTime = System.currentTimeMillis();
//        System.out.println("Size - " + linksSet.size());
//        System.out.println("That took " + (endTime - startTime) + " milliseconds");
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        OlxProcessing olx = new OlxProcessing();
        File file = new File(olx.PHANTOMJS_PATH);
        System.setProperty("phantomjs.binary.path", file.getAbsolutePath());
        Set<String> linksSet = olx.getPagesForProcessing(olx.OLX_BASE_URL);
        olx.processPagesList(linksSet);
    }

    private void processPagesList(Set<String> pages) throws IOException, InterruptedException {
        for (String pageUrl : pages) {
            System.out.println(pageUrl);
            processPage(pageUrl);
        }
    }

    private void processPage(String pageUrl) throws IOException, InterruptedException {
        driver.get(pageUrl);
        Boolean isPresent = driver.findElements(By.xpath("//div[@class='clr fleft marginleft15 contactitem brkword']")).size() > 0;
        if (isPresent) {
            WebElement element = driver.findElement(By.xpath("//div[@class='clr fleft marginleft15 contactitem brkword']"));
            action.doubleClick(element).perform();
            String text = element.getText();
            System.out.println(text);
        }
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
        } while (pageNumber != PAGE_COUNT);
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

}
