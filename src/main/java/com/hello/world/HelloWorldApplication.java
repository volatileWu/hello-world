package com.hello.world;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.hello.world.service.AService;

@SpringBootApplication
public class HelloWorldApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(HelloWorldApplication.class, args);
		final AService aService = applicationContext.getBean(AService.class);
		ExecutorService threadPool = Executors.newFixedThreadPool(2);
		final CountDownLatch l = new CountDownLatch(10);
		for (int i = 0; i < 10; i++) {
			threadPool.execute(new Runnable() {

				@Override
				public void run() {
					List<String> sayA = aService.sayA("t%");
					for (String a : sayA) {
						System.out.println(a + Thread.currentThread().getName());
					}
					l.countDown();
				}
			});
		}
		l.await();
		threadPool.shutdown();
	}
}
