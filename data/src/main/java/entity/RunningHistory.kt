package entity
data class RunningHistory(
    val kcal : Double,
    val duration : Int,
    val distance: Double,
    val dateInMillis : Long,
    val polylines: List<String>
){
    constructor() : this(0.0, 0, 0.0, 0L, emptyList())
}
