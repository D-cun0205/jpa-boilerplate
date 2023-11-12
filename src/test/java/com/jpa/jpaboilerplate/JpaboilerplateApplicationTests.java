package com.jpa.jpaboilerplate;

import com.jpa.jpaboilerplate.dto.MemberDto;
import com.jpa.jpaboilerplate.dto.QMemberDto;
import com.jpa.jpaboilerplate.dto.UserDto;
import com.jpa.jpaboilerplate.entity.Member;
import com.jpa.jpaboilerplate.entity.QMember;
import com.jpa.jpaboilerplate.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jpa.jpaboilerplate.entity.QMember.member;
import static com.jpa.jpaboilerplate.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaboilerplateApplicationTests {

	@Autowired
	EntityManager em;
	JPAQueryFactory queryFactory;

	@BeforeEach
	void init() {
		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		em.persist(teamA);
		Team teamB = new Team("teamB");
		em.persist(teamB);

		Member memberA = new Member("usernameA", 10, teamA);
		em.persist(memberA);
		Member memberB = new Member("usernameB", 20, teamA);
		em.persist(memberB);
		Member memberC = new Member("usernameC", 30, teamB);
		em.persist(memberC);
		Member memberD = new Member("usernameD", 40, teamB);
		em.persist(memberD);
	}

	/**
	 * 기본 문법
	 */

	@Test
	void testJPQL() throws Exception {
		//given

	    //when
		Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
				.setParameter("username", "usernameA")
				.getSingleResult();

	    //then
		assertThat(findMember.getUsername()).isEqualTo("usernameA");
	}

	@Test
	void testQuerydsl() throws Exception {
	    //given

		//when
		Member findMember = queryFactory
				.select(member)
				.from(member)
				.where(member.username.eq("usernameA"))
				.fetchOne();

	    //then
		assertThat(findMember.getUsername()).isEqualTo("usernameA");
	}

	// .and(), .or()
	// .and() 를 사용하는 경우 ',(콤마)' 로 구분해도 동일하게 작용된다
	@Test
	void testSearch() throws Exception {
	    //given

	    //when
		List<Member> findMembers = queryFactory
				.selectFrom(member)
				.where(member.username.eq("usernameA").or(member.age.eq(20)))
				.fetch();

		//then
		for (Member findMember : findMembers) {
			System.out.println("findMember = " + findMember);
		}
	}

	// 결과 조회
	@Test
	void testResultMethod() throws Exception {
	    queryFactory
				.selectFrom(member)
				.where(member.age.goe(10)) // >=(Greater Or Equal)
				.fetch(); // List
//				.fetchOne(); // Single
//				.fetchCount(); // deprecated, having or groupBy 사용할 때 이슈가 많기 때문
//				.fetchResults(); // deprecated, having or groupBy 사용할 때 이슈가 많기 때문
//				.fetchFirst(); // limit(1) + fetchOne() 과 같음
	}

	// 결과 조회에서 .fetchCount(), .fetchResults() deprecated 이 후 total count 얻는 방법
	@Test
	void testTotalCount() throws Exception {
		Long totalCount = queryFactory
				.select(member.count())
				.from(member)
				.fetchOne();
	}

	// 정렬
	// 1. 회원 나이 내림 차순
	// 2. 회원 이름 올림 차순
	// 2 에서 회원 이름이 없으면 마지막에 출력
	@Test
	void testSort() throws Exception {
	    //given
	    em.persist(new Member(null, 100));
		em.persist(new Member("member5", 100));
		em.persist(new Member("member6", 100));

	    //when
		List<Member> result = queryFactory
				.selectFrom(member)
				.where(member.age.eq(100))
				.orderBy(member.age.desc(), member.username.asc().nullsLast())
				.fetch();

		//then
		Member member5 = result.get(0);
		Member member6 = result.get(1);
		Member memberNull = result.get(2);
		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}

	// 페이징
	@Test
	void paging() throws Exception {
		List<Member> pagingMembers = queryFactory
				.selectFrom(member)
				.orderBy(member.username.desc())
				.offset(1)
				.limit(2)
				.fetch();

		Long totalCount = queryFactory
				.select(member.count())
				.from(member)
				.fetchOne();
	}

	// 집합
	@Test
	void aggregation() throws Exception {
		List<Tuple> result = queryFactory
				.select(
						member.count(),
						member.age.sum(),
						member.age.avg(),
						member.age.max(),
						member.age.min())
				.from(member)
				.fetch();

		Tuple tuple = result.get(0);
		assertThat(tuple.get(member.count())).isEqualTo(4);
		assertThat(tuple.get(member.age.sum())).isEqualTo(100);
		assertThat(tuple.get(member.age.avg())).isEqualTo(25);
		assertThat(tuple.get(member.age.max())).isEqualTo(40);
		assertThat(tuple.get(member.age.min())).isEqualTo(10);
	}

	// groupBy(), groupBy() 해서 having 사용하는 방법은 동일함
	@Test
	void group() throws Exception {
		List<Tuple> result = queryFactory
				.select(team.name, member.age.avg())
				.from(member)
				.join(member.team, team)
				.groupBy(team.name)
				.fetch();

		Tuple teamA = result.get(0);
		Tuple teamB = result.get(1);

		assertThat(teamA.get(team.name)).isEqualTo("teamA");
		assertThat(teamA.get(member.age.avg())).isEqualTo(15);

		assertThat(teamB.get(team.name)).isEqualTo("teamB");
		assertThat(teamB.get(member.age.avg())).isEqualTo(35);
	}

	// 조인
	@Test
	void testJoin() throws Exception {
	    //given

	    //when
		List<Member> members = queryFactory
				.selectFrom(member)
				.join(member.team, team) // .join() == .innerJoin()
//				.leftJoin()
//				.rightJoin()
				.where(team.name.eq("teamA"))
				.fetch();

		//then
		assertThat(members)
				.extracting("username")
				.containsExactly("member1", "member2");
	}

	// 세타 조인(그냥 막 조인)
	// 만약에 사용자 이름이 팀 이름이랑 같은 사람이 있는 경우 조인
	@Test
	void testThetaJoin() throws Exception {
	    //given
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));

	    //when
		List<Member> result = queryFactory
				.select(member)
				.from(member, team)
				.where(member.username.eq(team.name))
				.fetch();

	    //then
		assertThat(result)
				.extracting("username")
				.containsExactly("teamA", "teamB");
	}

	// on 절
	// 참고로 inner join 에 on, where 은 같이 써도 의미가 같음
	@Test
	void testOn() throws Exception {
	    //given
		em.persist(new Member("teamA"));
		em.persist(new Member("teamB"));
		em.persist(new Member("teamC"));

	    //when
		List<Tuple> tuples = queryFactory
				.select(member, team)
				.from(member)
				.leftJoin(team).on(member.username.eq(team.name))
				.where(team.name.eq("teamA"))
				.fetch();
	}

	@PersistenceUnit
	EntityManagerFactory emf;

	// fetch join
	@Test
	void testFetchJoinNotUse() throws Exception {
	    //given
		em.flush();
		em.clear();

	    //when
		Member findMember = queryFactory
				.selectFrom(member)
				.where(member.username.eq("usernameA"))
				.fetchOne();

		//then
		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).isFalse();
	}
	@Test
	void testFetchJoinUse() throws Exception {
		//given
		em.flush();
		em.clear();

		//when
		Member findMember = queryFactory
				.selectFrom(member)
				.join(member.team, team).fetchJoin()
				.where(member.username.eq("usernameA"))
				.fetchOne();

		//then
		boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
		assertThat(loaded).isTrue();
	}

	// sub query, user of max age, user of average age
	// ** from 절의 서브 쿼리는 지원하지 않음(JPA, QueryDSL 둘 다)
		// 쿼리를 2번 분리해서 실행하거나 nativeSQL 사용하여 해결
		// 그리고 쿼리가 많이 복잡해진다면 설계에 문제가 있지 않았나 고민해볼 필요가 있다
		// SQL AntiPatterns 책 추천
	@Test
	void testSubQuery() throws Exception {
	    //when
		QMember memberSub = new QMember("memberSub");
		List<Member> findMembers = queryFactory
				.selectFrom(member)
				.where(member.age.eq(
						JPAExpressions
								.select(memberSub.age.max()) // max age
//								.select(memberSub.age.avg()) // average age
								.from(memberSub)
				))
				.fetch();

		//then
		assertThat(findMembers)
				.extracting("age")
				.containsExactly(40);
	}

	// 아래 case, complex case 에 대해 사용 가능하지만 모두 조회하여 애플리케이션단에서 처리하는 것을 권장
	// case 문
	@Test
	void testCase() throws Exception {
		List<String> result = queryFactory
				.select(member.age
						.when(10).then("열살")
						.when(20).then("스무살")
						.otherwise("기타"))
				.from(member)
				.fetch();
	}
	// complex case 문, CaseBuilder 사용
	@Test
	void testComplexCase() throws Exception {
		List<String> result = queryFactory
				.select(new CaseBuilder()
						.when(member.age.between(0, 20)).then("0~20살")
						.when(member.age.between(21, 30)).then("21~30살")
						.otherwise("기타"))
				.from(member)
				.fetch();
	}

	// 상수
	// 쿼리에서는 상수를 가져오지 않고 가져온 데이터에 상수를 넣어주는 방식
	@Test
	void testConstant() throws Exception {
		List<Tuple> result = queryFactory
				.select(member, Expressions.constant("A"))
				.from(member)
				.fetch();
	}

	// 문자 더하기
	@Test
	void testConcat() throws Exception {
	    //when
		List<String> result = queryFactory
				.select(member.username.concat("_").concat(member.age.stringValue()))
				.from(member)
				.fetch();

		//then
		for (String s : result) {
			System.out.println("s = " + s);
		}
	}

	/**
	 * 중급 문법
	 */

	// 프로젝션, 셀렉트 대상 지정을 의미
		// 대상이 하나
	@Test
	void testSelectOne() throws Exception {
		List<String> result = queryFactory
				.select(member.username)
				.from(member)
				.fetch();
	}
		// 대상이 둘(튜플)
			// QueryDSL 에서 여러 타입을 조회할 때 사용할 수 있도록 만들어놓은 객체
	@Test
	void testSelectMultiple() throws Exception {
		List<Tuple> result = queryFactory
				// 아래 처럼 String, int 이거나 여러 테이블이 포함될 경우
				.select(member.username, member.age)
				.from(member)
				.fetch();

		for (Tuple tuple : result) {
			// Tuple 사용 방법
			String username = tuple.get(member.username);
			Integer age = tuple.get(member.age);
		}
	}

	// 프로젝션 결과를 DTO 로 조회하는 세 가지 방법과 DTO 의 필드 명이 다른 경우(서브 쿼리 포함)
		// 1.프로퍼티 접근 방법(Getter & Setter 사용하여 주입, Projections.bean 사용)
		// 2.필드 접근 방법(필드에 값을 바로 주입, Projections.fields 사용)
		// 3.생성자 접근 방법(생성자 안에 타입 순서가 동일해야함, Projections.constructor 사용)
	@Test
	void testSelectResultToDto() throws Exception {
		List<UserDto> users = queryFactory
//				.select(Projections.bean(UserDto.class, member.username, member.age)) // 필드명이 달라서 이름이 null
//				.select(Projections.bean(UserDto.class, member.username.as("name"), member.age)) 필드명 다를 때 as 사용하는 방법
//				.select(Projections.fields(UserDto.class, member.username, member.age)) // fields 또한 필드명이 달라서 이름이 null
//				.select(Projections.fields(UserDto.class, member.username.as("name"), member.age))
//				.select(Projections.constructor(UserDto.class, member.username, member.age)) // 타입만 같으면 결과가 정상적으로 출력
				.select(Projections.fields(UserDto.class, // 서브 쿼리의 결과에 별칭 사용하는 방법
						member.username.as("name"),
						Expressions.as(JPAExpressions
								.select(member.age.max())
								.from(member), "age")
				))
				.from(member)
				.fetch();
	}

	// @QueryProjection
	// 위 언급한 DTO 반환 방법의 단점으로 없는 필드를 추가해도 컴파일 단계에서 에러를 확인하지 못하고 런타임에 사용자에 의해 에러가 발생한다
	// 이 문제를 해결하기 위한 방법인데 아키텍처 관점으로 볼 때 Dto 는 클라이언트에게 제공될 수 있는 객체이고 JPA 와 의존 되어 있으면 좋지 않다
	// DTO 클래스안에 JPA 와 연관된 어노테이션이 생성자에 붙는다 이에 대한 트레이드오프를 고민하고 위 세 가지 방법 중 택해서 사용한다
	@Test
	void testQueryProjectionAnnotation() throws Exception {
		List<MemberDto> result = queryFactory
				.select(new QMemberDto(member.username, member.age))
				.from(member)
				.fetch();
	}

	// 동적 쿼리, BooleanBuilder 사용(실무에서 사용 권장)
	@Test
	void testBooleanBuilder() throws Exception {
		String username = "usernameA";
		int age = 10;
		List<Member> members = searchMembers(username, age);
		for (Member m : members) {
			System.out.println("m = " + m);
		}
	}

	private List<Member> searchMembers(String usernameCond, Integer ageCond) {
		BooleanBuilder builder = new BooleanBuilder();
		if (usernameCond != null) {
			builder.and(member.username.eq(usernameCond));
		}

		if (ageCond != null) {
			builder.and(member.age.eq(ageCond));
		}

		return queryFactory
				.selectFrom(member)
				.where(builder)
				.fetch();
	}

	// 동적 쿼리, 다중 파라미터(실무에서 사용 권장)
	@Test
	void testDynamicQuery() throws Exception {
		String username = "usernameA";
		int age = 10;

		List<Member> members = queryFactory
				.selectFrom(member)
//				.where(usernameEq(username), ageEq(age)) // predicate 사용하여 각 각의 메서드 표현
				.where(AllEq(username, age)) // BooleanExpression 사용하여 조합해서 한 번에 표현
				.fetch();

		for (Member member : members) {
			System.out.println("member = " + member);
		}
	}

	private Predicate usernameEq(String username) {
		return username != null ? member.username.eq(username) : null;
	}

	private Predicate ageEq(Integer age) {
		return age != null ? member.age.eq(age) : null;
	}

	// 메서드를 조합해서 사용하는 방법
	private BooleanExpression AllEq(String username, Integer age) {
		return usernameEq2(username).and(ageEq2(age));
	}

	private BooleanExpression usernameEq2(String username) {
		return username != null ? member.username.eq(username) : null;
	}

	private BooleanExpression ageEq2(Integer age) {
		return age != null ? member.age.eq(age) : null;
	}

	// 수정, 삭제 벌크
	// 벌크 연산은 영속성 컨텍스트에 영향을 주지 않는다
	// 벌크 연산 이 후 비즈니스 로직이 필요한 경우는 영속성 컨텍스트를 모두 비우고 필요한 엔티티를 다시 조회한 후 로직을 적용해야된다
	// 기왕이면 벌크 연산 이 후 비즈니스 로직을 가지지 않은 방법을 권장
	@Test
	void testBulk() throws Exception {
		long 수정_영향_받은_행_수 = queryFactory
				.update(member)
//				.set(member.username, "비회원") // 문자열 변경
				.set(member.age, member.age.add(1)) // 숫자, -숫자 변경
				.where(member.age.lt(28))
				.execute();

		long 삭제_영향_받은_행_수 = queryFactory
				.delete(member)
				.where(member.age.gt(18))
				.execute();

		em.flush();
		em.clear();

		// 벌크 연산 후 비즈니스 로직이 필요하면 꼭 영속성 컨텍스트를 비운 후 적용
	}

	// SQL Function, 데이터베이스에서 필요한 함수를 만들어 놓고 필요할 때 마다 사용하는 방법
	// 우선 ANSI SQL Function(모든 DB 에서 기본적으로 제공하는 function)은 QueryDSL 에서도 지원
	// 추가적인 기능이 필요한 경우 'DB명'+Dilect 를 상속받고 추가해야한다
	@Test
	void testSQLFunction() throws Exception {
		List<String> result = queryFactory
				.select(Expressions.stringTemplate(
						"function('lower', {0})", member.username)) // where 절에서도 사용 가능
//				.select(member.username.lower()) // QueryDSL 에서 지원하는 함수
				.from(member)
				.fetch();

		for (String s : result) {
			System.out.println("s = " + s);
		}
	}
}
