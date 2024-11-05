package com.example.jetttipapp.Utils

fun calculateTotalTip(totalBill: Double,
                      tipPercentage: Int): Double
{
    if(totalBill>0)
        return (tipPercentage*totalBill) /100
    else
        return 0.0
}


fun calTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int) : Double
{
    val bill=calculateTotalTip(totalBill,tipPercentage)+totalBill
    return bill/splitBy
}
