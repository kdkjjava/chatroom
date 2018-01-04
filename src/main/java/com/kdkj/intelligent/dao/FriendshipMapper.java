package com.kdkj.intelligent.dao;

import com.kdkj.intelligent.entity.Friendship;
import java.util.List;

public interface FriendshipMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Friendship record);

    Friendship selectByPrimaryKey(Long id);

    List<Friendship> selectAll();

    int updateByPrimaryKey(Friendship record);
}