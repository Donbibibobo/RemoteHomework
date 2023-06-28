package com.donDon

fun main(args: Array<String>) {
    val human = Human("Terry")
    human.attack()
    println("remaining mana: ${human.mana}")

    val mage = Mage("DonDon")
    mage.attack()
}



open class Human(var name: String){
    open fun attack() {
        println("$name use First Attack!")
    }
    var mana: Int = 0
}



class Mage(name: String): Human(name) {
    override fun attack() {
        println("$name use Fireball!")
    }
}

