package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickStatsResponse {

    private Long pageReach;

    private Long repeatingCustomers;

    private Long conversionRate;

}
