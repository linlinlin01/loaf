package com.xjdl.study.unitTesting.junitJupiter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JunitCaseTest {
	@BeforeEach
	void setUp() {
		log.info("测试开始");
	}

	@AfterEach
	void tearDown() {
		log.info("测试结束");
	}

	@Test
	@Order(3)
	void say() {
		log.info(new JunitCase().say());
	}

	/**
	 * 单线程执行10次
	 * @param testInfo
	 * @param repetitionInfo
	 */
	@Order(1)
	@Execution(ExecutionMode.SAME_THREAD)
	@DisplayName("单线程执行10次")
	@RepeatedTest(value = 10, name = "完成度：{currentRepetition}/{totalRepetitions}")
	void sameThreadTest(TestInfo testInfo, RepetitionInfo repetitionInfo) {
		log.info("测试方法 [{}]，当前第[{}]次，共[{}]次",
				testInfo.getTestMethod().get().getName(),
				repetitionInfo.getCurrentRepetition(),
				repetitionInfo.getTotalRepetitions());
	}

	/**
	 * 多线程执行10次
	 * @param testInfo
	 * @param repetitionInfo
	 */
	@Order(2)
	@Execution(ExecutionMode.CONCURRENT)
	@DisplayName("多线程执行10次")
	@RepeatedTest(value = 10, name = "完成度：{currentRepetition}/{totalRepetitions}")
	void concurrentTest(TestInfo testInfo, RepetitionInfo repetitionInfo) {
		log.info("测试方法 [{}]，当前第[{}]次，共[{}]次",
				testInfo.getTestMethod().get().getName(),
				repetitionInfo.getCurrentRepetition(),
				repetitionInfo.getTotalRepetitions());
	}
}
