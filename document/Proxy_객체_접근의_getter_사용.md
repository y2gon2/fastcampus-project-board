해당 문서는 Spring 에서 DB 에 접근 및 data 영속성을 부여하는 과정에서 사용되는 Spring 의 각 용어의 개념을 다시 정리해보고, 제목에 명시된 Proxy 객체에 직접 접근할 때 발생 가능한 문제와 이에 대한 해결 방안을 정리한다. 

## 1. 용어 개념 정리

### JPA (Java Persistence API)
* Java application 에서 관계형 DB 를 사용하여 Data 를 유지 관리 작업에 필요한 API specification.
* JPA 의 spec. 을 구현하여 Spring application 내에서 Data 접근을 추가항화 하고, Data 저장소화의 상호작용을 단순화 할 수 있도록 해준다.

### Hibernate
* JPA 의 구현체
* Java application 과 DB 사이에 ORM(Object-Relational Mapping) 을 실제적으로 제공한다.

### Entity
* DB table 과 1:1 로 mapping 되는 Java Class

#### => Entity 와 JPA 의 구현체인 Hibernate 를 사용함에 따라, SQL 과 DB migration 작업 없이도 순수 Java application 내애서 DB CRUD 작업이 가능하게 만들어줌. 

## 2. Proxy 객체 및 이에 대한 접근

### Proxy 객체와 Lazy Loading
* Hibernate 는 성능 최적화를 위해 proxy 객체를 사용하여 Lazy Loading 을 구현한다.
* 아래 예제와 같이, 만약 User Entity 가 Order Entity 와 연관 관계가 있다면,

```java
@Entity
public class User {
    @Id
    private Long id;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;

    public Long getId() {
        return id;
    }

    public List<Order> getOrders() {
        return orders;
    }
}
```
* User entity 작업 요청이 있을 때마다, Data 가 큰 orders list 를 모두 매번 가져오는 것은 비효율 적일 수 있다.
* 따라서 Hibernate 는 Orders field 를 바로 load 하지 않고, orders field 참조 객체만 생성한다.
* 해당 reference 객체가 proxy 객체이며, 이렇게 proxy 객체만 가지고 있다가 실제 data 가 필요할 때, 가져오는 것이 lazy loading 이다.

## 3. Proxy 객체에 직접 접근의 문제 
* 위 예제에서 만약 아래와 같이 User Entity 를 생성하고 orders Field 에 직접 접근하고자 한다면, Hibernate가 proxy 객체만 생성해 놓은 field 에 직접 접근을 시도했기 때문에, 해당 field 는 초기화 되지 않고 null 이거나 예기치 않은 동작을 일으 킬 수 있다. 
```java
        User user = entityManager.find(User.class, userId);

        List<Order> orders = user.orders;  // 이런 방식은 좋지 않습니다.
```
* 이 문제를 해결하기 위해 getter method 를 사용해야 한다. 
```java
        List<Order> orders = user.getOrders();  // 이것이 올바른 방식입니다.
```
* getter method 를 사용하면 Hibernate 는 proxy 객체를 올바르게 처리하고 필요한 시점에 실제 'Order' 객체를 DB 로부터 load 하게 된다. 