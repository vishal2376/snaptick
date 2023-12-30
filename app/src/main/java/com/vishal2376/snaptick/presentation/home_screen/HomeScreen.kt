package com.vishal2376.snaptick.presentation.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.TaskViewModel
import com.vishal2376.snaptick.presentation.common.fontRoboto
import com.vishal2376.snaptick.presentation.common.h1TextStyle
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.home_screen.components.BottomSheetComponent
import com.vishal2376.snaptick.presentation.home_screen.components.EmptyTaskComponent
import com.vishal2376.snaptick.presentation.home_screen.components.InfoComponent
import com.vishal2376.snaptick.presentation.home_screen.components.TaskComponent
import com.vishal2376.snaptick.ui.theme.Blue
import com.vishal2376.snaptick.ui.theme.Green
import com.vishal2376.snaptick.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
	taskViewModel: TaskViewModel,
) {

	val tasks by taskViewModel.taskList.collectAsStateWithLifecycle(initialValue = emptyList())

	val totalTasks = tasks.size
	val completedTasks = tasks.count { it.isCompleted }

	var showBottomSheet by rememberSaveable {
		mutableStateOf(false)
	}

	Scaffold(topBar = {
		TopAppBar(modifier = Modifier.padding(end = 16.dp),
		          colors = TopAppBarDefaults.topAppBarColors(
			          containerColor = MaterialTheme.colorScheme.background
		          ),
		          title = {
			          Text(
				          text = stringResource(id = R.string.app_name),
				          style = h1TextStyle
			          )
		          },
		          actions = {
			          Text(
				          text = "10",
				          fontSize = 18.sp,
				          fontFamily = fontRoboto,
				          fontWeight = FontWeight.Bold
			          )
			          Spacer(modifier = Modifier.width(4.dp))
			          Icon(
				          painter = painterResource(id = R.drawable.ic_fire),
				          contentDescription = null,
				          tint = Yellow,
				          modifier = Modifier.size(22.dp)
			          )
		          })
	},
	         floatingActionButton = {
		         FloatingActionButton(
			         onClick = {
				         showBottomSheet = true
			         },
			         containerColor = MaterialTheme.colorScheme.secondary,
			         contentColor = Color.White
		         ) {
			         Icon(
				         imageVector = Icons.Default.Add,
				         contentDescription = null
			         )
		         }
	         }) { innerPadding ->

		BottomSheetComponent(
			showBottomSheet,
			onClose = { showBottomSheet = false },
			taskViewModel = taskViewModel
		)

		Column(modifier = Modifier.padding(innerPadding)) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(
						16.dp,
						8.dp
					),
				horizontalArrangement = Arrangement.spacedBy(16.dp),
				verticalAlignment = Alignment.CenterVertically
			) {

				InfoComponent(
					title = "Completed",
					desc = "$completedTasks/$totalTasks Tasks",
					icon = R.drawable.ic_task_list,
					backgroundColor = Green,
					modifier = Modifier.weight(1f)
				)

				InfoComponent(
					title = "Free Time",
					desc = "8 hours",
					icon = R.drawable.ic_clock,
					backgroundColor = Blue,
					modifier = Modifier.weight(1f)
				)

			}

			if (tasks.isEmpty()) {
				EmptyTaskComponent()
			} else {
				Text(
					text = stringResource(R.string.today_tasks),
					style = h2TextStyle,
					color = Color.White,
					modifier = Modifier.padding(16.dp)
				)

				LazyColumn(
					modifier = Modifier
						.fillMaxSize()
						.padding(
							16.dp,
							0.dp
						)
				) {
					items(items = tasks,
					      key = { it.id }) { task ->
						TaskComponent(task = task)
						Spacer(modifier = Modifier.height(10.dp))
					}
				}
			}
		}
	}
}