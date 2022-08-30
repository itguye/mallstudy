package com.dudu.mall.service;

import com.dudu.mall.mbg.model.UmsMemberLevel;

import java.util.List;

public interface UmsMemberLevelService {

    /**
     * 获取会员列表
     * @param defaultStatus
     * @return
     */
    List<UmsMemberLevel> list(Integer defaultStatus);
}
