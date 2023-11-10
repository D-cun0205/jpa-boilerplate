package com.jpa.jpaboilerplate.repository;

import com.jpa.jpaboilerplate.entity.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    // 일반적인 CRUD(수정은 변경 감지 사용 권장), ById 조건 검색까지는 기본적으로 인터페이스에서 제공

    // 관례를 활용한 메서드 이름으로 쿼리 생성
    Member findByUsername(String username);

    // 네임드 쿼리 사용은 엔티티에 적용하는 방법이고 사용을 권장하지 않음

    // @Query 사용, 순수 JPA 에서 사용 했던 JPQL 방식
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findQueryUser(@Param("username") String username, @Param("age") int age);

    // 페이징 + 관례를 활용한 메서드 이름으로 쿼리 생성
    Page<Member> findByAge(int age, Pageable pageable);

    // totalCount 에서 성능 저하가 예상될 때 해결 방법(조인이 많고(쿼리가 복잡한 경우) + 데이터 수 엄청 많은 경우)
    // 카운터 쿼리 분리 방법
    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
    Page<Member> findByAgeSeparatedTotalCount(int age, Pageable pageable);

    // 벌크 연산
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    // 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // Hint
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
