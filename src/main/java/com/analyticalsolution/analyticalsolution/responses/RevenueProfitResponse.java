/**
 * File: RevenueProfitResponse.java
 * Author: Atharv Mirgal
 * Description: This response class provides a structure for revenue and profit data associated with a specific
 *              date. Commonly used in financial reporting and analysis, it encapsulates date-wise revenue and
 *              profit figures to support detailed insights and reporting requirements. Lombok annotations are
 *              used to auto-generate constructors, getters, and setters, ensuring ease of use and reducing
 *              boilerplate code.
 * Created on: 29/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueProfitResponse {

    private Date date;

    private Long revenue;

    private Long profit;

}
