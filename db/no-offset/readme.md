# No Offset 방식 추가 정리

explain (select id
from employee
order by id desc
limit 10
offset 1000000)

![img_1.png](img_1.png)

![img_2.png](img_2.png)

![img_3.png](img_3.png)

![img_4.png](img_4.png)