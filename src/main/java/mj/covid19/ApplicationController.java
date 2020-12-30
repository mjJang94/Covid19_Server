package mj.covid19;

import mj.covid19.info.CovidInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
public class ApplicationController {


    private static final int SUCCESS = 200;

    private static final String COVID_START_DATE = "20200108";


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public CovidInfo addDogData() {

        CovidInfo covidInfo = new CovidInfo();

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String filename = "test" + format.format(date) + ".xml";
//        //on ubuntu
        String filePath = "/home/ubuntu/file/" + filename;
//        //on mac
//        String filePath = "/Users/paymint/"+ filename;

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


        @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
        public void requestCovidInfo() {

            try {

                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

                URL url = new URL("http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?serviceKey=bJbteBumBuvg1VROB4L5tsHdqOwOwf7lT5Kic%2BRKqF00S4lPk4hVGPxsf4SbHIAIlhEttryMl0fOM4q4e45WFQ%3D%3D&pageNo=1&numOfRows=10&startCreateDt=" + COVID_START_DATE + "&endCreateDt=" + format.format(date));

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

                    String filename = "test" + format.format(date) + ".xml";

                    //on ubuntu
                    String filePath = "/home/ubuntu/file/" + filename;
                    //on mac
                    //String filePath = "/Users/paymint/"+ filename;

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

