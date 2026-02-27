package com.unstampedpages.app.ui.screens.mystamps

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unstampedpages.app.ui.theme.Primary
import com.unstampedpages.app.ui.theme.Secondary

@Composable
fun MyStampsScreen(
    viewModel: MyStampsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.saveStampImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary.copy(alpha = 0.1f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Country",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Stamp",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.width(80.dp),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Primary.copy(alpha = 0.2f))
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = uiState.countryStamps,
                key = { it.country.code }
            ) { countryStamp ->
                CountryStampRow(
                    countryStamp = countryStamp,
                    onAddClick = { viewModel.showUploadDialog(countryStamp.country) },
                    onRemoveClick = { viewModel.removeStamp(countryStamp.country.code) }
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Primary.copy(alpha = 0.1f))
                )
            }
        }
    }

    // Upload Dialog
    if (uiState.showUploadDialog && uiState.selectedCountry != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUploadDialog() },
            title = {
                Text(
                    text = "Add Stamp",
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Upload a passport stamp image for ${uiState.selectedCountry?.name}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Select an image from your device to add to your stamp collection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Secondary,
                        contentColor = Primary
                    )
                ) {
                    Text("Choose Image")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissUploadDialog() }) {
                    Text("Cancel", color = Primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun CountryStampRow(
    countryStamp: CountryStamp,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = countryStamp.country.name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier.width(80.dp),
            contentAlignment = Alignment.Center
        ) {
            if (countryStamp.imagePath != null) {
                StampThumbnail(
                    imagePath = countryStamp.imagePath,
                    countryName = countryStamp.country.name,
                    onRemoveClick = onRemoveClick
                )
            } else {
                AddStampButton(onClick = onAddClick)
            }
        }
    }
}

@Composable
private fun StampThumbnail(
    imagePath: String,
    countryName: String,
    onRemoveClick: () -> Unit
) {
    val bitmap = remember(imagePath) {
        try {
            BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Box {
        Card(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "Stamp for $countryName",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
                .background(
                    color = Color.Red.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove stamp",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
private fun AddStampButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = Secondary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add stamp",
            tint = Secondary,
            modifier = Modifier.size(28.dp)
        )
    }
}
