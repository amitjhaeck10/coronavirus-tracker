package io.java.maask.coronavirustracker.service;

import io.java.maask.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronavirusService {

    private static  String VIRUS_DATE = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private List<LocationStats> allStats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron="* * * * * *")
    public void fetchCoronavirusData() throws IOException, InterruptedException {
        List<LocationStats> localStats = new ArrayList<>();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request =  HttpRequest.newBuilder().uri(URI.create(VIRUS_DATE)).build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        StringReader csvReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
        for (CSVRecord record : records) {
            LocationStats stats = new LocationStats();
            stats.setState(record.get("Province/State"));
            stats.setCountry(record.get("Country/Region"));
            stats.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
            int diff = Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2));
            stats.setDiffFromPrevDay(diff);
            localStats.add(stats);
        }
        allStats = localStats;
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

}
