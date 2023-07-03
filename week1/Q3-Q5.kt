package com.Don

fun main(args: Array<String>) {
    val human = Human("Terry")
    human.attack()

    // 方法一，直接為 Human 新增 mana 屬性，用「Human 物件」搭配 if 查看是否有 mana
    if (human.mana == 0) {
        println("Human don't have mana.")
    }

    // 發法二，為 Human 新增擴展函式，將「Human 物件」的「有無魔力」另外設計為函式處理
    human.manaCheck()

    val mage = Mage("DonDon")
    mage.attack()
}

open class Human(var name: String){
    open fun attack() {
        println("$name use First Attack!")
    }
    val mana: Int = 0;
}

class Mage(name: String): Human(name) {
    override fun attack() {
        println("$name use Fireball!")
    }
}

fun Human.manaCheck() {
    println("nomana")     // 暫定
}