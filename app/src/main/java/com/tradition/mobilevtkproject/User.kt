package com.tradition.mobilevtkproject

import java.io.Serializable

class User (
    var id: String? = null, var email: String = "", var accessLevel: Level = Level.RegularUser, var pass: String = "", var name: String = "", var surname: String = "", var age: Int = 0, var balance: Int = 0
): Serializable