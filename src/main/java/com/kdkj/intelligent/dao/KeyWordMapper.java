package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.KeyWord;
import java.util.List;

public interface KeyWordMapper {
    int deleteByPrimaryKey(Integer 主键);

    int insert(KeyWord record);

    KeyWord selectByPrimaryKey(Integer 主键);

    List<KeyWord> selectAll();

    int updateByPrimaryKey(KeyWord record);
}