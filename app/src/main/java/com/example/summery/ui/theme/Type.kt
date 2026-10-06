package com.example.summery.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PrimaryTextColor = Color(0xFF1C1B1F)
val SecondaryTextColor = Color(0xFF49454F)

val SummeryTypography = Typography(
    //-Headers ->  Store names, Product names ? NOT screen titles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp,
        lineHeight = 26.sp,
        color = PrimaryTextColor
    ),


    //Card titles
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        color = PrimaryTextColor
    ),


    //Primary text, in card info
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 19.sp,
        color = PrimaryTextColor
    ),

    //Micro text, mainly the "i", action chips, ALSO RATINGS
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        color = SecondaryTextColor
    ),

)

    //NEW ONE JUST FOR DESCRIPTIONS
    val Typography.descriptionText: TextStyle
        get() = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 26.sp,
            color = Color.DarkGray
        )
