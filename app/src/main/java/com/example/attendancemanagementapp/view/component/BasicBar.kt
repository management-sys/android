package com.example.attendancemanagementapp.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue

/* 기본 상단바 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(
    title: String,                  // 제목
    actIcon: ImageVector? = null,   // 액션 아이콘
    actTint: Color = Color.Black,   // 액션 아이콘 색상
    onClickNavIcon: () -> Unit,     // 뒤로 가기 아이콘 클릭 이벤트
    onClickActIcon: () -> Unit = {} // 액션 아이콘 클릭 이벤트
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.shadow(elevation = 1.dp),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onClickNavIcon()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로 가기 버튼"
                )
            }
        },
        actions = {
            if (actIcon != null) {
                IconButton(
                    onClick = {
                        onClickActIcon()
                    }
                ) {
                    Icon(
                        imageVector = actIcon,
                        contentDescription = "액션 버튼",
                        tint = actTint
                    )
                }
            }
        }
    )
}

/* 정보 수정 바 */
@Composable
fun EditBar(
    name: String,                           // 이름
    value: String,                          // 값
    onValueChange: (String) -> Unit = {},   // 값 변경 시 실행 함수
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false             // 필수 여부
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        OutlinedTextField(
            modifier = Modifier.weight(0.65f),
            value = value,
            onValueChange = { onValueChange(it) },
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = DarkGray,
                focusedBorderColor = DarkGray,
                disabledContainerColor = DisableGray,
                disabledBorderColor = DarkGray,
                disabledTextColor = DarkGray
            ),
            placeholder = {
                Text(
                    text = value,
                    fontSize = 16.sp
                )
            },
            enabled = enabled
        )
    }
}

/* 정보 수정 바 - 큰 텍스트 필드 */
@Composable
fun BigEditBar(
    name: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(0.35f).padding(top = 13.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedTextField(
            modifier = Modifier.weight(0.65f).height(150.dp),
            value = value,
            onValueChange = { onValueChange(it) },
            singleLine = false,
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = DarkGray,
                focusedBorderColor = DarkGray,
                disabledContainerColor = DisableGray,
                disabledBorderColor = DarkGray,
                disabledTextColor = DarkGray
            ),
            placeholder = {
                Text(
                    text = value,
                    fontSize = 16.sp
                )
            },
            enabled = enabled
        )
    }
}

/* 정보 수정 바 - 라디오 버튼 */
@Composable
fun RadioEditBar(
    name: String,
    selected: String,
    isRequired: Boolean = false
) {
    val options = listOf("사용", "미사용")

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        Row(
            Modifier.weight(0.65f).padding(end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEach { info ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = info == selected,
                        enabled = false,
                        onClick = {}
                    )
                    Text(
                        text = info,
                        fontSize = 16.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

/* 정보 수정 바 - 검색 */
@Composable
fun SearchEditBar(
    name: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        OutlinedTextField(
            modifier = Modifier.weight(0.5f),
            value = value,
            onValueChange = {},
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = DisableGray,
                disabledBorderColor = DarkGray,
                disabledTextColor = DarkGray
            ),
            placeholder = {
                Text(
                    text = value,
                    fontSize = 16.sp
                )
            },
            enabled = false
        )

        IconButton(
            onClick = { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "상위코드 검색",
                tint = MainBlue
            )
        }
    }
}