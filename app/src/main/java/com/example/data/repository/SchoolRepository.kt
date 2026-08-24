package com.example.data.repository

import com.example.data.dao.AssessmentDao
import com.example.data.dao.AttendanceDao
import com.example.data.dao.SchoolDao
import com.example.data.dao.StudentDao
import com.example.data.dao.SubjectDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceSummary
import com.example.data.model.FormativeScore
import com.example.data.model.LearningObjective
import com.example.data.model.SchoolProfile
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.SummativeScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SchoolRepository(
    private val schoolDao: SchoolDao,
    private val studentDao: StudentDao,
    private val subjectDao: SubjectDao,
    private val assessmentDao: AssessmentDao,
    private val attendanceDao: AttendanceDao
) {
    // School Profile
    val schoolProfile: Flow<SchoolProfile?> = schoolDao.getSchoolProfile()
    suspend fun getSchoolProfileDirect(): SchoolProfile? = schoolDao.getSchoolProfileDirect()
    suspend fun updateSchoolProfile(profile: SchoolProfile) = schoolDao.insertOrUpdate(profile)

    // Students
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val studentCount: Flow<Int> = studentDao.getStudentCount()
    suspend fun getAllStudentsDirect(): List<Student> = studentDao.getAllStudentsDirect()
    fun getStudentById(id: Long): Flow<Student?> = studentDao.getStudentById(id)
    suspend fun getStudentByIdDirect(id: Long): Student? = studentDao.getStudentByIdDirect(id)
    suspend fun getStudentByQrCode(qr: String): Student? = studentDao.getStudentByQrCode(qr)
    suspend fun insertStudent(student: Student): Long = studentDao.insertStudent(student)
    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)
    suspend fun deleteStudent(student: Student) = studentDao.deleteStudent(student)

    suspend fun syncOfficial29Roster(officialStudents: List<Student>) {
        studentDao.deleteAllStudents()
        studentDao.insertAll(officialStudents)

        // Populate scores & attendance
        assessmentDao.deleteAllFormative()
        assessmentDao.deleteAllSummative()
        attendanceDao.deleteAllAttendance()

        val formativeList = mutableListOf<FormativeScore>()
        val summativeList = mutableListOf<SummativeScore>()
        val subjectCodes = listOf("PAIBP", "PANCASILA", "BINDO", "MTK", "IPAS", "SBDP", "PJOK", "BING", "MULOK")

        officialStudents.forEachIndexed { studentIdx, student ->
            subjectCodes.forEach { subId ->
                val baseScore = 80 + ((studentIdx * 3 + subId.hashCode().let { if (it < 0) -it else it }) % 16)
                formativeList.add(FormativeScore(0, student.id, subId, "TP 6.1", (baseScore + 2).coerceIn(70, 98), "Tuntas"))
                formativeList.add(FormativeScore(0, student.id, subId, "TP 6.2", (baseScore - 1).coerceIn(68, 96), "Tuntas"))
                formativeList.add(FormativeScore(0, student.id, subId, "TP 6.3", (baseScore + 1).coerceIn(72, 99), "Tuntas"))

                val sumatif1 = (baseScore + 1).coerceIn(70, 98)
                val sumatif2 = (baseScore - 2).coerceIn(68, 96)
                val sts = (baseScore + 3).coerceIn(70, 99)
                val sas = (baseScore).coerceIn(70, 97)

                summativeList.add(SummativeScore(0, student.id, subId, "SUMATIF_1", sumatif1, "Ulangan Harian 1"))
                summativeList.add(SummativeScore(0, student.id, subId, "SUMATIF_2", sumatif2, "Ulangan Harian 2"))
                summativeList.add(SummativeScore(0, student.id, subId, "STS", sts, "Sumatif Tengah Semester"))
                summativeList.add(SummativeScore(0, student.id, subId, "SAS", sas, "Sumatif Akhir Semester"))
            }
        }
        assessmentDao.insertAllFormative(formativeList)
        assessmentDao.insertAllSummative(summativeList)

        // Seed Attendance
        val today = "2025-10-15"
        val yesterday = "2025-10-14"
        val attendanceList = mutableListOf<AttendanceRecord>()

        officialStudents.forEachIndexed { idx, student ->
            val statusToday = when (idx) {
                4 -> "SAKIT"
                11 -> "IZIN"
                19 -> "TERLAMBAT"
                else -> "HADIR"
            }
            attendanceList.add(
                AttendanceRecord(
                    id = 0,
                    studentId = student.id,
                    date = today,
                    status = statusToday,
                    time = if (statusToday == "TERLAMBAT") "07:25 WIB" else "06:55 WIB",
                    method = if (idx % 2 == 0) "QR_SCAN" else "MANUAL",
                    notes = if (statusToday == "SAKIT") "Demam, surat dokter terlampir" else if (statusToday == "IZIN") "Acara keluarga" else "Tepat waktu",
                    notifiedWa = true
                )
            )
            attendanceList.add(
                AttendanceRecord(
                    id = 0,
                    studentId = student.id,
                    date = yesterday,
                    status = if (idx == 8) "SAKIT" else "HADIR",
                    time = "07:00 WIB",
                    method = "QR_SCAN",
                    notes = "Tepat waktu",
                    notifiedWa = true
                )
            )
        }
        attendanceDao.insertAll(attendanceList)
    }

    // Subjects & TPs
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()
    suspend fun getAllSubjectsDirect(): List<Subject> = subjectDao.getAllSubjectsDirect()
    fun getObjectivesBySubject(subjectId: String): Flow<List<LearningObjective>> =
        subjectDao.getObjectivesBySubject(subjectId)
    suspend fun getAllObjectivesDirect(): List<LearningObjective> = subjectDao.getAllObjectivesDirect()
    suspend fun insertObjective(objective: LearningObjective) =
        subjectDao.insertObjectives(listOf(objective))

    // Formative Scores
    fun getFormativeScores(studentId: Long, subjectId: String): Flow<List<FormativeScore>> =
        assessmentDao.getFormativeScores(studentId, subjectId)
    suspend fun getAllFormativeForStudent(studentId: Long): List<FormativeScore> =
        assessmentDao.getAllFormativeForStudent(studentId)
    suspend fun getAllFormativeScoresDirect(): List<FormativeScore> =
        assessmentDao.getAllFormativeScoresDirect()
    suspend fun saveFormativeScore(score: FormativeScore) =
        assessmentDao.insertFormativeScore(score)

    // Summative Scores
    fun getSummativeScores(studentId: Long, subjectId: String): Flow<List<SummativeScore>> =
        assessmentDao.getSummativeScores(studentId, subjectId)
    suspend fun getAllSummativeForStudent(studentId: Long): List<SummativeScore> =
        assessmentDao.getAllSummativeForStudent(studentId)
    suspend fun getAllSummativeScoresDirect(): List<SummativeScore> =
        assessmentDao.getAllSummativeScoresDirect()
    suspend fun saveSummativeScore(score: SummativeScore) =
        assessmentDao.insertSummativeScore(score)

    // Attendance
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceByDate(date)
    suspend fun getAttendanceByDateDirect(date: String): List<AttendanceRecord> =
        attendanceDao.getAttendanceByDateDirect(date)
    fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceByStudent(studentId)
    suspend fun getAttendanceByStudentDirect(studentId: Long): List<AttendanceRecord> =
        attendanceDao.getAttendanceByStudentDirect(studentId)
    val allAttendance: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendance()
    suspend fun getAllAttendanceDirect(): List<AttendanceRecord> =
        attendanceDao.getAllAttendanceDirect()

    suspend fun recordAttendance(
        studentId: Long,
        date: String,
        status: String,
        time: String,
        method: String,
        notes: String = ""
    ) {
        attendanceDao.insertAttendance(
            AttendanceRecord(
                id = 0,
                studentId = studentId,
                date = date,
                status = status,
                time = time,
                method = method,
                notes = notes,
                notifiedWa = false
            )
        )
    }

    suspend fun updateAttendance(record: AttendanceRecord) =
        attendanceDao.updateAttendance(record)

    suspend fun getStudentAttendanceSummary(studentId: Long): AttendanceSummary {
        val records = attendanceDao.getAttendanceByStudentDirect(studentId)
        var h = 0
        var s = 0
        var i = 0
        var a = 0
        var t = 0
        records.forEach { r ->
            when (r.status) {
                "HADIR" -> h++
                "SAKIT" -> s++
                "IZIN" -> i++
                "ALPA" -> a++
                "TERLAMBAT" -> t++
            }
        }
        return AttendanceSummary(
            hadir = h,
            sakit = s,
            izin = i,
            alpa = a,
            terlambat = t,
            totalDays = records.size
        )
    }
}
