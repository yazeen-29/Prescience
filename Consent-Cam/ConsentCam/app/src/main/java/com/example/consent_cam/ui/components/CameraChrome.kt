package com.example.consent_cam.ui.components

import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.consent_cam.CaptureMode
import com.example.consent_cam.PrivacyMode
import com.example.consent_cam.ble.AppearanceConsent
import com.example.consent_cam.ble.BleConsentUiState
import com.example.consent_cam.camera.CameraCapabilities
import com.example.consent_cam.ui.theme.CameraPanel
import com.example.consent_cam.ui.theme.CameraPanelLight
import com.example.consent_cam.ui.theme.IqooYellow
import com.example.consent_cam.ui.theme.MutedText
import com.example.consent_cam.ui.theme.SafeGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.net.Uri

/** Stateless visual building blocks. Screen state and callbacks remain in MainActivity. */
@Composable
internal fun PermissionPanel(onRequestPermissions: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Camera permission needed", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("ConsentCam needs the camera to show the privacy viewfinder.", color = MutedText, textAlign = TextAlign.Center)
        Spacer(Modifier.height(22.dp))
        Button(onClick = onRequestPermissions, colors = ButtonDefaults.buttonColors(containerColor = IqooYellow, contentColor = Color.Black)) { Text("Allow camera") }
    }
}

@Composable
internal fun PrescienceStatusStrip(facePassportReady: Boolean, voicePassportReady: Boolean, bleActive: Boolean, visualProtectionActive: Boolean) {
    val items = listOf("Face" to facePassportReady, "Voice" to voicePassportReady, "BLE" to bleActive, "Visual" to visualProtectionActive, "Voice protection" to true)
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xD9101010)).padding(horizontal = 10.dp, vertical = 8.dp)) {
        items.chunked(3).forEach { row -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) { row.forEach { (label, ready) -> Text(if (ready) "• $label" else "○ $label", color = if (ready) SafeGreen else MutedText, fontSize = 10.sp, maxLines = 1, modifier = Modifier.weight(1f)) } } }
    }
}

@Composable
internal fun PrivacyBadge(privacyMode: PrivacyMode, privacyEnabled: Boolean, bleState: BleConsentUiState, detectedFaceCount: Int, preciseStatus: String?) {
    Row(Modifier.clip(RoundedCornerShape(16.dp)).background(Color(0xE6101010)).border(1.dp, Color(0xFF454545), RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(9.dp).clip(CircleShape).background(if (privacyEnabled && bleState.protectionActive) IqooYellow else SafeGreen)); Spacer(Modifier.width(9.dp)); Column { Text(if (privacyEnabled) "${privacyMode.title} active • $detectedFaceCount face(s)" else "Automatic protection disabled", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold); Text(if (!privacyEnabled) "BLE consent broadcasting remains active" else preciseStatus ?: if (bleState.protectionActive) "Nearby PROTECT • preview masked" else "${bleState.nearbySessionCount} BLE consent session(s)", color = MutedText, fontSize = 12.sp) }
    }
}

@Composable
internal fun BleConsentCard(state: BleConsentUiState, onSetConsent: (AppearanceConsent) -> Unit, onStartBle: () -> Unit, onRequestPermissions: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = CameraPanel), shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("BLE CONSENT DEMO", color = MutedText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(8.dp)); Text(state.status, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { AppearanceConsent.entries.forEach { consent -> Button(onClick = { onSetConsent(consent) }, colors = ButtonDefaults.buttonColors(containerColor = if (state.ownConsent == consent) IqooYellow else CameraPanelLight, contentColor = if (state.ownConsent == consent) Color.Black else Color.White)) { Text(consent.label, fontWeight = FontWeight.Bold) } } }
            Spacer(Modifier.height(10.dp)); Text("Advertising: ${if (state.advertising) "ON" else "OFF"}  •  Scanning: ${if (state.scanning) "ON" else "OFF"}", color = MutedText, fontSize = 12.sp); Text("Tokens accepted: ${state.acceptedTokenCount}  •  Nearby: ${state.nearbySessionCount}  •  Band: ${state.proximityBand}", color = if (state.protectionActive) IqooYellow else SafeGreen, fontSize = 12.sp); Spacer(Modifier.height(8.dp)); OutlinedButton(onClick = if (state.permissionsGranted) onStartBle else onRequestPermissions) { Text(if (state.permissionsGranted) "Start / retry BLE" else "Allow BLE permissions") }
        }
    }
}

