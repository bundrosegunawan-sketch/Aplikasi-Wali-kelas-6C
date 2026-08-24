package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.DapodikBackupScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GradeManagementScreen
import com.example.ui.screens.ReportCardScreen
import com.example.ui.screens.SchoolProfileScreen
import com.example.ui.screens.StudentManagementScreen
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TealSecondary

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val school by viewModel.schoolProfile.collectAsState()
    val students by viewModel.students.collectAsState()

    val configuration = LocalConfiguration.current
    val isTabletOrWide = configuration.screenWidthDp >= 720

    val navTabs = listOf(
        NavTabItem("Dashboard", Icons.Default.Dashboard, "nav_dashboard"),
        NavTabItem("Siswa", Icons.Default.People, "nav_students"),
        NavTabItem("Presensi", Icons.Default.QrCodeScanner, "nav_attendance"),
        NavTabItem("Nilai", Icons.Default.Assessment, "nav_grades"),
        NavTabItem("Rapor PDF", Icons.Default.PictureAsPdf, "nav_report"),
        NavTabItem("Sekolah", Icons.Default.School, "nav_school"),
        NavTabItem("Dapodik", Icons.Default.CloudSync, "nav_dapodik")
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Navigation Rail for Tablet / Expanded screens
        if (isTabletOrWide) {
            NavigationRail(
                containerColor = NavyDark,
                contentColor = Color.White,
                header = {
                    Surface(
                        color = GoldAccent,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp)
                            .size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, contentDescription = null, tint = NavyDark, modifier = Modifier.size(24.dp))
                        }
                    }
                }
            ) {
                navTabs.forEachIndexed { index, item ->
                    NavigationRailItem(
                        selected = activeTab == index,
                        onClick = { viewModel.activeTab.value = index },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 10.sp) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = NavyDark,
                            selectedTextColor = GoldAccent,
                            indicatorColor = GoldAccent,
                            unselectedIconColor = Color.White.copy(alpha = 0.7f),
                            unselectedTextColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }

        Scaffold(
            modifier = modifier.weight(1f),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = GoldAccent,
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = NavyDark, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            androidx.compose.foundation.layout.Column {
                                Text(
                                    text = "Wali Kelas 6 SD",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${school?.className ?: "Kelas VI"} • Kurikulum Merdeka Fase C",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = NavyDark
                    )
                )
            },
            bottomBar = {
                if (!isTabletOrWide) {
                    NavigationBar(
                        containerColor = NavyDark,
                        contentColor = Color.White,
                        tonalElevation = 8.dp
                    ) {
                        navTabs.forEachIndexed { index, item ->
                            val isSelected = activeTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.activeTab.value = index },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NavyDark,
                                    selectedTextColor = GoldAccent,
                                    indicatorColor = GoldAccent,
                                    unselectedIconColor = Color.White.copy(alpha = 0.65f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.65f)
                                ),
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                when (activeTab) {
                    0 -> DashboardScreen(viewModel = viewModel)
                    1 -> StudentManagementScreen(viewModel = viewModel)
                    2 -> AttendanceScreen(viewModel = viewModel)
                    3 -> GradeManagementScreen(viewModel = viewModel)
                    4 -> ReportCardScreen(viewModel = viewModel)
                    5 -> SchoolProfileScreen(viewModel = viewModel)
                    6 -> DapodikBackupScreen(viewModel = viewModel)
                    else -> DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}
