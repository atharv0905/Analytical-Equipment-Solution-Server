/**
 * File: DateRange.java
 * Author: Atharv Mirgal
 * Description: This response class represents a date range with a start date and an end date, encapsulating
 *              date values for filtering or querying purposes. It is commonly used for specifying date ranges
 *              in request and response payloads. The class is annotated with Lombok annotations to automatically
 *              generate boilerplate code such as getters, setters, constructors, and a no-argument constructor
 *              for easier initialization and usage.
 * Created on: 29/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRange {
    private Date startDate;
    private Date endDate;
}
