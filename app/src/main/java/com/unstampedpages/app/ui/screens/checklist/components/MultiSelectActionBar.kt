package com.unstampedpages.app.ui.screens.checklist.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unstampedpages.app.ui.theme.Accent
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

@Composable
fun MultiSelectActionBar(
    selectedCount: Int,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("multi_select_action_bar"),
        color = Secondary,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier.testTag("multi_select_cancel")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel selection",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "$selectedCount selected",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("selected_count")
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onDelete,
                modifier = Modifier.testTag("multi_select_delete")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Accent
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Delete",
                    color = Accent,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
