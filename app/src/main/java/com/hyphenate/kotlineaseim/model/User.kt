package com.hyphenate.kotlineaseim.model

class User {
    lateinit var id: String
    lateinit var avatar: String
    lateinit var name: String
    lateinit var role: String

    constructor(id: String, avatar: String, name: String, role: String) {
        this.id = id
        this.avatar = avatar
        this.name = name
        this.role = role
    }
}