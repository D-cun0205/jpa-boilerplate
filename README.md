### Spring Data JPA 사용할 때 쿼리 호출하는 방법

```
1.(가장 먼저 사용 고려)단순한 쿼리 호출인 경우 파라미터가 1~2개 정도 들어갈 때 메서드 이름으로 쿼리 생성 사용
2.(사용 권장 안함)엔티티에 @NamedQuery 사용해서 쿼리 작성하는 방법
3.(1번에서 한계가 있을 때 사용)레파지토리 메서드에 쿼리 정의하기 
4.페이징 사용 방법, MemberRepository, 테스트 클래스 내용 참조
```

### 벌크성 수정 쿼리

```
순수 JPA 일반 쿼리(update + .executeUpdate()), @Query + @Param + @Modifying 사용하여 업데이트
일반적으로 벌크 연산을 수행한 후 동일 트랜잭션에서 영속성 컨텍스트를 비우지 않고 그대로 사용하게 되면
영속성 컨텍스트는 벌크 연산에 대해 내용을 알지 못하므로 데이터에 큰 오류가 생길 수 있고 이를 방지하기 위해
@Modifying(clearAutomatically = true) 속성 값을 세팅하여 영속성을 비울 수 있다
```

### 엔티티 그래프

```
패치 조인을 편리하게 하는 방법 @EntityGraph(속성 값 입력)
```

### JPA Hint & Lock

```
Hint: JPA 구현체(하이버네이트)에게 제공하는 힌트, SQL 아님
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
예를 들어 수정에 대한 메서드가 있을 때 수정 메커니즘은 원본(스냅샷)과 변경사항이 있어서 이를 확인하고 변경 감지로
데이터를 수정하게 되는데 Hint 를 적용하면 스냅샷을 생성하지 않고 수정 절차를 이어가며 성능에 약간의 도움이 될 수 있다
그러나 남발 금지 이 옵션을 모든 수정 메서드에 적용한다고 성능에 대해 많은 이득을 취하지 않기 때문에
정말 서비스가 크거나 트래픽이 상당할 경우 이를 고려하고 사실 서비스가 많이 커지면 이런 옵션으로 이득을 보기 전에
앞 단에 캐시 관련 프로세스를 추가해서 해결하는게 더 이점이 크다

Lock: 조회한 데이터에 대해서 수정 못하도록 막는 설정
```

### 사용자 정의 repository

```
주의 사항으로 커스텀용 repository 의 구현체 클래스 이름은 엔티티 repository 인터페이스 이름과  같고 + Impl 붙어야한다
ex) MemberRepository(엔티티 repository) 인 경우 커스텀은 MemberRepositoryImpl 이 되어야 한다 (관례)
물론 Impl 을 적용안하는 방법으로 xml, Java config 설정이 있긴하다
근데 복잡한 쿼리는 JPQL or Query DSL 을 사용하던 코드가 상당히 길어지기 때문에 굳이 사용자 정의 Repository 구현보다
일반적으로 사용하는 엔티티 repository 하나, JPQL or Query DSL repository 하나 를 만들어서
서비스단에서 둘 다 호출해서 사용하는게 유지보수에도 더 좋다 
```

### Auditing

```
@EnableJpaAuditing 어노테이션을 Spring Boot Main 클래스에 적용
BaseEntity 클래스를 적용하고 싶은 엔티티에 상속 설정
BaseEntity 클래스에 설정 값 확인
날짜는 LocalDate, LocalDateTime 을 사용하면 되고
사용자는 AuditorAware 를 구현하는 클래스에 HTTP 요청으로 받은 사용자 이름을 설정할 수 있다
```

### 도메인 클래스 컨버터

```
컨트롤러에서 @PathVariable 어노테이션을 사용하여 Long 타입의 엔티티 식별 값을 받는 경우 아래 와 같이 사용 가능
@PathVariable("id") Member member -> @PathVariable("id") Member member
이는 조회용으로만 사용해야되며 수정하거나 별다른 비지니스로직이 포함될 경우 깊게 알고 있지 않은 상태에서
사용할 때 추적하기 어려운 여러 에러사항이 발생할 수 있다
```

### Pageable

```
page 는 기본적으로 0 부터 시작한다
application.yml 파일에 spring.data.web.pageable 설정으로 미니멈, 멕시멈, 제한 가능
spring.data.web.pageable.one-indexed-parameters 로 시작을 1로 변경할 수 있는데
Pageable 객체에는 실제로 적용되지 않아 혼동을 불러일으킬 수 있으니 사용하지 않은 것을 권장
```

### JPA Log 확인용 설정

```
P6SpySqlFormatter 클래스 확인
```