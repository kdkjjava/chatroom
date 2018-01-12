package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.KeyWord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KeyWordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyWord record);

    KeyWord selectByPrimaryKey(Integer id);

    List<KeyWord> selectAll();

    int updateByPrimaryKey(KeyWord record);

    Integer updateByMasterId(KeyWord keyWord);

    KeyWord selectByMasterId(String masterId);

    Integer deleteByMasterId(String masterId);
}