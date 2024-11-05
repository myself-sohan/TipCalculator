package com.example.jetttipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetttipapp.Utils.calTotalPerPerson
import com.example.jetttipapp.Utils.calculateTotalTip
import com.example.jetttipapp.components.InputField
import com.example.jetttipapp.ui.theme.JettTipAppTheme
import com.example.jetttipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JettTipAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    )
                    {
                        MyApp()
                    }
                }
            }
        }
    }
}
@Composable
fun MyApp()
{
    MainContent()
}


@Composable
fun TopHeader(totalPerPerson : Double=0.0)
{
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(15.dp),
            //.clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        //color = Color(229,210,246,255),
        color= Color(0xFFE9D7F7),
        shape=RoundedCornerShape(corner = CornerSize(12.dp))
    )
    {
        Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            val total= "%.2f".format(totalPerPerson)
            Text(text="Total Per Person",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
                )
            Text(text="$$total",
                fontWeight = FontWeight.ExtraBold,
                style= MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContent()
{
    BillForm()
    {

    }
}


@Composable
fun BillForm(modifier: Modifier=Modifier,
              onValChange:(String) -> Unit= {})
{
    val totalBillState= rememberSaveable { mutableStateOf("") }
    val validState= remember(totalBillState.value)
    {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController= LocalSoftwareKeyboardController.current
    val splitByState= remember { mutableIntStateOf(1) }
    val sliderPositionState= remember { mutableFloatStateOf(0f) }
    val tipPercentage=(sliderPositionState.floatValue*100).toInt()
    val tipAmountState= remember { mutableDoubleStateOf(0.0) }
    val totalPerPersonState= remember { mutableDoubleStateOf(0.0) }
    Column(verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {
        TopHeader(totalPerPerson=totalPerPersonState.doubleValue)

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            shape = RoundedCornerShape(corner = CornerSize(8.dp)),
            border = BorderStroke(width = 1.dp, color = Color(235, 235, 235, 255))
        )
        {
            Column(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start

            ) {
                InputField(
                    valueState = totalBillState,
                    labelId = "Enter Bill",
                    enabled = true,
                    isSingleLine = true,
                    onAction = KeyboardActions {
                        if (!validState) return@KeyboardActions
                        onValChange(totalBillState.value.trim())

                        keyboardController?.hide()
                    }
                )
                if (validState)
                {
                    val parsedBill = totalBillState.value.toDoubleOrNull()
                    if (parsedBill != null) {
                        tipAmountState.doubleValue = calculateTotalTip(parsedBill, tipPercentage)
                        totalPerPersonState.doubleValue= calTotalPerPerson(
                            totalBill = parsedBill,
                            splitBy = splitByState.intValue,
                            tipPercentage=tipPercentage)
                    }

                    Row(
                        modifier = Modifier
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.Start
                    )
                    {
                        Text(
                            text = "Split",
                            modifier = Modifier
                                .align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(
                            modifier = Modifier.width(120.dp)
                        )
                        Row(
                            modifier = Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End
                        )
                        {
                            RoundIconButton(
                                iconPainter = painterResource(R.drawable.remove),
                                onClick = {
                                    if (splitByState.intValue > 1)
                                        splitByState.intValue--
                                    totalPerPersonState.doubleValue= calTotalPerPerson(
                                        totalBill = (totalBillState.value).toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage=tipPercentage)
                                }

                            )
                            Text(
                                text = splitByState.intValue.toString(),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .padding(start = 10.dp, end = 10.dp)
                            )
                            RoundIconButton(
                                iconPainter = painterResource(R.drawable.add),
                                onClick = { splitByState.intValue++
                                    totalPerPersonState.doubleValue= calTotalPerPerson(
                                        totalBill = (totalBillState.value).toDouble(),
                                        splitBy = splitByState.intValue,
                                        tipPercentage=tipPercentage)
                                })

                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 3.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Start
                    )
                    {
                        Text(
                            text = "Tip",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(200.dp))

                        Text(
                            text = "${tipAmountState.doubleValue}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Text(text = "$tipPercentage %")
                        Spacer(modifier = Modifier.height(14.dp))
                        Slider(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                            value = sliderPositionState.floatValue,
                            onValueChange = { newVal ->
                                sliderPositionState.floatValue = newVal
                            },
                            steps = 100,
                            onValueChangeFinished = {
                            }

                        )

                    }

                } else {
                    Box()
                    {

                    }
                }
            }
        }
    }
}

