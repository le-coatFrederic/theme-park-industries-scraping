package com.fredlecoat.backend.services.implementations;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fredlecoat.backend.entities.dtos.MainPlayerRequest;
import com.fredlecoat.backend.services.DashboardService;
import com.fredlecoat.backend.services.PlayerService;
import com.fredlecoat.backend.services.ScraperService;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class ScraperServiceSimpleImpl implements ScraperService {

    @Autowired
    private TextToDataConverterServiceImpl textConverter;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private PlayerService playerService;

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
