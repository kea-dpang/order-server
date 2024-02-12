package kea.dpang.order.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class ExecutionTimeLoggingAspect {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Pointcut("within(kea.dpang.order.controller..*)")
    fun controllerPackagePointcut() {
        // 이 함수는 "kea.dpang.order.controller" 패키지 내의 모든 메소드를 대상으로 Pointcut을 정의한다.
        // 함수 본체가 비어있는 이유는 이 함수F 자체는 실행되지 않고,
        // @Pointcut 어노테이션의 값이 AspectJ 표현식을 해석하는 데 사용되기 때문이다.
    }

    @Around("controllerPackagePointcut()")
    fun around(point: ProceedingJoinPoint): Any? {
        // 시작 시간
        val start = System.currentTimeMillis()

        // 메서드 실행
        val proceed = point.proceed()

        // 종료 시간
        val executionTime = System.currentTimeMillis() - start

        log.info("${point.signature} 실행시간: $executionTime ms")

        return proceed
    }
}
