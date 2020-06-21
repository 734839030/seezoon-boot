package com.seezoon.boot.common.web;

import com.seezoon.boot.common.utils.DateUtils;
import com.seezoon.boot.context.dto.ResponeModel;
import com.seezoon.boot.context.exception.ExceptionCode;
import com.seezoon.boot.context.exception.ResponeException;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * 参数的统一校验处理
 *
 * @author hdf
 * @date 2020/5/30 10:08 下午
 */
@RestController
@ControllerAdvice
public class ParamsControllerAdvice {

    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(ParamsControllerAdvice.class);

    /**
     * 初始化数据绑定 1. 将所有传递进来的String进行HTML编码，防止XSS攻击 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }

            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponeModel missingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponeModel.error(ExceptionCode.PARAM_INVALID, e.getMessage());

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponeModel constraintViolationException(ConstraintViolationException e) {
        return ResponeModel.error(ExceptionCode.PARAM_INVALID, e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    public ResponeModel bindException(BindException e) {
        StringBuilder sb = new StringBuilder();
        e.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            sb.append(fieldName).append(":").append(errorMessage).append(";");
        });
        ResponeModel responeModel = ResponeModel.error(ExceptionCode.PARAM_BIND_ERROR, sb.toString());
        return responeModel;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponeModel methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            sb.append(fieldName).append(":").append(errorMessage).append(";");
        });
        return ResponeModel.error(ExceptionCode.PARAM_INVALID, sb.toString());
    }

    @ResponseBody
    @ExceptionHandler(ResponeException.class)
    public ResponeModel responeException(ResponeException e) {
        ResponeModel responeModel = ResponeModel.error(e.getResponeCode(), e.getResponeMsg());
        return responeModel;
    }

    /**
     * 可以细化异常，spring 从小异常抓，抓到就不往后走
     *
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponeModel exception(Exception e) {
        logger.error("global exception ", e);
        ResponeModel responeModel = ResponeModel.error(ExceptionCode.UNKNOWN, e.getMessage());
        return responeModel;
    }
}
