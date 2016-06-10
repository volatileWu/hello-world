package com.hello.world.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hello.world.mapper.OrderMapper;
import com.hello.world.service.AService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommand.Setter;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

import rx.Observable;
import rx.functions.Func1;

@Service
public class AServiceImpl implements AService {

	@Autowired
	private OrderMapper mapper;

	@Override
	public List<String> sayA(final String name) {
		HystrixCommand<List<String>> aCommand = new HystrixCommand<List<String>>(
				Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("aGroup"))
						.andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
								.withExecutionTimeoutInMilliseconds(350).withCircuitBreakerErrorThresholdPercentage(40)
								.withFallbackIsolationSemaphoreMaxConcurrentRequests(500))
						.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
								.withQueueSizeRejectionThreshold(10).withCoreSize(20).withMaxQueueSize(100))) {

			@Override
			protected List<String> run() throws Exception {
				return mapper.find(name);
			}

			@Override
			protected List<String> getFallback() {
				List<String> list = new ArrayList<String>();
				list.add("fall");
				list.add("back");
				return list;
			}
		};
		Observable<String> observable = aCommand.observe().flatMap(new Func1<List<String>, Observable<String>>() {

			@Override
			public Observable<String> call(List<String> t) {
				return Observable.from(t);
			}
		}).map(new Func1<String, String>() {

			@Override
			public String call(String t) {
				System.out.println(Thread.currentThread().getName());
				return t + " test1111";
			}
		});
		try {
			Thread.sleep(520);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long s = System.currentTimeMillis();
		List<String> r = observable.toList().toBlocking().single();
		System.out.println(System.currentTimeMillis() - s);
		return r;
	}

}
