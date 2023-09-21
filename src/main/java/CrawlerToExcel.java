
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CrawlerToExcel {
    private static int rowIndex = 1;
    private static final int BATCH_SIZE = 100;

    public static void main(String[] args) throws IOException {

        String url = "https://www.104.com.tw/jobs/search/?";
        String keyword = "ro=0&keyword=java%E5%B7%A5%E7%A8%8B%E5%B8%AB%20%E5%BE%8C%E7%AB%AF";
        String expansionType = "&expansionType=area%2Cspec%2Ccom%2Cjob%2Cwf%2Cwktm";
        String area = "&area=6001014000%2C6001016000";
        String order = "order=1&asc=0";
        String page = "&page=";
        int pageNumber = 1;
        String other = "&mode=s&jobsource=2018indexpoc&langFlag=0&langStatus=0";
        String excelPath = "C:\\Users\\怡靜陳\\Documents\\CrawlerToExcel.xlsx";
        List<Row> rowList = new ArrayList<>();//將資料儲存至一定量後批次輸出excel

        //try(...)无论是正常執行完毕还是发生異常，try-with-resources会自动關閉Workbook和FileOutputStream，釋放资源
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelPath)) {
            //create a new sheet
            String time = new GetTime().dateTime();
            Sheet sheet = workbook.createSheet(time);
            //header's value
            CellContentEnum[] values = CellContentEnum.values();
            int length = values.length;
            Row header = sheet.createRow(0);
            for (int i = 0; i < length; i++) {
                header.createCell(i).setCellValue(values[i].getLabel());
            }
            //第一頁爬蟲
            boolean hasData;
            int processCount=0;
            do {
                String newUrl = url + keyword + expansionType + area + order + page + pageNumber + other;
                Elements moreData = Jsoup.connect(newUrl).get().select("article.job-list-item");
                hasData = !moreData.isEmpty();
                if (hasData) {
                    processingData(moreData, sheet, rowList);
                }
                processCount += moreData.size();
                if (!hasData || processCount > BATCH_SIZE){
                    workbook.write(outputStream);
                    processCount =processCount-BATCH_SIZE;
                }
                pageNumber++;
            } while (hasData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //處理資料
    static void processingData(Elements data, Sheet sheet, List<Row> rowList) {
        for (Element element : data) {
            Row row = sheet.createRow(rowIndex++);
            //更新時間
            String updateTime = element.select(".b-tit__date").text();
            row.createCell(0).setCellValue(updateTime);
            //職務名稱
            String jobTitle = element.select(".js-job-link").text();
            row.createCell(1).setCellValue(jobTitle);
            //地區
            String jobArea = Objects.requireNonNull(element.select("ul.b-content li").first()).text();
            row.createCell(2).setCellValue(jobArea);
            //徵才公司
            String company = Objects.requireNonNull(element.select("ul a").first()).text();
            row.createCell(3).setCellValue(company);
            //薪資待遇
            String salary = Objects.requireNonNull(element.select("div .job-list-tag").select(".b-tag--default").first()).text();
            row.createCell(4).setCellValue(salary);
            //職務內容(部分)
            String info = element.select("p").text();
            row.createCell(5).setCellValue(info);
            //104連結
            String jobLink = element.select("a.js-job-link").attr("href");
            row.createCell(6).setCellValue(jobLink);

            rowList.add(row);
        }
    }
}