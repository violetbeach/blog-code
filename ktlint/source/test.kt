fun getUserTypeCode(user: User): String =
        when (user.type) {
            Type.MEMBER ->
                if (true) {
                    return 1
                }

            Type.ADMIN ->
                if (true) {
                    return 2
                }
        }

class User {
    val type: Type
}

enum class Type {
    ADMIN,
    MEMBER,
}
