package com.seezoon.front.wechat.sdk.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * 提现响应
 */
@JacksonXmlRootElement(localName="xml")
public class WxTransfersRespone extends WxTransactionRespone{

    private String payment_no;

    public String getPayment_no() {
        return payment_no;
    }

    public void setPayment_no(String payment_no) {
        this.payment_no = payment_no;
    }
}
