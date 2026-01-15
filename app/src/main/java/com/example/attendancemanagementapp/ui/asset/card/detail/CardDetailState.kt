package com.example.attendancemanagementapp.ui.asset.card.detail

import com.example.attendancemanagementapp.data.dto.CardDTO

data class CardDetailState(
    val cardInfo: CardDTO.GetCardResponse = CardDTO.GetCardResponse()
)