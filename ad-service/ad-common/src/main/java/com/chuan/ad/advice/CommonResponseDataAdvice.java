package com.chuan.ad.advice;

import com.chuan.ad.annotation.IgnoreResponseAdvice;
import com.chuan.ad.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * ResponseBodyAdvice 接口允许在执行 @ResponseBody 或 ResponseEntity 控制器方法之后，
 * 但在使用 HttpMessageConverter 写入响应体之前自定义响应，进行功能增强。通常用于 加密，签名，统一数据格式等.
 *
 * 统一响应处理
 */

@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 1、选择是否执行 beforeBodyWrite 方法，返回 true 执行，false 不执行.
     * 2、通过 supports 方法，可以选择对哪些类或方法的 Response 进行处理，其余的
     则不处理。

     * @param returnType      返回类型
     * @param converterType   转换器
     * @return   返回 true 则下面的 beforeBodyWrite  执行，否则不执行
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        //标注IgnoreResponseAdvice注解类或方法，返回false
        if (returnType.getDeclaringClass().isAnnotationPresent(IgnoreResponseAdvice.class) ||
                returnType.getMethod().isAnnotationPresent(IgnoreResponseAdvice.class)){
            return false;
        }
        return true;
    }

    /**
     * 对 Response 处理的具体执行方法
     * @param body：响应对象(response)中的响应体
     * @param returnType：控制器方法的返回类型
     * @param selectedContentType：通过内容协商选择的内容类型
     * @param selectedConverterType：选择写入响应的转换器类型
     * @param request：当前请求
     * @param response：当前响应
     * @return ：返回传入的主体或修改过的(可能是新的)主体
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        CommonResponse<Object> commonResponse = new CommonResponse<>(0, "");
        //body为null，说明data为空，直接返回
        if (null == body){
            return commonResponse;
        }
        //如果它已经是一个CommonResponse，我们直接返回
        else if (body instanceof CommonResponse){
            commonResponse = (CommonResponse<Object>)body;
        }
        //包装response在data中
        else {
            commonResponse.setData(body);
        }
        return commonResponse;
    }
}



















































