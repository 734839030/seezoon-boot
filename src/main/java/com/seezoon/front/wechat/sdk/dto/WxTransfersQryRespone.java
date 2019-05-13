package com.seezoon.front.wechat.sdk.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 提现查询
 */
@JacksonXmlRootElement(localName="xml")
public class WxTransfersQryRespone extends WxTransactionRespone{

    private String partner_trade_no	;
    private String status;

    public String getPartner_trade_no() {
        return partner_trade_no;
    }

    public void setPartner_trade_no(String partner_trade_no) {
        this.partner_trade_no = partner_trade_no;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
