package com.kdkj.intelligent.service;

import com.kdkj.intelligent.entity.KeyWord;

import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/1/11 17:06
 * @Description:
 **/
public interface KeyWordService {
    Integer add(KeyWord keyWord);
    Integer update(KeyWord keyWord);
    KeyWord getByMaster(Integer masterId);
    Integer insert(KeyWord keyWord);
    Integer delete(Integer masterId);
}
