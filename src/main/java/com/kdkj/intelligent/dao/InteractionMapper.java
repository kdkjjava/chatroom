package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Interaction;
import java.util.List;

public interface InteractionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Interaction record);

    Interaction selectByPrimaryKey(Integer id);

    List<Interaction> selectAll();

    int updateByPrimaryKey(Interaction record);
}