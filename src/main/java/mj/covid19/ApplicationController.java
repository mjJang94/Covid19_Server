package mj.covid19;

import mj.covid19.info.CovidInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@RestController
public class ApplicationController {


    private static final int SUCCESS = 200;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public CovidInfo addDogData() {

        CovidInfo covidInfo = new CovidInfo();


        String filename = "CovidData.xml";

        //on ubuntu
        String filePath = "/home/ubuntu/file/" + filename;


        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String inputLine;
            StringBuffer s = new StringBuffer();

            while ((inputLine = br.readLine()) != null) {
                s.append(inputLine);
            }

            covidInfo.setCovidXmlInfo(s.toString());

        } catch (Exception e) {
            e.printStackTrace();
            covidInfo.setCovidXmlInfo("");
        }

        return covidInfo;
    }

    @Component
    public class DemoScheduler {


        @Scheduled(cron = " 0 0 10,12 * * MON-FRI", zone = "Asia/Seoul")
        public void requestCovidInfo() {

            try {

                Date todayDate = new Date();

                //오늘 날짜
                SimpleDateFormat todayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

                //어제 날짜
                Date yesterdayDate = new Date();
                yesterdayDate = new Date(yesterdayDate.getTime()+(1000*60*60*24*-1));
                SimpleDateFormat yesterDayFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);



                URL url = new URL("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=bJbteBumBuvg1VROB4L5tsHdqOwOwf7lT5Kic%2BRKqF00S4lPk4hVGPxsf4SbHIAIlhEttryMl0fOM4q4e45WFQ%3D%3D&pageNo=1&numOfRows=10&startCreateDt=" + yesterDayFormat.format(yesterdayDate) + "&endCreateDt=" + todayFormat.format(todayDate));

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (SUCCESS == con.getResponseCode()) {

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    StringBuffer s = new StringBuffer();


                    while ((inputLine = bufferedReader.readLine()) != null) {
                        s.append(inputLine);
                    }

                    bufferedReader.close();

                    String result = s.toString();

                    String filename = "CovidData.xml";

                    //on ubuntu
                    String filePath = "/home/ubuntu/file/" + filename;

                    FileWriter fileWriter = new FileWriter(filePath, false);

                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(result);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}

