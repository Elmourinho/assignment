package com.example.assignment.e2e;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;

@Aspect
public class JmsMessageListenerAspect implements Ordered {

	private CountDownLatch countDown;

	public JmsMessageListenerAspect() {
		reinitializeCountDown();
	}

	@Around("@annotation(org.springframework.jms.annotation.JmsListener)")
	void jmsListenerAnnotationAspect(ProceedingJoinPoint joinPoint) throws Throwable {
		joinPoint.proceed();
		countDown.countDown();
	}

	void awaitCompletion() {
		try {
			countDown.await();
			reinitializeCountDown();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException(e);
		}
	}

	private void reinitializeCountDown() {
		countDown = new CountDownLatch(1);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
