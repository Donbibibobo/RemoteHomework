package com.donDon

fun main(args: Array<String>) {
    val human = Human("Terry")
    human.attack()

    val mage = Mage("DonDon")
    mage.attack()

    println(basicData.Human.mana)     // 0，Human 沒有魔力
}


open class Human(var name: String){
    open fun attack() {
        println("$name use First Attack!")
    }
}


class Mage(name: String): Human(name) {
    override fun attack() {
        println("$name use Fireball!")
    }
}

// 使用 enum class 來設計「角色數量」與「基礎數值」
// 好處：「新增角色」容易，也很容易訪問到「特定角色」的「某項數值」，並且「數據集中管理」程式碼較清楚
enum class basicData(var strength: Int, var mana: Int, var speed: Int, var healthPoint: Int) {
    Human(10,0,40,100),
    Mage(3,100,20,80),
    Knight(20,50,80,200)
}
