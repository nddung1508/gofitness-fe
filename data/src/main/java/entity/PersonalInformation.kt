package entity

data class PersonalInformation(
    val height : Int,
    val weight : Int,
    val goal : String
){
    constructor() : this(0, 0, "Lose Weight")
}
