package com.example.buildnote

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun SomeView(modifier: Modifier = Modifier,
             navController: NavController) {

    // Can better placed in model
    val myList = mutableListOf("Audi", "BMW", "VW", "Mercedes")

    var showDropdown = remember { mutableStateOf<Boolean>(false) }
    var selectedCar = remember { mutableStateOf("Audi") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter // Aligns content to the top center
    ) {
        Text(
            text = "Some View",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
    Column(modifier = Modifier.padding(40.dp)) {
        // LazyColum only render the visible items -> good for long lists
        LazyColumn (
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(myList){
                myListIndex, item -> Text(item)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dropdown example
        Row {
            Text(selectedCar.value, modifier = Modifier.clickable {
                showDropdown.value = true
            })
            Icon(painter = painterResource(
                id = R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "Dropdown Icon"
            )
            DropdownMenu(expanded = showDropdown.value,
                onDismissRequest = { showDropdown.value = false }
            ) {
                myList.forEach{
                    DropdownMenuItem(
                        {Text(it)},
                        onClick = {
                            selectedCar.value = it
                            showDropdown.value = false
                        }
                    )
                }
            }

        }

        Spacer(modifier = Modifier.weight(1.0f))

        Button(onClick = {navController.navigate("Greeting")}) {
            Text("Navigate back")
        }
    }
}
