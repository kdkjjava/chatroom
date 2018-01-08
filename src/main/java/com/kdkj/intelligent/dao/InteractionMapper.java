package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Interaction;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface InteractionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Interaction record);

    Interaction selectByPrimaryKey(Integer id);

    List<Interaction> selectAll();

    int updateByPrimaryKey(Interaction record);
}