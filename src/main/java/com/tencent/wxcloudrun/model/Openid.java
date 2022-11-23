package com.tencent.wxcloudrun.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "openid")
public class Openid {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private String openid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
