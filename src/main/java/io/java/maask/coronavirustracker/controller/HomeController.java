package io.java.maask.coronavirustracker.controller;

import io.java.maask.coronavirustracker.service.CoronavirusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CoronavirusService coronavirusService;

    @GetMapping("/tracker")
    public String home(Model model){
        model.addAttribute("locationStats",coronavirusService.getAllStats());
        int totalReportedCases = coronavirusService.getAllStats().stream().mapToInt(stats->stats.getLatestTotalCases()).sum();
        model.addAttribute("totalReportedCases",totalReportedCases);
        int totalNewCases = coronavirusService.getAllStats().stream().mapToInt(stats->stats.getDiffFromPrevDay()).sum();
        model.addAttribute("totalNewCases",totalNewCases);
        return "Home";
    }

}
