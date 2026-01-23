package com.example.attendancemanagementapp.data.repository

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.data.param.VacationsQuery
import com.example.attendancemanagementapp.retrofit.service.VacationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.net.URLDecoder
import javax.inject.Inject

class VacationRepository @Inject constructor(private val service: VacationService) {
    // 휴가 신청
    fun addVacation(request: VacationDTO.AddVacationRequest): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.addVacation(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 상세 조회
    fun getVacation(vacationId: String): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.getVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 정보 수정
    fun updateVacation(vacationId: String, request: VacationDTO.UpdateVacationRequest): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.updateVacation(
            vacationId = vacationId,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 삭제
    fun deleteVacation(vacationId: String): Flow<Result<Unit>> = flow {
        val response = service.deleteVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 취소
    fun cancelVacation(vacationId: String): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.cancelVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 이전 승인자 불러오기
    fun getPrevApprovers(userId: String): Flow<Result<ApproverDTO.GetPrevApproversResponse>> = flow {
        val response = service.getPrevApprovers(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 현황 목록 조회
    fun getVacations(userId: String, query: VacationsQuery, page: Int): Flow<Result<VacationDTO.GetVacationsResponse>> = flow {
        val response = service.getVacations(
            userId = userId,
            year = if (query.year == 0) null else query.year,
            filter =  query.filter.toKey(),
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청서 다운로드(PDF)
    fun downloadVacationPdf(context: Context, vacationId: String): Flow<Result<android.net.Uri>> = flow {
        val response = service.downloadVacationPdf(vacationId = vacationId)
        val body = response.body() ?: throw IllegalStateException("Empty body")

        val fileName = extreactFileName(
            contentDisposition = response.headers()["content-disposition"],
            fallback = "vacation_${vacationId}.pdf"
        )

        val uri = withContext(Dispatchers.IO) {
            savePdfToDownloads(
                context = context,
                body = body,
                fileName = fileName
            )
        }

        emit(Result.success(uri))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 파일명 추출
    private fun extreactFileName(contentDisposition: String?, fallback: String): String {
        if (contentDisposition.isNullOrBlank()) return fallback

        val idx = contentDisposition.indexOf("filename=")
        if (idx == -1) return fallback

        var raw = contentDisposition.substring(idx + "filename=".length)
            .substringBefore(";")
            .trim()
            .removePrefix("\"")
            .removeSuffix("\"")

        val decoded = runCatching { URLDecoder.decode(raw, "UTF-8") }.getOrNull()
        return (decoded ?: fallback).ifBlank { fallback }
    }

    // pdf 파일 저장
    private fun savePdfToDownloads(context: Context, body: ResponseBody, fileName: String): android.net.Uri {
        val safeName = if (fileName.endsWith(".pdf", ignoreCase = true)) fileName else "$fileName.pdf"

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, safeName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // "Download/"
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("MediaStore insert failed")

        resolver.openOutputStream(uri)?.use { output ->
            body.byteStream().use { input ->
                input.copyTo(output)
            }
        } ?: throw IllegalStateException("openOutputStream failed")

        values.clear()
        values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)

        return uri
    }
}