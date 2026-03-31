package com.unstampedpages.app.ui.screens.mystamps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
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
    val context = LocalContext.current
    val pendingCameraUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.saveStampImage(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        viewModel.saveCameraImage(success)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pendingCameraUri.value?.let { uri ->
                cameraLauncher.launch(uri)
            }
        }
        pendingCameraUri.value = null
    }

    fun launchCamera() {
        val uri = viewModel.createCameraUri()
        if (uri != null) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                cameraLauncher.launch(uri)
            } else {
                pendingCameraUri.value = uri
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("my_stamps_screen")
    ) {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("header_row"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Country",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .testTag("column_header_country")
            )
            Text(
                text = "Stamp",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .width(80.dp)
                    .testTag("column_header_stamp"),
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("country_stamps_list"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(
                items = uiState.countryStamps,
                key = { it.country.code }
            ) { countryStamp ->
                CountryStampRow(
                    countryStamp = countryStamp,
                    onAddClick = { viewModel.showUploadDialog(countryStamp.country) },
                    onRemoveClick = { viewModel.removeStamp(countryStamp.country.code) },
                    testTag = "country_row_${countryStamp.country.code}"
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                )
            }
        }
    }

    // Upload Dialog
    if (uiState.showUploadDialog && uiState.selectedCountry != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUploadDialog() },
            modifier = Modifier.testTag("add_stamp_dialog"),
            title = {
                Text(
                    text = "Add Stamp",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("dialog_title")
                )
            },
            text = {
                Column {
                    Text(
                        text = "Add a passport stamp image for ${uiState.selectedCountry?.name}",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Take a new photo or choose an existing image from your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { launchCamera() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("camera_button"),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Camera")
                        }
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gallery_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Secondary,
                                contentColor = Primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gallery")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissUploadDialog() },
                    modifier = Modifier.testTag("cancel_button")
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
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
    onRemoveClick: () -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag(testTag),
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
                    onRemoveClick = onRemoveClick,
                    testTag = "stamp_${countryStamp.country.code}"
                )
            } else {
                AddStampButton(
                    onClick = onAddClick,
                    testTag = "add_stamp_${countryStamp.country.code}"
                )
            }
        }
    }
}

@Composable
private fun StampThumbnail(
    imagePath: String,
    countryName: String,
    onRemoveClick: () -> Unit,
    testTag: String = ""
) {
    val bitmap = remember(imagePath) {
        try {
            BitmapFactory.decodeFile(imagePath)?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Box(modifier = Modifier.testTag(testTag)) {
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
                .testTag("${testTag}_remove")
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
private fun AddStampButton(
    onClick: () -> Unit,
    testTag: String = ""
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 2.dp,
                color = Secondary.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
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
