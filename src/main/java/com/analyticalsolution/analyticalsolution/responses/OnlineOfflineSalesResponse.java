package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineOfflineSalesResponse {

    List<RevenueProfitResponse> offlineRevenueProfit = new ArrayList<>();

    List<RevenueProfitResponse> onlineRevenueProfit = new ArrayList<>();

}
