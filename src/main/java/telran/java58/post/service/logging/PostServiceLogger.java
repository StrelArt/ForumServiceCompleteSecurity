package telran.java58.post.service.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "telran.java58.post.service.PostServiceImpl")
@Aspect
public class PostServiceLogger {

    @Pointcut("execution(public * telran.java58.post.service.PostServiceImpl.*(String)) && args(id) ")
    public void findById(String id) {}

    @Pointcut("@annotation(telran.java58.post.service.logging.PostLogger)")
    public void annotatePostLogger() {}

    @Pointcut("execution(public Iterable<telran.java58.post.dto.PostDto> telran.java58.post.service.PostServiceImpl.findPosts*(..))")
    public void bulkFindPostsLogger() {}


    @Before("findById(id)")
    public void logFindById(String id) {
        log.info("find post by id {}", id);
    }

    @AfterReturning("annotatePostLogger()")
    public void logAnnotatePostLogger(JoinPoint joinPoint) {
        log.info("Annotated by PostLogger method: {}, done", joinPoint.getSignature().getName());
    }

    @Around("bulkFindPostsLogger()")
    public Object logBulkFindPostsLogger(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String str) {
                args[i] = str.toLowerCase();
            }
        }
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed(args);
        long end = System.currentTimeMillis();
        log.info("method {}, time: {} ms", joinPoint.getSignature().getName(), end - start);
        return result;

    }




}
