package com.kdkj.intelligent.service;

import java.util.List;

/**
 * powered by IntelliJ IDEA
 *
 * @Author: unknown
 * @Date: 2018/2/7 16:46
 * @Description:
 **/
public interface MembersService {
    List<String> selectUsernameInGroup(String groupId);

    List<String> selectGroupIdByUsername(String username);
}
