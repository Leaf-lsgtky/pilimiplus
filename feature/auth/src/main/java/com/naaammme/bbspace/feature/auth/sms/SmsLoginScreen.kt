package com.naaammme.bbspace.feature.auth.sms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.overlay.OverlayListPopup
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naaammme.bbspace.core.designsystem.component.CollapsingTopBarScaffold
import com.naaammme.bbspace.core.model.SmsLoginState
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SmsLoginScreen(
    viewModel: SmsLoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onBack: () -> Unit = {},
    onSwitchToQr: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val countdown by viewModel.countdown.collectAsStateWithLifecycle()
    val lastCaptchaKey by viewModel.lastCaptchaKey.collectAsStateWithLifecycle()
    val countryList by viewModel.countryList.collectAsStateWithLifecycle()
    val selectedCountry by viewModel.selectedCountry.collectAsStateWithLifecycle()
    val loadingCountries by viewModel.loadingCountries.collectAsStateWithLifecycle()

    var phone by rememberSaveable { mutableStateOf("") }
    var smsCode by rememberSaveable { mutableStateOf("") }
    var countryDropdownExpanded by remember { mutableStateOf(false) }

    // 极验弹窗需要的 token
    var geetestToken by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is SmsLoginState.Success) {
            onLoginSuccess()
        }
    }

    // 极验弹窗
    val geetestState = state
    if (geetestState is SmsLoginState.NeedGeetest) {
        geetestToken = geetestState.token
        GeetestDialog(
            gt = geetestState.gt,
            challenge = geetestState.challenge,
            onResult = { result ->
                viewModel.onGeetestResult(result, geetestToken)
            },
            onDismiss = {
                viewModel.resetState()
            }
        )
    }

    CollapsingTopBarScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = { Text("手机号登录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(MiuixIcons.Back, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box {
                TextField(
                    value = phone,
                    onValueChange = { phone = it.filter { c -> c.isDigit() } },
                    label = "手机号",
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Row(
                            modifier = Modifier.clickable {
                                countryDropdownExpanded = true
                                viewModel.fetchCountryCodes()
                            },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+${selectedCountry.countryCode}",
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OverlayListPopup(
                    show = countryDropdownExpanded,
                    onDismissRequest = { countryDropdownExpanded = false }
                ) {
                    ListPopupColumn {
                        if (loadingCountries) {
                            DropdownImpl(
                                text = "加载中...",
                                optionSize = 1,
                                isSelected = false,
                                index = 0,
                                enabled = false,
                                onSelectedIndexChange = {}
                            )
                        } else {
                            countryList.forEachIndexed { index, country ->
                                DropdownImpl(
                                    text = "+${country.countryCode} ${country.cname}",
                                    optionSize = countryList.size,
                                    isSelected = selectedCountry == country,
                                    index = index,
                                    onSelectedIndexChange = {
                                        viewModel.selectCountry(country)
                                        countryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = smsCode,
                    onValueChange = { smsCode = it.filter { c -> c.isDigit() }.take(6) },
                    label = "验证码",
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                val smsSent = state is SmsLoginState.SmsSent
                val sending = state is SmsLoginState.SendingSms
                val canSend = selectedCountry.countryCode != 86 || phone.length >= 11
                val sendEnabled = canSend && countdown == 0 && !sending

                Button(
                    onClick = { viewModel.sendSms(phone) },
                    enabled = sendEnabled,
                    modifier = Modifier.height(56.dp)
                ) {
                    Text(
                        when {
                            sending -> "发送中..."
                            countdown > 0 -> "${countdown}s"
                            smsSent -> "重新发送"
                            else -> "获取验证码"
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val canLogin = smsCode.length == 6 && (state is SmsLoginState.SmsSent || (state is SmsLoginState.Error && lastCaptchaKey.isNotEmpty()))
            val logging = state is SmsLoginState.Logging

            Button(
                onClick = { viewModel.login(phone, code = smsCode) },
                enabled = canLogin && !logging,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (logging) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("登录")
                }
            }

            // 错误提示
            val errorState = state
            if (errorState is SmsLoginState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorState.msg,
                    color = MiuixTheme.colorScheme.error,
                    style = MiuixTheme.textStyles.body2
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "手机号仅用于请求 B站官方接口获取鉴权信息，所有数据均保存于本地设备。请务必通过 GitHub 渠道下载本应用。",
                style = MiuixTheme.textStyles.footnote1,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSwitchToQr) {
                Text("扫码登录")
            }
        }
    }
}
