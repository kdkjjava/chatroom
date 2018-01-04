package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.dao.MembersMapper;
import com.kdkj.intelligent.entity.Members;
import com.kdkj.intelligent.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseServiceImpl implements BaseService{
    @Autowired
    private MembersMapper membersMapper;

    @Override
    public Integer add(Members members) {
        return membersMapper.insert(members);
    }
}
