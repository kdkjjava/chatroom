package com.kdkj.intelligent.service.impl;

import com.kdkj.intelligent.dao.KeyWordMapper;
import com.kdkj.intelligent.entity.KeyWord;
import com.kdkj.intelligent.service.KeyWordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/11 17:06
 * @Description:
 **/
@Service
public class KeyWordServiceImpl implements KeyWordService{
    @Resource
    private KeyWordMapper keyWordMapper;

    /**
     * 新增一条keyWord的记录
     * @param keyWord
     * @return
     */
    @Override
    public Integer add(KeyWord keyWord){
        return keyWordMapper.insert(keyWord);
    }

    /**
     * 通过masterId修改keyWord的内容
     * @param keyWord
     * @return
     */
    @Override
    public Integer update(KeyWord keyWord) {
        return keyWordMapper.updateByMasterId(keyWord);
    }

    /**
     * 通过masterId获得代理商的单条关键字记录
     * @param masterId
     * @return
     */
    @Override
    public KeyWord getByMaster(Integer masterId) {
        return keyWordMapper.selectByMasterId(masterId);
    }

    @Override
    public Integer insert(KeyWord keyWord) {
        return keyWordMapper.insert(keyWord);
    }

    @Override
    public Integer delete(Integer masterId) {
        return keyWordMapper.deleteByMasterId(masterId);
    }
}
