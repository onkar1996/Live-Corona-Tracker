package co.coronatracker.controllers;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import co.coronatracker.models.DataLocation;
import co.coronatracker.services.CoronaTrackerService;

@Controller
public class HomeController {

	@Autowired
	CoronaTrackerService coronaVirusDataService;

	@GetMapping("/")
	public String home(Model model) {
		List<DataLocation> allStats = coronaVirusDataService.getAllStats();
		int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", NumberFormat.getNumberInstance(Locale.US).format(totalReportedCases));
		model.addAttribute("totalNewCases", NumberFormat.getNumberInstance(Locale.US).format(totalNewCases));
		return "home";
	}
}
