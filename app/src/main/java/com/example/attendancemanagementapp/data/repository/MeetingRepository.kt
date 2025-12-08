package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.retrofit.service.MeetingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MeetingRepository @Inject constructor(private val service: MeetingService) {
    // 회의록 등록
    fun addMeeting(request: MeetingDTO.AddMeetingRequest): Flow<Result<MeetingDTO.GetMeetingResponse>> = flow {
        val response = service.addMeeting(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 회의록 상세 조회
    fun getMeeting(meetingId: Long): Flow<Result<MeetingDTO.GetMeetingResponse>> = flow {
        val response = service.getMeeting(
            meetingId = meetingId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 회의록 수정
    fun updateMeeting(meetingId: Long, request: MeetingDTO.UpdateMeetingRequest): Flow<Result<MeetingDTO.GetMeetingResponse>> = flow {
        val response = service.updateMeeting(
            meetingId = meetingId,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 회의록 삭제
    fun deleteMeeting(meetingId: Long): Flow<Result<Unit>> = flow {
        val response = service.deleteMeeting(
            meetingId = meetingId
        )
        emit(Result.success(Unit))
    }.catch { e ->
        emit(Result.failure(e))
    }
}