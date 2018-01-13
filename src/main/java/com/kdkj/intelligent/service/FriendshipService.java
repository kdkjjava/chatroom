package com.kdkj.intelligent.service;

import com.kdkj.intelligent.entity.Friendship;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FriendshipService {
    int deleteByPrimaryKey(Long id);

    int insert(Friendship record);

    List<Friendship> selectByAttribute(Friendship record);
    
    Friendship selectByPrimaryKey(Long id);

    List<Friendship> selectAll();

    int updateByPrimaryKey(Friendship record);
}