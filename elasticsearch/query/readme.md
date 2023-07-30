## 1. 풀 텍스트 쿼리

### match_all

해당 인덱스의 전체 도큐먼트를 검색한다.

```
GET my_index/_search
```

### match

해당 문자열이 포함된 도큐먼트를 검색한다.

```
GET my_index/_search
{
  "query": {
    "match": {
      "message": "dog cat"
    }
  }
}
```

만약 `dog cat`과 같이 여러 개의 단어를 넣게 되면 OR로 검색된다.

AND 조건으로 검색하려면 아래와 같이 operator를 변경해야 한다.

```
GET my_index/_search
{
  "query": {
    "match": {
      "message": {
        "query": "quick dog",
        "operator": "and"
      }
    }
  }
}
```

### match_phrase

AND 검색을 사용할 경우 한 Document의 여러 개의 단어가 모두 포함되면 대상에 포함된다.

```
GET my_index/_search
{
  "query": {
    "match_phrase": {
      "message": "lazy dog"
    }
  }
}
```

match_phrase는 공백을 포함해 문자열의 정확한 일치 여부로 검색한다.

### query_string

URL 검색에 사용하는 Query String 문법을 검색에 활용할 수 있다.

```
GET my_index/_search
{
  "query": {
    "query_string": {
      "default_field": "message",
      "query": "(jumping AND lazy) OR \"quick dog\""
    }
  }
}
```

## 2. Bool 복합 쿼리

boolean 로직을 사용하는 쿼리를 조합해서 만들 수 있다.
- must: 쿼리가 참인 도큐먼트를 검색
- must_not: 쿼리가 거짓인 도큐먼트를 검색
- should: 검색 결과 중 이 쿼리에 해당하는 도큐먼트의 점수를 높인다.
- filter: 쿼리가 참인 도큐먼트를 검색하지만 스코어를 계산하지 않는다.

## 3. 정확도

ES는 RDB와 달리 정확도를 계산하는 알고리즘을 가지고 있고, 정확도를 기반으로 사용자가 가장 원하는 검색 결과를 보여줄 수 있다.

이를 relevancy(렐러번시) 라고 한다.

### TF

검색할 문자열이 많이 포함되어 있을 수록 점수가 높다.
- 최대 BM: 25

### IDF

- 더 희소한 단어가 많을 수록 점수가 높다.
- 쥬라기 공원의 경우 **공원**보다 **쥬라기**가 더 희소하다면 점수가 높다.

### Field Length

검색할 개수가 같다면 필드 Length가 짧을 수록 점수가 높다.

## 4. Term 쿼리

Term 쿼리는 애널라이저를 적용하지 않고 입력된 검색어 그대로 일치하는 텀을 찾는다.

그래서 전문 검색보다는 단순 문자열 검색에 유리하다.

## 참고
- https://esbook.kimjmin.net/05-search