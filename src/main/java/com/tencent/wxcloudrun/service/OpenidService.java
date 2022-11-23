package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.Openid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OpenidService {
    @Autowired
    private OpenidRepository openidRepository;

    public List<String> getAllOpenid() {
        List<Openid> allOpenids = Optional.of(openidRepository.findAll()).orElse(new ArrayList<>());
        return allOpenids.stream().map(Openid::getOpenid).collect(Collectors.toList());
    }

    public Openid getOpenidById(Integer id) {
        return openidRepository.getOpenidById(id);
    }

    public boolean hasOpenid(String openid) {
        Optional<Openid> openidByOpenid = Optional.of(openidRepository.getOpenidByOpenid(openid));
        return openidByOpenid.isPresent();
    }

    public void saveOpenid(String openid) {
        Openid openidObj = new Openid();
        openidObj.setOpenid(openid);
        openidRepository.save(openidObj);
    }
}
