import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.coursework.R
import com.example.coursework.features.entry.presentation.viewmodel.EntryAction

@Composable
fun EntryScreen(
    onAction: (EntryAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),
                        Color(0xFF00BCD4)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 250.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AniPaint",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    onAction(EntryAction.CreatePicture)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Создать рисунок",
                    tint = Color(0xFF6A11CB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Создать рисунок",
                    color = Color(0xFF6A11CB),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = {
                    onAction(EntryAction.CreateAnimation)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Создать анимацию",
                    tint = Color(0xFF6A11CB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Создать анимацию",
                    color = Color(0xFF6A11CB),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Button(
                onClick = {
                    onAction(EntryAction.OpenGallery)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.baseline_photo_library_24),
                    contentDescription = "Галерея",
                    tint = Color(0xFF6A11CB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Галерея",
                    color = Color(0xFF6A11CB),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}