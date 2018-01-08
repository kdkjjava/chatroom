package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.KeyWord;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface KeyWordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(KeyWord record);

    KeyWord selectByPrimaryKey(Integer id);

    List<KeyWord> selectAll();

    int updateByPrimaryKey(KeyWord record);
}