package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Members;
import java.util.List;

public interface MembersMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Members record);

    Members selectByPrimaryKey(Long id);

    List<Members> selectAll();

    int updateByPrimaryKey(Members record);
}