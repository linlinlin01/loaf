package com.xjdl.study.stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
class User {
	private String name;
	private int age;
	private int salary;
}

/**
 * stream流操作
 *
 * Lambda表达式的使用条件
 * 首先，都是接口
 * 其次，接口中有且只有一个接口，才可以使用lambda表达式
 * 1.接口中只有一个抽象方法的接口，叫做函数式接口
 * 2.如果是函数式接口，那么就可以用@FunctionalInterface注解标识
 */
@Slf4j
public class StreamDemo {
	public static void main(String[] args) {
		List<User> list = Arrays.asList(
				new User("zhangsan", 23, 2000),
				new User("lisi", 43, 22000),
				new User("goushdan", 54, 12000)
		);
		test1(list);
		test2(list);
		test6(list);

		List<Integer> list1 = Arrays.asList(1, 4, 7, 3, 9);
		test3(list1);
		test4(list1);
		test5(list1);

		test7(list, list1);

		List<String> sentences = Arrays.asList("hello world", "aaa bbbb cccc dddd");
		test8(sentences);
		test9(sentences);
	}

	/**
	 * 使用peek【中间方法】进行遍历处理
	 */
	public static void test9(List<String> sentences) {
		// 演示点1： 仅peek操作，最终不会执行
		log.info("----before peek----");
		sentences.stream().peek(item -> log.info("{}", item));
		log.info("----after peek----");
		// 演示点2： 仅foreach操作，最终会执行
		log.info("----before foreach----");
		sentences.forEach(item -> log.info("{}", item));
		log.info("----after foreach----");
		// 演示点3：peek操作后面增加终止操作，peek会执行
		log.info("----before peek and count----");
		sentences.stream().peek(item -> log.info("{}", item)).count();
		log.info("----after peek and count----");
	}

	/**
	 * flatMap 每个元素返回一个或者多个元素
	 * <p>
	 * flatMap操作的时候其实是先每个元素处理并返回一个新的Stream，然后将多个Stream展开合并为了一个完整的新的Stream
	 */
	private static void test8(List<String> sentences) {
		// 使用流操作
		List<String> results = sentences.stream()
				.flatMap(sentence -> Arrays.stream(sentence.split(" ")))
				.collect(Collectors.toList());
		log.info("{}", results);
	}

	/**
	 * 合并
	 *
	 * @param list
	 * @param list1
	 */
	private static void test7(List<User> list, List<Integer> list1) {
		Stream.of(list, list1)
				.forEach(item -> log.info("{}", item));

	}

	/**
	 * 跳过前几个元素
	 *
	 * @param list
	 */
	private static void test6(List<User> list) {
		List<User> collect = list.stream()
				.skip(2)
				.collect(Collectors.toList());
		log.info("{}", collect);
	}

	/**
	 * map
	 * 每个元素返回一个结果
	 *
	 * @param list1
	 */
	private static void test5(List<Integer> list1) {
		list1.stream()
				.map(item -> item + 12)
				.forEach(item -> log.info("{}", item));
	}

	/**
	 * reduce
	 * 所有结果进行合并操作
	 *
	 * @param list1
	 */
	private static void test4(List<Integer> list1) {
		Integer integer = list1.stream()
//				.reduce((a, b) -> a + b)
				.reduce(Integer::sum)
				.get();
		log.info("{}", integer);
	}

	/**
	 * 长度，极值，统计
	 *
	 * @param list1
	 */
	private static void test3(List<Integer> list1) {
		Integer integer = list1.stream()
				.max(Integer::compareTo)
				.get();
		long count = list1.stream().count();
		IntSummaryStatistics intSummaryStatistics = list1.stream()
				.mapToInt(Integer::intValue)
				.summaryStatistics();

		log.info("{}", intSummaryStatistics.getCount());
		log.info("{}", intSummaryStatistics.getAverage());
		log.info("{}", intSummaryStatistics.getMax());
		log.info("{}", intSummaryStatistics.getMin());
		log.info("{}", intSummaryStatistics.getSum());
		log.info("{}", count);
		log.info("{}", integer);
	}

	/**
	 * 截取
	 *
	 * @param list
	 */
	private static void test2(List<User> list) {
		List<User> collect = list.stream()
				.limit(2)
				.collect(Collectors.toList());
		log.info("{}", collect);
	}

	/**
	 * 过滤
	 *
	 * @param list
	 */
	private static void test1(List<User> list) {
		Predicate<User> predicate1 = user -> user.getAge() < 50;
		Predicate<User> predicate2 = user -> user.getSalary() > 3000;

		List<User> collect = list.stream()
				.filter(predicate1.and(predicate2))
				.collect(Collectors.toList());
		log.info("{}", collect);
	}
}