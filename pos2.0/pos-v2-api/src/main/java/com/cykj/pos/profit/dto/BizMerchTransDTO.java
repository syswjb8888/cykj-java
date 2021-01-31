package com.cykj.pos.profit.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.cykj.common.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BizMerchTransDTO {

    /** 用户商编 */
    private String merchantId;

    /** 外部订单号 */
    private String orderId;

    /** 交易金额（元） */
    private String amt;

    /** 交易编号 */
    private String idTxn;

    /** 产品码 */
    private String productCode;

    /** 银行授权码 */
    private String authCode;

    /** 优惠费率标识 */
    private String discountRateFlag;

    /** 交易状态 */
    private String status;

    /** 错误码 */
    private String errorCode;

    /** 错误描述 */
    private String errorMsg;

    /** 交易请求时间 */
    private String txnTime;

    /** 输入模式 */
    private String serviceEntryMode;

    /** 卡类型 */
    private String cardType;

    /** 设备SN号 */
    private String snCode;

    /** 设备类型 */
    private String deviceType;

    /** 商户实名姓名 */
    private String userName;

    /** 商户手机号 */
    private String phoneNo;

    /** 店面收银交易标识 */
    private String isStoreCachier;

    /** 产品名称 */
    private String receiptType;

    /** S0手续费套餐生效标识 */
    private String S0PackageFlag;

    /** 二级机构编号 */
    private String secondOrgID;

    /** 直属机构编号 */
    private String directlyOrgID;

    /** 一机双模标识 */
    private String specialMode;

    /** e-sim卡服务费标识 */
    private String esimFlg;

    /** 用户实名身份证号 */
    private String idCardNo;

    /** 交易上送商户编号 */
    private String authMerchantId;

    /** 交易上送终端编号 */
    private String authTerminalId;
}
