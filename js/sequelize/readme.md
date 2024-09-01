### Sequelize.fn()

Sequelize.fn()을 사용하면 sql 함수를 쉽게 사용할 수 있습니다. 예시를 몇개 들겠습니다.

```ts
// SUBSTR(str, pos);
sequelize.fn("substr", str, pos);

// IFNULL(a, b);
sequelize.fn("ifnull", a, b);

// SUM(a, b);
sequelize.fn("sum", a, b);

// CONCAT(a, b);
sequelize.fn("concat", a, b);
```

첫 번째 파라미터로는 함수명을, 두 번째 파라미터부터는 대상을 지정하면 됩니다.

```ts
function fn(fn: string, ...args: unknown[]): Fn;
```

SELECT에서 사용할 때는 아래와 같이 사용할 수 있습니다. attributes를 배열로 정의하면 AS를 사용할 수 있습니다.

```ts
const completeEmail = sequelize.fn("ifnull", sequelize.col("email"), "nothing");
const fullName = sequelize.fn("concat", sequelize.col("first_name"), " ", sequelize.col("last_name");
 
await this.model.findAndCountAll({
    attributes: ["id", [completeEmail, "email"], [fullName, "name"]],
    where: {
        id: user.id
    }
});
```

### Sequelize.where()

학습한 Sequelize.fn()을 where절에서 사용할 때는 Sequelize.where()을 사용할 수 있습니다.

```ts
const fullName = sequelize.fn("concat", sequelize.col("first_name"), " ", sequelize.col("last_name");
const completeEmail = sequelize.fn("ifnull", sequelize.col("email"), "nothing");

await this.model.findAndCountAll({
    attributes: ["id", [completeEmail, "email"], [fullName, "name"]],
    where: {
        [Op.or]: [
            sequelize.where(fullName, {[Op.like]: `%${query}%`}),
            sequelize.where(completeEmail, {[Op.like]: `%${query}%`})
        ],
    }
});
```

그리고, Where 절의 basic한 문장과 같이 사용하려면 두 가지 방법이 있습니다. 첫 번째는 key를 임의로 정의하고, value로 sequelize.where()을 사용하는 방법이 있습니다.

```ts
const fullName = sequelize.fn("concat", sequelize.col("first_name"), " ", sequelize.col("last_name");

await this.model.findAndCountAll({
    attributes: ["id", [fullName, "name"]],
    where: {
    	id: id,
    	$custom: sequelize.where(fullName, {[Op.like]: `%${query}%`})
    }
});
```

Object.assign()으로 객체를 병합하는 방법도 있습니다.

```ts
const fullName = sequelize.fn("concat", sequelize.col("first_name"), " ", sequelize.col("last_name");

await this.model.findAndCountAll({
    attributes: ["id", [fullName, "name"]],
    where: Object.assign({
    	id: id
    }, sequelize.where(fullName, {[Op.like]: `%${query}%`}))
});
```

### Sequelize.col()

Sequelize.col()을 사용하면 특정 컬럼을 선택할 수 있습니다. 예를 들면, Left Join을 해서 결과가 있을 시 Join 테이블의 컬럼을 사용하고, 아니면 메인 테이블의 컬럼을 사용하는 상황이라고 가정합니다.

Sequelize.col()을 사용해서 특정 테이블에 있는 컬럼을 지정할 수 있습니다.

```ts
const completeName = sequelize.fn("ifnull", sequelize.col("user_info.nickname"), sequelize.col("user.name"));

const result = await this.model.findAndCountAll({
    attributes: ["no", [completeName, "name"]],
    include: {
        association: model.associations["user_info"],
        required: false,
        as: "user_info"
    }
});
```

### Sequelize.literal()

서술적인 SQL 함수는 Sequelize.fn()으로 해결할 수 없습니다.

가령 IF나 CASE의 경우에는 컬럼이나 상수 뿐만 아니라, >=, <=, >, <, IN  등의 문자가 들어가서 fn()으로 처리하기 까다롭습니다.

이 때 Sequelize.literal()을 사용할 수 있습니다.

```ts
const completeEmail = Sequelize.literal(`
    if(user.domain IS NULL, user.email, CONCAT(user.email, ${domain}))
`);

await this.model.findAndCountAll({
    attributes: ["id", [completeEmail, "email"]],
    where: {
        id: user.id
    }
});
```

감사합니다.
