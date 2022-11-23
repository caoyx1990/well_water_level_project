package com.tencent.wxcloudrun.service;


import com.tencent.wxcloudrun.model.Openid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenidRepository extends JpaRepository<Openid, Integer> {
    Openid getOpenidById(Integer id);

    Openid getOpenidByOpenid(String openid);

}
