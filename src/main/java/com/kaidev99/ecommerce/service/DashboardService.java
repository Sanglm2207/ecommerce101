package com.kaidev99.ecommerce.service;

import com.kaidev99.ecommerce.dto.DashboardStatsDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashboardStatsDTO getDashboardStats(LocalDate startDate, LocalDate endDate);
}
