package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public int getAlertNum() {
        List<Alert> alerts = alertRepository.findAll();
        if (alerts != null && !alerts.isEmpty()) {
            return alerts.get(0).getNumber();
        }
        return 5;
    }
}
