package com.dudu.mall.controller;

import com.dudu.mall.common.api.CommonResult;
import com.dudu.mall.mbg.model.UmsMemberLevel;
import com.dudu.mall.service.UmsMemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/memberLevel")
@Api(tags = "UmsMemberLevelController", description = "会员等级管理")
public class UmsMemberLevelController {
    @Resource
    private UmsMemberLevelService umsMemberLevelService;

    @ApiOperation("查询所有会员信息")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public CommonResult<List<UmsMemberLevel>> list(@RequestParam("defaultStatus") Integer defaultStatus) {
        List<UmsMemberLevel> memberLevelList = umsMemberLevelService.list(defaultStatus);
        return CommonResult.success(memberLevelList);
    }
}
