package com.jordanfulawka.parsewell.dto.applications;

public record ApplicationsInsightsDto(int totalApplications, int applicationsInPastWeek, ApplicationsByStatusDto applicationsByStatus) {
}
