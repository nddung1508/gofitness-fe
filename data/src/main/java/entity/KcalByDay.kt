package entity

data class KcalByDay (
    val kcal: Double,
    val timestamp: Long
){
    constructor() : this(0.0, 0)
}