package com.aarevalo.parking.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.ui.theme.ParkingTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * A card component displaying parking meter information.
 */
@Composable
fun ParkingMeterCard(
    parkingMeter: ParkingMeter,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with meter ID and rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Meter ${parkingMeter.meterId}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                RateBadge(rate = parkingMeter.rate)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            InfoRow(
                icon = Icons.Default.LocationOn,
                label = parkingMeter.streetName,
                contentDescription = "Location"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Time limit
            InfoRow(
                icon = Icons.Default.AccessTime,
                label = "${parkingMeter.timeLimit} min limit",
                contentDescription = "Time limit"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Rate hours
            InfoRow(
                icon = Icons.Default.AttachMoney,
                label = "${parkingMeter.rateStart} - ${parkingMeter.rateEnd}",
                contentDescription = "Rate hours"
            )

            if (parkingMeter.payByPhone) {
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(
                    icon = Icons.Default.Phone,
                    label = "Pay by phone available",
                    contentDescription = "Pay by phone"
                )
            }
        }
    }
}

@Composable
private fun RateBadge(
    rate: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.CANADA)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = "${currencyFormat.format(rate)}/hr",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ParkingMeterCardPreview() {
    ParkingTheme {
        ParkingMeterCard(
            parkingMeter = ParkingMeter(
                meterId = "12345",
                meterHead = "Single",
                rateArea = "Downtown",
                latitude = 49.2827,
                longitude = -123.1207,
                streetNum = 100,
                streetSide = "N",
                streetName = "West Georgia St",
                timeLimit = 120,
                rateDays = "Mon-Sat",
                rateStart = "9:00 AM",
                rateEnd = "10:00 PM",
                rate = 6.0,
                payByPhone = true
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
