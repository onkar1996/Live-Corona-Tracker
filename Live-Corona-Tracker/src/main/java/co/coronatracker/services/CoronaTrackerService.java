package co.coronatracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import co.coronatracker.models.DataLocation;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class CoronaTrackerService {
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<DataLocation> allStats = new ArrayList<>();

	public List<DataLocation> getAllStats() {
		return allStats;
	}

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException {
		List<DataLocation> newStats = new ArrayList<>();
		StringReader csvBodyReader = new StringReader(webApiCallStringData(VIRUS_DATA_URL));
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			DataLocation locationStat = new DataLocation();
			locationStat.setState(record.get("Province/State").isEmpty() ? "--" : record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			int latestCases = Integer.parseInt(record.get(record.size() - 1));
			int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
			locationStat.setLatestTotalCases(latestCases);
			locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
			newStats.add(locationStat);

		}
		this.allStats = newStats;
	}

	public static String webApiCallStringData(String URL) {
		String data = "";
		OkHttpClient client = new OkHttpClient.Builder().build();
		Request request = new Request.Builder().url(URL).get().build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
			response = null;
		}
		if (response != null) {
			try {
				data = response.body().string();
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

}
