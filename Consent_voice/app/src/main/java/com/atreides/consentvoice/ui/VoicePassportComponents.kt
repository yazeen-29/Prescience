package com.atreides.consentvoice.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.atreides.voiceconsent.PersonaAccessory
import com.atreides.voiceconsent.PersonaEmojiProfile
import com.atreides.voiceconsent.PersonaEyes
import com.atreides.voiceconsent.PersonaHair
import com.atreides.voiceconsent.PersonaMouth
import com.atreides.voiceconsent.PersonaPalette

/** Presentation-only Voice Passport editor. The caller owns the selected profile. */
@Composable
fun VoicePersonaEditor(persona: PersonaEmojiProfile, onPersonaChange: (PersonaEmojiProfile) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VoicePersonaPreview(persona, Modifier.padding(end = 16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("${persona.palette.label} · ${persona.hair.label}")
                    Text("${persona.eyes.label} eyes · ${persona.mouth.label}")
                    Text("${persona.accessory.label} accessory")
                }
            }
            VoicePersonaChoice("Palette: ${persona.palette.label}") { onPersonaChange(persona.copy(palette = persona.palette.next())) }
            VoicePersonaChoice("Hair: ${persona.hair.label}") { onPersonaChange(persona.copy(hair = persona.hair.next())) }
            VoicePersonaChoice("Eyes: ${persona.eyes.label}") { onPersonaChange(persona.copy(eyes = persona.eyes.next())) }
            VoicePersonaChoice("Mouth: ${persona.mouth.label}") { onPersonaChange(persona.copy(mouth = persona.mouth.next())) }
            VoicePersonaChoice("Accessory: ${persona.accessory.label}") { onPersonaChange(persona.copy(accessory = persona.accessory.next())) }
            Text("After peer approval, share only this recipe; the receiving device renders it locally.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun VoicePersonaChoice(label: String, onClick: () -> Unit) { Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) { Text(label) } }

@Composable
private fun VoicePersonaPreview(persona: PersonaEmojiProfile, modifier: Modifier = Modifier) {
    val face = when (persona.palette) { PersonaPalette.SUNSET -> Color(0xFFFFC56E); PersonaPalette.OCEAN -> Color(0xFF78D4E8); PersonaPalette.MINT -> Color(0xFF9DE0B4); PersonaPalette.LILAC -> Color(0xFFC7B2F5) }
    Canvas(modifier = modifier.height(104.dp).fillMaxWidth(0.30f)) {
        val r = size.minDimension * 0.36f; val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f); drawCircle(face, r, center)
        val eyeY = center.y - r * 0.12f; val eyeOffset = r * 0.34f; val eyeRadius = r * 0.075f
        when (persona.eyes) { PersonaEyes.DOTS -> { drawCircle(Color(0xFF1E293B), eyeRadius, center.copy(x = center.x - eyeOffset, y = eyeY)); drawCircle(Color(0xFF1E293B), eyeRadius, center.copy(x = center.x + eyeOffset, y = eyeY)) }; PersonaEyes.HAPPY -> { drawArc(Color(0xFF1E293B), 190f, 160f, false, center.copy(x = center.x - eyeOffset - eyeRadius, y = eyeY - eyeRadius), androidx.compose.ui.geometry.Size(eyeRadius * 2, eyeRadius * 1.4f), style = Stroke(5f)); drawArc(Color(0xFF1E293B), 190f, 160f, false, center.copy(x = center.x + eyeOffset - eyeRadius, y = eyeY - eyeRadius), androidx.compose.ui.geometry.Size(eyeRadius * 2, eyeRadius * 1.4f), style = Stroke(5f)) }; PersonaEyes.SPARKLE -> { drawCircle(Color.White, eyeRadius * 1.5f, center.copy(x = center.x - eyeOffset, y = eyeY)); drawCircle(Color.White, eyeRadius * 1.5f, center.copy(x = center.x + eyeOffset, y = eyeY)) } }
        val mouthY = center.y + r * 0.25f
        when (persona.mouth) { PersonaMouth.SMILE -> drawArc(Color(0xFF1E293B), 10f, 160f, false, androidx.compose.ui.geometry.Offset(center.x - r * .35f, mouthY - r * .15f), androidx.compose.ui.geometry.Size(r * .7f, r * .5f), style = Stroke(6f)); PersonaMouth.CALM -> drawLine(Color(0xFF1E293B), center.copy(x = center.x - r * .25f, y = mouthY), center.copy(x = center.x + r * .25f, y = mouthY), 6f); PersonaMouth.GRIN -> drawRoundRect(Color.White, center.copy(x = center.x - r * .28f, y = mouthY - r * .08f), androidx.compose.ui.geometry.Size(r * .56f, r * .25f), androidx.compose.ui.geometry.CornerRadius(r * .07f)) }
        when (persona.hair) { PersonaHair.CURLY -> repeat(5) { drawCircle(Color(0xFF4A2D22), r * .18f, center.copy(x = center.x - r * .55f + it * r * .28f, y = center.y - r * .75f)) }; PersonaHair.SWEEP -> drawArc(Color(0xFF4A2D22), 185f, 175f, true, androidx.compose.ui.geometry.Offset(center.x - r, center.y - r), androidx.compose.ui.geometry.Size(r * 2, r * 1.1f)); PersonaHair.BOB -> drawArc(Color(0xFF293241), 180f, 180f, true, androidx.compose.ui.geometry.Offset(center.x - r, center.y - r), androidx.compose.ui.geometry.Size(r * 2, r * 1.55f)); PersonaHair.CAP -> drawArc(Color(0xFFDC5F40), 180f, 180f, true, androidx.compose.ui.geometry.Offset(center.x - r, center.y - r * 1.1f), androidx.compose.ui.geometry.Size(r * 2, r * 1.35f)) }
        when (persona.accessory) { PersonaAccessory.NONE -> Unit; PersonaAccessory.GLASSES -> { drawCircle(Color(0xFF1E293B), r * .23f, center.copy(x = center.x - eyeOffset, y = eyeY), style = Stroke(5f)); drawCircle(Color(0xFF1E293B), r * .23f, center.copy(x = center.x + eyeOffset, y = eyeY), style = Stroke(5f)); drawLine(Color(0xFF1E293B), center.copy(x = center.x - r * .11f, y = eyeY), center.copy(x = center.x + r * .11f, y = eyeY), 5f) }; PersonaAccessory.STAR -> drawCircle(Color(0xFFFFD23F), r * .13f, center.copy(x = center.x + r * .63f, y = center.y - r * .55f)) }
    }
}

private fun <T> T.next(values: Array<T>): T = values[(values.indexOf(this) + 1) % values.size]
private fun PersonaPalette.next() = next(PersonaPalette.values())
private fun PersonaHair.next() = next(PersonaHair.values())
private fun PersonaEyes.next() = next(PersonaEyes.values())
private fun PersonaMouth.next() = next(PersonaMouth.values())
private fun PersonaAccessory.next() = next(PersonaAccessory.values())
