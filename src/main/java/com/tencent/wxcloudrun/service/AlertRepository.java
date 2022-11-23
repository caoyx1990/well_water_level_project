package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
}
