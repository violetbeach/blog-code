class UserExam {
    fun getUserTypeCode(user: User): Any = when (user.type) {
        Type.MEMBER -> if (true) {
            1
        } else {
            2
        }

        Type.ADMIN -> if (true) {
            2
        } else {
            1
        }

        else -> 0
    }

    class User {
        var type: Type? = null
    }

    enum class Type {
        ADMIN,
        MEMBER,
    }
}