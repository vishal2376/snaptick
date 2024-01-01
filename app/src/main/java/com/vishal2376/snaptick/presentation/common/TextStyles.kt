package com.vishal2376.snaptick.presentation.common

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vishal2376.snaptick.R


val fontRoboto = FontFamily(Font(R.font.roboto))
val fontMontserrat = FontFamily(Font(R.font.montserrat))

var h1TextStyle = TextStyle(
	fontSize = 24.sp,
	fontFamily = fontMontserrat,
	fontWeight = FontWeight.Bold
)

var h2TextStyle = TextStyle(
	fontSize = 20.sp,
	fontFamily = fontMontserrat,
	fontWeight = FontWeight.Bold
)

val infoTextStyle = TextStyle(
	fontSize = 18.sp,
	fontFamily = fontMontserrat,
	fontWeight = FontWeight.SemiBold
)

val infoDescTextStyle = TextStyle(
	fontSize = 14.sp,
	fontFamily = fontRoboto
)

var taskDescTextStyle = TextStyle(
	fontSize = 12.sp,
	fontFamily = fontRoboto
)

var taskTextStyle = TextStyle(
	fontSize = 16.sp,
	fontFamily = fontRoboto,
)
