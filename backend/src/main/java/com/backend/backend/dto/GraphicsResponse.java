package com.backend.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GraphicsResponse {
    private boolean success;
    private String disease;
    private String error;
    private List<DrugProducingCountry> drugProducingCountries;
    private List<String> countriesWithDrug;
    private List<YearlyProduction> yearlyProduction;
    private List<PatientsByCountry> patientsByCountry;
    private List<Scientist> scientists;
    private List<RiskFactor> riskFactors;
    private List<SpreadRate> spreadRate;
    private List<DrugPriceInfo> drugPrices;

    // Drug Producing Countries - Bar Chart
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrugProducingCountry {
        private String country;
        private int drugCount;
    }

    // Yearly Production - Line Chart
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YearlyProduction {
        private String year;
        private int production;
    }

    // Patients by Country - Heat Map
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientsByCountry {
        private String country;
        private int patientCount;
    }

    // Scientists - Table
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scientist {
        private String name;
        private String institution;
        private String email;
        private String phone;
        private String country;
    }

    // Risk Factors - Pie Chart
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskFactor {
        private String factor;
        private double percentage;
    }

    // Spread Rate - Area Chart
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpreadRate {
        private String period;
        private int rate;
    }
    
    // Drug Prices - Bar Chart
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DrugPriceInfo {
        private String drugName;
        private double price;
    }
}
