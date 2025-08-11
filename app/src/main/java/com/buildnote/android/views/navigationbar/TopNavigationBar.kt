package com.buildnote.android.views.navigationbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import com.buildnote.android.R
@Composable
fun CustomTopBar(
    title: String,
    pageIcon: ImageVector,
    onProfileClick: () -> Unit
) {
    // statusBarsPadding() sorgt dafür, dass der Header unterhalb der Systemstatusleiste beginnt.
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
        // Falls noch mehr Höhe nötig ist, kannst du zusätzlich
        // .padding(top = 4.dp, bottom = 4.dp) nutzen
    ) {
        Row(
                      modifier = Modifier
                                  .fillMaxWidth()
                                .padding(horizontal = 16.dp)  // nur noch seitliches Padding
                                 .height(46.dp),               // fixe Höhe von 56 dp
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Linke Seite: Symbol + Titel
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = pageIcon,
                    contentDescription = "Seitensymbol",
                    tint = Color(0xFF333333),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Rechte Seite: Rundes Profilbild
            Image(
                painter = painterResource(id = R.drawable.buildnote_icon),
                contentDescription = "Profilbild",
                modifier = Modifier
                    .size(56.dp)         // Größe des Icons
                    .clip(CircleShape)
                    .clickable { onProfileClick() }
            )
        }
    }
}
