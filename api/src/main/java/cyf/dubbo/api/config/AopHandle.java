package cyf.dubbo.api.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Cheng Yufei
 * @create 2019-02-25 15:52
 **/
@Aspect
@Component
@Slf4j
public class AopHandle {


    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object handle(ProceedingJoinPoint pdj) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info(">>> 开始请求：{}，args:{[]}", request.getRequestURI(), pdj.getArgs());
        Object result = pdj.proceed();
        log.info("<<< 结束请求 , 耗时:{} ms , 结果：{}", stopwatch.elapsed(TimeUnit.MILLISECONDS), Objects.nonNull(result) ? JSONObject.toJSONString(result) : "");
        return result;
    }

//    @AfterReturning(value = "execution(* cyf.dubbo.api.controller.*Controller())")
   /* @AfterReturning("a()")
    public void d(JoinPoint point) {

    }

    @Pointcut("execution(* cyf.dubbo.api.controller.*Controller())")
    public void a() {

    }*/
}
