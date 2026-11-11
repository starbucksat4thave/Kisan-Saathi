package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NodeState
import com.example.data.model.RoadmapNode
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun RoadmapView(
    nodes: List<RoadmapNode>,
    selectedNodeKey: String,
    onSelectNode: (RoadmapNode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        nodes.forEachIndexed { index, node ->
            // Path Connector between nodes
            if (index > 0) {
                val previousState = nodes[index - 1].state
                val isCompletedLink = previousState == NodeState.DONE
                Canvas(
                    modifier = Modifier
                        .width(4.dp)
                        .height(36.dp)
                ) {
                    drawLine(
                        color = if (isCompletedLink) FieldGreen else Color.LightGray,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = 3.dp.toPx(),
                        pathEffect = if (!isCompletedLink) PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f) else null
                    )
                }
            }

            RoadmapNodeItem(
                node = node,
                isSelected = node.key == selectedNodeKey,
                onNodeClick = { onSelectNode(node) },
                // Gentle winding horizontal offset: alternate slightly left and right
                horizontalOffsetDp = when (index % 3) {
                    0 -> (-20).dp
                    1 -> 0.dp
                    else -> 20.dp
                }
            )
        }
    }
}

@Composable
fun RoadmapNodeItem(
    node: RoadmapNode,
    isSelected: Boolean,
    onNodeClick: () -> Unit,
    horizontalOffsetDp: androidx.compose.ui.unit.Dp
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_node")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .offset { IntOffset(horizontalOffsetDp.roundToPx(), 0) }
            .clickable { onNodeClick() }
            .testTag("node_${node.key}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Glowing pulse ring if CURRENT
            if (node.state == NodeState.CURRENT) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(FieldGreen.copy(alpha = 0.22f))
                )
            }

            // Outer Circle Ring
            Surface(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape),
                color = when (node.state) {
                    NodeState.DONE -> FieldGreen
                    NodeState.CURRENT -> MaterialTheme.colorScheme.surface
                    NodeState.UPCOMING -> MaterialTheme.colorScheme.surfaceVariant
                    NodeState.MISSED -> Color(0xFFFFECB3)
                },
                border = androidx.compose.foundation.BorderStroke(
                    width = if (node.state == NodeState.CURRENT) 3.5.dp else 2.dp,
                    color = when (node.state) {
                        NodeState.DONE -> FieldGreen
                        NodeState.CURRENT -> FieldGreen
                        NodeState.UPCOMING -> Color.LightGray
                        NodeState.MISSED -> BarkatGold
                    }
                ),
                shadowElevation = if (node.state == NodeState.CURRENT) 6.dp else 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (node.state == NodeState.DONE) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        val iconVector = getIconForNode(node.icon)
                        Icon(
                            imageVector = iconVector,
                            contentDescription = if (isUrdu) node.titleUr else node.titleEn,
                            tint = when (node.state) {
                                NodeState.CURRENT -> FieldGreen
                                NodeState.UPCOMING -> Color.Gray
                                NodeState.MISSED -> BarkatGold
                                NodeState.DONE -> Color.White
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // "You are here" badge for CURRENT node
            if (node.state == NodeState.CURRENT) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FieldGreen)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isUrdu) "آپ یہاں ہیں" else "You are here",
                        fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                        color = Color.White,
                        fontSize = if (isUrdu) 11.sp else 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Node Title & Details
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = if (isSelected) 1.5.dp else 0.dp,
                    color = if (isSelected) FieldGreen else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isUrdu) node.titleUr else node.titleEn,
                    fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                    fontSize = if (isUrdu) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isUrdu) "${node.titleEn} · ${node.gddStage}" else "${node.titleUr} · ${node.gddStage}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getIconForNode(iconName: String): ImageVector {
    return when (iconName) {
        "agriculture" -> Icons.Default.Agriculture
        "grain" -> Icons.Default.Grain
        "spa" -> Icons.Default.Spa
        "ac_unit" -> Icons.Default.AcUnit
        "bug_report" -> Icons.Default.BugReport
        "wb_sunny" -> Icons.Default.WbSunny
        "inventory_2" -> Icons.Default.Inventory2
        else -> Icons.Default.Grain
    }
}
