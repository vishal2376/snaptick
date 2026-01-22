package com.vishal2376.snaptick.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vishal2376.snaptick.R
import com.vishal2376.snaptick.presentation.common.h2TextStyle
import com.vishal2376.snaptick.presentation.common.taskTextStyle
import com.vishal2376.snaptick.presentation.settings.common.TopLanguage
import com.vishal2376.snaptick.ui.theme.Blue

@Composable
fun LanguageOptionComponent(defaultLanguage: String, onSelect: (String) -> Unit) {
	var selectedLanguage by remember { mutableStateOf(TopLanguage.ENGLISH) }

	for (language in TopLanguage.entries) {
		if (language.languageCode == defaultLanguage) {
			selectedLanguage = language
			break
		}
	}

	Column(horizontalAlignment = Alignment.CenterHorizontally) {
		Text(
			text = stringResource(R.string.select_language),
			style = h2TextStyle,
			color = MaterialTheme.colorScheme.onBackground,
		)
		Spacer(modifier = Modifier.height(8.dp))
		Column(Modifier.verticalScroll(rememberScrollState())) {
			TopLanguage.entries.forEach { language ->
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.clickable {
							selectedLanguage = language
							onSelect(selectedLanguage.languageCode)
						},
					verticalAlignment = Alignment.CenterVertically
				) {
					RadioButton(
						selected = selectedLanguage == language,
						onClick = {
							selectedLanguage = language
							onSelect(selectedLanguage.languageCode)
						},
						colors = RadioButtonDefaults.colors(selectedColor = Blue)
					)
					Text(
						text = "${language.emoji}  ${language.endonym}",
						style = taskTextStyle,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun LanguageOptionComponentPreview() {
	LanguageOptionComponent("en", {})
}