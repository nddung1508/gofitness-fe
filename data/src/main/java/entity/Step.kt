package entity

data class Step(
    val amount : Int,
    val timestamp : Long
){
    constructor() : this(0, 0)
}