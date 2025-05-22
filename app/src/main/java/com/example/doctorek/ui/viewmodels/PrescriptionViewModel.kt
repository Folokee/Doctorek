package com.example.doctorek.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doctorek.data.models.PatientPrescription
import com.example.doctorek.data.repositories.PrescriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrescriptionViewModel(private val repository: PrescriptionRepository, private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(PrescriptionUiState())
    val uiState: StateFlow<PrescriptionUiState> = _uiState.asStateFlow()

    init {
        loadPrescriptions()
    }

    fun loadPrescriptions() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            repository.getPatientPrescriptions().collect { result ->
                result.fold(
                    onSuccess = { prescriptions ->
                        _uiState.update { 
                            it.copy(
                                prescriptions = prescriptions,
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = throwable.message ?: "Unknown error occurred"
                            )
                        }
                    }
                )
            }
        }
    }

    fun searchPrescriptions(query: String) {
        val currentPrescriptions = _uiState.value.prescriptions
        if (query.isEmpty()) {
            _uiState.update { it.copy(filteredPrescriptions = currentPrescriptions) }
        } else {
            val filtered = currentPrescriptions.filter { prescription ->
                prescription.doctor.name.contains(query, ignoreCase = true) ||
                prescription.prescriptionDate.contains(query, ignoreCase = true) ||
                prescription.details.medications.any { med -> 
                    med.name.contains(query, ignoreCase = true)
                }
            }
            _uiState.update { it.copy(filteredPrescriptions = filtered) }
        }
    }

    fun refreshPrescriptions() {
        loadPrescriptions()
    }

    fun downloadPrescriptionPdf(prescription: PatientPrescription) {
        _uiState.update { it.copy(isDownloading = true) }
        viewModelScope.launch {
            try {
                val pdfUri = withContext(Dispatchers.IO) {
                    generatePdf(prescription)
                }
                _uiState.update { it.copy(isDownloading = false, pdfUri = pdfUri) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isDownloading = false, 
                        error = "Failed to generate PDF: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun clearPdfUri() {
        _uiState.update { it.copy(pdfUri = null) }
    }

    private suspend fun generatePdf(prescription: PatientPrescription): Uri {
        return withContext(Dispatchers.IO) {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()
            
            // Set up text formatting
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            
            // Draw header
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 18f
            canvas.drawText("Medical Prescription", 50f, 50f, paint)
            
            // Draw doctor information
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 14f
            canvas.drawText("Doctor Information", 50f, 90f, paint)
            
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            canvas.drawText("Name: ${prescription.doctor.name}", 70f, 110f, paint)
            canvas.drawText("Specialty: ${prescription.doctor.specialty}", 70f, 130f, paint)
            canvas.drawText("Hospital: ${prescription.doctor.hospitalName}", 70f, 150f, paint)
            
            // Draw patient information
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 14f
            canvas.drawText("Patient Information", 50f, 180f, paint)
            
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            canvas.drawText("Name: ${prescription.patient.name}", 70f, 200f, paint)
            canvas.drawText("Email: ${prescription.patient.email}", 70f, 220f, paint)
            if (prescription.patient.phone.isNotEmpty()) {
                canvas.drawText("Phone: ${prescription.patient.phone}", 70f, 240f, paint)
            }
            
            // Draw date
            canvas.drawText("Date: ${prescription.prescriptionDate}", 400f, 110f, paint)
            canvas.drawText("Prescription ID: ${prescription.localId}", 400f, 130f, paint)
            
            // Draw separator line
            paint.strokeWidth = 1f
            canvas.drawLine(50f, 260f, 562f, 260f, paint)
            
            // Draw medications heading
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 14f
            canvas.drawText("Medications", 50f, 290f, paint)
            
            // Draw medications
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 12f
            
            var yPosition = 320f
            prescription.details.medications.forEachIndexed { index, medication ->
                canvas.drawText("${index + 1}. ${medication.name}", 70f, yPosition, paint)
                yPosition += 20f
                canvas.drawText("   Dosage: ${medication.dosage}", 90f, yPosition, paint)
                yPosition += 20f
                canvas.drawText("   Frequency: ${medication.frequency}", 90f, yPosition, paint)
                yPosition += 20f
                canvas.drawText("   Duration: ${medication.duration}", 90f, yPosition, paint)
                yPosition += 40f
            }
            
            // Additional notes if available
            if (!prescription.additionalNotes.isNullOrEmpty()) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText("Additional Notes:", 50f, yPosition, paint)
                yPosition += 20f
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                canvas.drawText(prescription.additionalNotes, 70f, yPosition, paint)
            }
            
            // Finalize the page and document
            pdfDocument.finishPage(page)
            
            // Create file
            val fileName = "prescription_${prescription.localId.replace("-", "_")}.pdf"
            val file = File(context.cacheDir, fileName)
            
            // Save the document
            FileOutputStream(file).use { fileOutputStream ->
                pdfDocument.writeTo(fileOutputStream)
            }
            
            pdfDocument.close()
            
            // Return the file URI
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PrescriptionViewModel::class.java)) {
                return PrescriptionViewModel(
                    repository = PrescriptionRepository(context),
                    context = context
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class PrescriptionUiState(
    val prescriptions: List<PatientPrescription> = emptyList(),
    val filteredPrescriptions: List<PatientPrescription> = emptyList(),
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val error: String? = null,
    val pdfUri: Uri? = null
)
