package com.fredlecoat.backend.services.implementations;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.requests.MainPlayerRequest;
import com.fredlecoat.backend.services.DashboardService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.services.ScraperService;

@Service
public class ScraperServiceSimpleImpl implements ScraperService {

    private final TextToDataConverterServiceImpl textConverter;
    private final DashboardService dashboardService;
    private final PlayerService playerService;

    public ScraperServiceSimpleImpl(
        TextToDataConverterServiceImpl textConverter,
        DashboardService dashboardService,
        PlayerService playerService
    ) {
        this.textConverter = textConverter;
        this.dashboardService = dashboardService;
        this.playerService = playerService;
    }

   @Override
    public void getPersonalData() {
        Map<String, String> data = this.dashboardService.getPersonalData();
        
        data.replaceAll((k, v) -> this.textConverter.convert(k, v));
        System.out.println(data);

        MainPlayerRequest mainPlayer = new MainPlayerRequest(
                "Danaleight", 
                Integer.parseInt(data.get("money")), 
                Integer.parseInt(data.get("level")), 
                Integer.parseInt(data.get("experience"))
            );

        System.out.println(this.playerService.saveMainPlayer(mainPlayer));
    }

    @Override
    public void getDashboardActivities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDashboardActivities'");
    }

}
