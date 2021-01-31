package com.cykj.pos.controller.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cykj.common.core.domain.AjaxResult;
import com.cykj.common.core.domain.entity.SysUser;
import com.cykj.common.core.domain.model.LoginUser;
import com.cykj.common.utils.SecurityUtils;
import com.cykj.pos.domain.BizMerchant;
import com.cykj.pos.domain.BizMicroInfo;
import com.cykj.pos.enums.bizstatus.BizStatusContantEnum;
import com.cykj.pos.profit.dto.*;
import com.cykj.pos.service.IBizMerchantService;
import com.cykj.pos.service.IBizMicroInfoService;
import com.cykj.pos.service.IBizVerifyCodeService;
import com.cykj.pos.util.LoginUserUtils;
import com.cykj.system.service.ISysUserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pos/api/v2/merchant")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PosV2AppMerchantController {

    private final IBizMerchantService merchantService;

    private final IBizVerifyCodeService verifyCodeService;

    private final ISysUserService sysUserService;

    private final IBizMicroInfoService microInfoService;

    @ApiOperation(value="获取商户及子商户列表")
    @ApiResponses({@ApiResponse(code=200,response = BizMerchant.class,message = "业务数据响应成功")})
    @PostMapping("/list")
    public AjaxResult queryTerminalList(@RequestBody MerchantDTO merchantDTO){
        AjaxResult ajaxResult = AjaxResult.success();
        Long userId = LoginUserUtils.getLoginUserId();
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);
        List<BizMerchant> merchantList = merchantService.getAllSubNodeAndIncludeSelf(merchant.getMerchId());
        ajaxResult.put("data",merchantList);
        return  ajaxResult;
    }
    @ApiOperation(value="获取当前商户信息")
    @ApiResponses({@ApiResponse(code=200,response = BizMicroInfo.class,message = "业务数据响应成功")})
    @PostMapping("/current")
    public AjaxResult getCurrentMerchat(@RequestBody BizMerchant merchantBrief){
        AjaxResult ajaxResult = AjaxResult.success();
        Long userId = LoginUserUtils.getLoginUserId();
        BizMerchant merchant = merchantService.getMerchantByUserId(userId);

        LambdaQueryWrapper<BizMicroInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(BizMicroInfo::getMerchId ,merchant.getMerchId());
        BizMicroInfo microInfo =  microInfoService.getOne(lqw);
        Map<String,Object> merchanInfo = new HashMap<>();
        merchanInfo.put("merchantBrief",merchant);
        merchanInfo.put("merchantDetail",microInfo);
        ajaxResult.put("data",merchanInfo);
        return  ajaxResult;
    }
    @ApiOperation(value="获取直接子商户字典")
    @ApiResponses({@ApiResponse(code=200,response = MerchantDict.class,message = "业务数据响应成功")})
    @PostMapping("/direct/child/dict")
    public AjaxResult getChildMerchantDictList(@RequestBody MerchantDTO merchantDTO){
        AjaxResult ajaxResult = AjaxResult.success();
        Long userId = LoginUserUtils.getLoginUserId();
        List<MerchantDict> dicts = merchantService.getChildMerchartDictList(userId);
        ajaxResult.put("data",dicts);
        return ajaxResult;
    }

    @ApiOperation(value="获取我的伙伴列表")
    @ApiImplicitParams({@ApiImplicitParam(name="nickName",value = "用户姓名",dataType = "string",required = false,paramType="body"),
            @ApiImplicitParam(name="verifyStatus",value = "商户报件状态，0-报件审核中，1-报件成功，2-异常",dataType = "string",required = false,paramType="body"),
            @ApiImplicitParam(name="monthType",value = "数据月份，'this'-本月，'last'-上月",dataType = "string",required = false,paramType="body"),
            @ApiImplicitParam(name="pageNo",value = "当前页号，默认-1",dataType = "int",required = true,paramType="body"),
            @ApiImplicitParam(name="pageSize",value = "当前页大小，默认-1",dataType = "int",required = true,paramType="body")})
    @ApiResponses({@ApiResponse(code=200,response = PartnerDTO.class,message = "业务数据响应成功")})
    @PostMapping("/mypartner/list")
    public AjaxResult getPartnerList(@RequestBody MerchantDTO merchantDTO){
        AjaxResult ajaxResult = AjaxResult.success();
        merchantDTO.setUserId(LoginUserUtils.getLoginUserId());
        ajaxResult.put("data",merchantService.getPagedPartnerList(merchantDTO));
        ajaxResult.put("partnerCounts",merchantService.getChildMerchantCounts(merchantDTO.getUserId(),merchantDTO.getVerifyStatus(),"",merchantDTO.getNickName()));
        return ajaxResult;
    }

    @ApiOperation(value="获取我的伙伴详情")
    @ApiResponses({@ApiResponse(code=200,response = PartnerDTO.class,message = "业务数据响应成功")})
    @PostMapping("/mypartner/detail")
    public AjaxResult getPartnerDetail(@RequestBody MerchantDTO merchantDTO){
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("data",merchantService.getPartnerDetail(LoginUserUtils.getLoginUserId()));
        return ajaxResult;
    }

    @ApiOperation(value="商户入驻")
    @ApiResponses({@ApiResponse(code=200,response = MicroMerchantDTO.class,message = "商户入驻成功")})
    @PostMapping("/register")
    public AjaxResult merchantRegister(@RequestBody MicroMerchantDTO microMerchantDTO){
        AjaxResult ajaxResult = AjaxResult.success("商户入驻成功");
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String mobile = sysUser.getPhonenumber();
        String verifyCode = microMerchantDTO.getVerifyCode();
        BizStatusContantEnum bizStatus = verifyCodeService.verifyCodeValidate(mobile,verifyCode);
        if(bizStatus != BizStatusContantEnum.SMS_SUCCESS){
            return AjaxResult.error(bizStatus.getName());
        }
        microMerchantDTO.setUserId(sysUser.getUserId());
        merchantService.microMerchantRegister(microMerchantDTO);
        return ajaxResult;
    }

    @ApiOperation(value="登录密码设置")
    @ApiImplicitParams({@ApiImplicitParam(name="password",value = "登录密码密文",dataType = "string",required = true,paramType="body")})
    @ApiResponses({@ApiResponse(code=200,response = PartnerInviteDTO.class,message = "登录密码重置成功")})
    @PostMapping("/password/reset")
    public AjaxResult resetLoginPassword(@RequestBody PartnerInviteDTO partnerInviteDTO){
        AjaxResult ajaxResult = AjaxResult.success("登录密码重置成功");
        SysUser sysUser = SecurityUtils.getLoginUser().getUser();
        String mobile = sysUser.getPhonenumber();
        String verifyCode = partnerInviteDTO.getVerifyCode();
        BizStatusContantEnum bizStatus = verifyCodeService.verifyCodeValidate(mobile,verifyCode);
        if(bizStatus != BizStatusContantEnum.SMS_SUCCESS){
            return AjaxResult.error(bizStatus.getName());
        }
        //密码密文处理，前端算法解密-新算法加密-保存
        String password = SecurityUtils.encryptPassword(partnerInviteDTO.getPassword());
        sysUserService.resetUserPwd(sysUser.getUserName(),password);
        return ajaxResult;
    }
}
