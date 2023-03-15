## MongoDB - 데이터 모델링하는 방법!

## 너무 큰 배열 문제

아래 데이터 기반의 카페 서비스가 있다.

```mongo
db.cafe.insertMany([
    {
        _id: 1,
        name: "IT Community",
        desc: "A Cafe where developer's share information.",
        created_at: ISODate("2018-08-09"),
        last_article: ISODate("2022-06-01T10:56:32.00Z"),
        level: 5,
        members: [
            {
                id: "tom93",
                first_name: "Tom",
                last_name: "Park",
                phone: "000-0000-1234",
                joined_at: ISODate("2018-09-12"),
                job: "DBA"
            },
            {
                id: "asodsa123",
                first_name: "Jenny",
                last_name: "Kim",
                phone: "000-0000-1111",
                joined_at: ISODate("2018-10-02"),
                job: "Frontend Dev"
            },
            {
                id: "candy12",
                first_name: "Zen",
                last_name: "PKo",
                phone: "000-0000-1233",
                joined_at: ISODate("2019-01-12"),
                job: "DA"
            }
        ]
       
    }    
]);
```

여기서 문제는 1개의 카페에 members가 너무 길어질 수 있다는 점이다.

해당 경우 데이터가 커질수록 조회, 생성, 수정(members)가 매우 느려질 수 있다. 추가로 MongoDB는 1개 Document당 16MB를 최대로 하기 때문에 에러가 발생할 수 있다.
- 100,000개의 members를 가진 cafe 기준 13MB정도를 차지한다.
- 많아야 130,000개 이상의 members에서는 더이상 member를 추가할 수 없게 된다.

만약 1개 카페의 members 수의 제한이 있다면, 배열을 사용해서 한 곳에 몰아서 데이터를 보관하는 것이 좋을 수 있다.

하지만 무제한의 members가 들어갈 수 있다는 요구사항이 있다면 배열을 사용할 수 없다.

## 선형적 성능 문제

그럼 위 부분은 어떻게 해결할 수 있을까?

```mongo
db.members.insertMany([
    {
        id: "tom93",
        first_name: "Tom",
        last_name: "Park",
        phone: "000-0000-1234",
        job: "DBA",
        join_cafes: [{
            _id: 1,
            name: "IT Community",
            desc: "A Cafe where developer's share information.",
            created_at: ISODate("2018-08-09"),
            last_article: ISODate("2022-06-01T10:56:32.00Z"),
            level: 5,
            joined_at: ISODate("2018-09-12")
        }, ...]
    },
    ...
])
```

cafe가 members를 가지는 것이 아니라, member가 각각 자신이 속한 cafe를 가지도록 모델링을 할 수 있다.

이 경우 join_cafes의 경우에는 비즈니스 요구사항에 따라 최대 200개~300개 정도만 지원하도록 설계할 수 있기 때문에 충분히 가능하게 된다.

하지만 여전히 존재하는 문제가 있다. 

cafe의 정보를 수정한다고 했을 때는 아래와 같이 members에도 수정사항을 직접 반영해줘야 한다.

```mongo
db.members.updateMany(
    {
        "joined_Cafes._id": 1
    },
    {
        $set: {
            "joined_cafes.$.last_article": date
        }
    }
)
```

이때 모든 members의 join_cafes를 탐색하며 하나씩 전부 수정하기 때문에 선형적으로 작업이 필요하게 되는 문제가 발생한다.



