package com.wcj.gulimall.product.exception;

import com.wcj.common.exception.BizCodeEnum;
import com.wcj.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理所有异常
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.wcj.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException exception) {
        log.error("数据校验出现问题{},异常类型{}", exception.getMessage(), exception.getClass());

        BindingResult bindingResult = exception.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getCode(),
                        BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getMsg())
                .put("data", errorMap);
    }

    // 处理其他异常
//    @ExceptionHandler(value = Throwable.class)
    public R handleException() {

        return R.error(BizCodeEnum.UNKONW_EXCEPITON.getCode(),
                BizCodeEnum.UNKONW_EXCEPITON.getMsg());
    }
}
