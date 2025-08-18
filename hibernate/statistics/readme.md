## Statistics

`Hibernate`에서는 `StatisticsImplementor`를 제공한다. 이를 활용하면 insert, delete, fetch 등이 몇번 발생했는 지나 캐시 사용 유무 등을 확인할 수 있다.

이러한 데이터는 콜백을 통해 수집되고, 이를 활용해서 테스트로 N+1이 발생하지 않는 것, 1차 캐싱을 사용하는 것 등 다양한 것을 검증할 수 있다.

이러한 기능은 오버헤드를 유발하기에 프로퍼티(`hibernate.generate_statistics`) 설정을 통해 제어되고, 기본 값은 false다.




