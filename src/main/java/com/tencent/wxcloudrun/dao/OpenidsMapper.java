package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Openid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OpenidsMapper {

  Openid getOpenId(@Param("id") Integer id);

  List<Openid> getOpenIds();

  Openid insertOpenId(Openid openid);
}