@Composable
internal fun RecordingBadge(seconds: Int) { val minutes = seconds / 60; val remainingSeconds = seconds % 60; Row(Modifier.clip(RoundedCornerShape(50)).background(Color(0xE60D0D0D)).padding(horizontal = 13.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(9.dp).clip(CircleShape).background(Color(0xFFF63B30))); Spacer(Modifier.width(8.dp)); Text("REC %02d:%02d".format(minutes, remainingSeconds), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold) } }

@Composable
internal fun ZoomRow(selectedZoom: Float, capabilities: CameraCapabilities, onZoomSelected: (Float) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { listOf(0.6f, 1f, 2f).forEach { ratio -> val supported = ratio >= capabilities.minimumZoomRatio && ratio <= capabilities.maximumZoomRatio; val selected = kotlin.math.abs(selectedZoom - ratio) < 0.05f; Text(if (ratio == 1f) "1×" else "${ratio}×", color = when { selected -> IqooYellow; supported -> Color.White; else -> Color.DarkGray }, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.clip(RoundedCornerShape(50)).clickable(enabled = supported) { onZoomSelected(ratio) }.padding(horizontal = 16.dp, vertical = 9.dp)) } } }

@Composable
internal fun CaptureModeRow(selected: CaptureMode, onModeChanged: (CaptureMode) -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CaptureMode.entries.forEach { mode -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 20.dp).clickable { onModeChanged(mode) }) { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }, color = if (mode == selected) IqooYellow else Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp); Spacer(Modifier.height(4.dp)); Box(Modifier.width(if (mode == selected) 22.dp else 0.dp).height(2.dp).background(IqooYellow)) } } } }

@Composable
internal fun CameraControls(captureMode: CaptureMode, isRecording: Boolean, isCapturing: Boolean, latestMedia: Uri?, onGallery: () -> Unit, onSwitchCamera: () -> Unit, onShutter: () -> Unit) { Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Box(Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(if (latestMedia != null) CameraPanelLight else Color(0xFF474747)).clickable(enabled = latestMedia != null) { onGallery() }, contentAlignment = Alignment.Center) { if (latestMedia == null) Text("□", color = Color.White, fontSize = 23.sp) else SavedMediaPreview(latestMedia, "media", Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))) }; Box(Modifier.size(82.dp).clip(CircleShape).border(4.dp, Color.White, CircleShape).padding(6.dp).clickable(enabled = !isCapturing) { onShutter() }, contentAlignment = Alignment.Center) { Box(Modifier.fillMaxSize().clip(if (captureMode == CaptureMode.VIDEO && isRecording) RoundedCornerShape(10.dp) else CircleShape).background(if (captureMode == CaptureMode.VIDEO) Color(0xFFF63B30) else if (isCapturing) Color(0xFFD9D9D9) else Color.White)) }; CircleButton("↻", "Switch camera", onSwitchCamera) } }

@Composable
internal fun SavedMediaPreview(uri: Uri, mediaType: String, modifier: Modifier = Modifier) { val context = LocalContext.current; var bitmap by remember(uri) { mutableStateOf<Bitmap?>(null) }; LaunchedEffect(uri) { bitmap = withContext(Dispatchers.IO) { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) runCatching { context.contentResolver.loadThumbnail(uri, Size(720, 960), null) }.getOrNull() else runCatching { context.contentResolver.openInputStream(uri)?.use { android.graphics.BitmapFactory.decodeStream(it) } }.getOrNull() } }; Box(modifier.background(CameraPanel), contentAlignment = Alignment.Center) { if (bitmap != null) Image(bitmap = bitmap!!.asImageBitmap(), contentDescription = "Latest saved $mediaType", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop) else Text("Loading $mediaType…", color = MutedText) } }

@Composable
internal fun CircleButton(label: String, contentDescription: String, onClick: () -> Unit) { Box(Modifier.size(44.dp).clip(CircleShape).background(Color(0xB5000000)).border(1.dp, Color(0x553D3D3D), CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) { Text(label, color = Color.White, fontSize = 23.sp, maxLines = 1, overflow = TextOverflow.Clip) } }

@Composable
internal fun StatusPill(text: String, color: Color) { Row(Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.13f)).padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(7.dp).clip(CircleShape).background(color)); Spacer(Modifier.width(6.dp)); Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) } }
