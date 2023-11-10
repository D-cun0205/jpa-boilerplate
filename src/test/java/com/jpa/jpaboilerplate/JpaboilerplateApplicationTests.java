package com.jpa.jpaboilerplate;

import com.jpa.jpaboilerplate.dto.MemberDto;
import com.jpa.jpaboilerplate.entity.Member;
import com.jpa.jpaboilerplate.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class JpaboilerplateApplicationTests {
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	EntityManager em;

	@Test
	void testQuery() {
		Member m1 = new Member("AAA", 10);
		Member m2 = new Member("BBB", 20);
		memberRepository.save(m1);
		memberRepository.save(m2);

		List<Member> result = memberRepository.findQueryUser("AAA", 10);
		assertThat(result.get(0)).isEqualTo(m1);
	}

	@Test
	void paging() {
		memberRepository.save(new Member("member1", 10));
		memberRepository.save(new Member("member2", 10));
		memberRepository.save(new Member("member3", 10));
		memberRepository.save(new Member("member4", 10));
		memberRepository.save(new Member("member5", 10));

		int age = 10;
		// paging + sorting + totalCount
		PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

		//when
		Page<Member> page = memberRepository.findByAge(age, pageRequest);
		// 페이지 형태 그대로 API 응답에 사용하는 방법 권장
		Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getAge(), null));

		//then
		List<Member> content = page.getContent();
		long totalElements = page.getTotalElements();

		assertThat(content.size()).isEqualTo(3);
		assertThat(totalElements).isEqualTo(5);
		assertThat(page.getNumber()).isEqualTo(0);
		assertThat(page.getTotalPages()).isEqualTo(2);
		assertThat(page.isFirst()).isTrue();
		assertThat(page.hasNext()).isTrue();

//		List<Member> memberCustom = memberRepository.findMemberCustom();
	}

	@Test
	void auditing_test() throws Exception {
	    //given
	    Member member = new Member("dcun", 33);

	    //when
		memberRepository.save(member);
		em.flush();
		em.clear();
	    
	    //then
		Member findMember = memberRepository.findById(member.getId()).get();
		System.out.println("=======================================================");
		System.out.println("findMember = " + findMember.getCreatedDate());
		System.out.println("findMember = " + findMember.getUpdatedDate());
		System.out.println("=======================================================");

		Thread.sleep(1000);
		findMember.setUsername("si-young");

		em.flush();
		em.clear();

		Member secondFindMember = memberRepository.findById(member.getId()).get();
		System.out.println("=======================================================");
		System.out.println("secondFindMember = " + secondFindMember.getCreatedDate());
		System.out.println("secondFindMember = " + secondFindMember.getUpdatedDate());
		System.out.println("=======================================================");
	}
}
