package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AssessmentDao
import com.example.data.dao.AttendanceDao
import com.example.data.dao.SchoolDao
import com.example.data.dao.StudentDao
import com.example.data.dao.SubjectDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.FormativeScore
import com.example.data.model.LearningObjective
import com.example.data.model.SchoolProfile
import com.example.data.model.Student
import com.example.data.model.Subject
import com.example.data.model.SummativeScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolProfile::class,
        Student::class,
        Subject::class,
        LearningObjective::class,
        FormativeScore::class,
        SummativeScore::class,
        AttendanceRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao
    abstract fun studentDao(): StudentDao
    abstract fun subjectDao(): SubjectDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wali_kelas_6_sd.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }
    }
}

suspend fun populateInitialData(database: AppDatabase) {
    // 1. Initial School Profile
    database.schoolDao().insertOrUpdate(
        SchoolProfile(
            id = 1,
            schoolName = "SD NEGERI 01 MERDEKA BELAJAR",
            npsn = "20108923",
            schoolAddress = "Jl. Pendidikan Nasional No. 45, Kebayoran Baru",
            district = "Kebayoran Baru",
            regency = "Kota Jakarta Selatan",
            province = "DKI Jakarta",
            postalCode = "12180",
            email = "sdn01merdeka@kemdikbud.go.id",
            headmasterName = "Dr. H. Bambang Suryono, M.Pd.",
            headmasterNip = "197204151998031004",
            teacherName = "Siti Rahmawati, S.Pd., M.Pd.",
            teacherNip = "198509122010012023",
            teacherPhone = "6281234567890",
            className = "Kelas VI-A (Enam A)",
            phase = "Fase C (Kelas 5-6)",
            academicYear = "2025/2026",
            semester = "1 (Ganjil)",
            curriculum = "Kurikulum Merdeka (Kemendikdasmen)",
            placeAndDateOfReport = "Jakarta, 20 Desember 2025"
        )
    )

    // 2. Initial Subjects (Fase C Kelas 6 SD Kurikulum Merdeka)
    val subjects = listOf(
        Subject("PAIBP", "Pendidikan Agama & Budi Pekerti", "PABP", "Wajib", 1, 75, "menu_book"),
        Subject("PANCASILA", "Pendidikan Pancasila", "PPKn", "Wajib", 2, 75, "gavel"),
        Subject("BINDO", "Bahasa Indonesia", "B. Indo", "Wajib", 3, 75, "translate"),
        Subject("MTK", "Matematika", "MTK", "Wajib", 4, 72, "calculate"),
        Subject("IPAS", "Ilmu Pengetahuan Alam & Sosial", "IPAS", "Wajib", 5, 75, "science"),
        Subject("SBDP", "Seni Rupa & Prakarya", "Seni", "Wajib", 6, 75, "palette"),
        Subject("PJOK", "Pendidikan Jasmani, Olahraga & Kesehatan", "PJOK", "Wajib", 7, 75, "sports_soccer"),
        Subject("BING", "Bahasa Inggris", "B. Ing", "Pilihan", 8, 70, "language"),
        Subject("MULOK", "Bahasa & Sastra Daerah (Mulok)", "Mulok", "Muatan Lokal", 9, 75, "local_library")
    )
    database.subjectDao().insertSubjects(subjects)

    // 3. Learning Objectives (Tujuan Pembelajaran / Lingkup Materi Semester 1)
    val tps = listOf(
        // PAIBP
        LearningObjective(0, "PAIBP", "TP 6.1", "Hukum Bacaan & Makna", "Menganalisis hukum bacaan tajwid dan kandungan makna QS Al-Hujurat ayat 13.", 1),
        LearningObjective(0, "PAIBP", "TP 6.2", "Asmaul Husna", "Meneladani sifat-sifat Allah melalui Asmaul Husna Al-Ghaffar dan Al-Afuw.", 1),
        LearningObjective(0, "PAIBP", "TP 6.3", "Indahnya Toleransi", "Mempraktikkan sikap toleransi dan menghargai perbedaan antar sesama.", 1),
        // Pendidikan Pancasila
        LearningObjective(0, "PANCASILA", "TP 6.1", "Pancasila dalam Kehidupan", "Menganalisis penerapan nilai-nilai Pancasila dalam kehidupan berbangsa.", 1),
        LearningObjective(0, "PANCASILA", "TP 6.2", "Norma, Hak & Kewajiban", "Mengidentifikasi pelaksanaan hak dan kewajiban warga negara secara berimbang.", 1),
        LearningObjective(0, "PANCASILA", "TP 6.3", "Musyawarah Mufakat", "Mempraktikkan musyawarah dalam pengambilan keputusan bersama.", 1),
        // Bahasa Indonesia
        LearningObjective(0, "BINDO", "TP 6.1", "Membaca Teks Eksplanasi", "Mengidentifikasi informasi penting dari teks eksplanasi ilmiah secara kritis.", 1),
        LearningObjective(0, "BINDO", "TP 6.2", "Menulis Formulir & Surat", "Mengisi teks formulir pendaftaran dan menulis surat resmi secara terstruktur.", 1),
        LearningObjective(0, "BINDO", "TP 6.3", "Teks Pidato Persuasif", "Menyusun dan menyampaikan pidato persuasif dengan intonasi yang tepat.", 1),
        // Matematika
        LearningObjective(0, "MTK", "TP 6.1", "Operasi Pecahan & Desimal", "Menyelesaikan masalah perkalian dan pembagian pecahan serta desimal.", 1),
        LearningObjective(0, "MTK", "TP 6.2", "Rasio & Skala", "Memahami konsep rasio proporsional dan penerapannya pada skala peta.", 1),
        LearningObjective(0, "MTK", "TP 6.3", "Bangun Ruang Kubus & Balok", "Menghitung volume dan luas permukaan kubus, balok serta jaring-jaringnya.", 1),
        // IPAS
        LearningObjective(0, "IPAS", "TP 6.1", "Sistem Gerak Manusia", "Menganalisis cara kerja rangka, sendi, dan otot manusia dalam bergerak.", 1),
        LearningObjective(0, "IPAS", "TP 6.2", "Perkembangbiakan Makhluk", "Menjelaskan perkembangbiakan vegetatif dan generatif tumbuhan dan hewan.", 1),
        LearningObjective(0, "IPAS", "TP 6.3", "Negara Anggota ASEAN", "Memahami karakteristik geografis dan kondisi sosial budaya negara ASEAN.", 1),
        // SBdP
        LearningObjective(0, "SBDP", "TP 6.1", "Seni Reklame & Poster", "Merancang dan membuat karya poster persuasif bertema lingkungan.", 1),
        LearningObjective(0, "SBDP", "TP 6.2", "Interval Nada & Melodi", "Memainkan alat musik melodis sederhana dengan membaca partitur notasi.", 1),
        // PJOK
        LearningObjective(0, "PJOK", "TP 6.1", "Permainan Bola Besar", "Mempraktikkan variasi gerak dasar lokomotor dan manipulatif sepak bola/voli.", 1),
        LearningObjective(0, "PJOK", "TP 6.2", "Kebugaran Jasmani", "Melakukan latihan daya tahan jantung dan paru melalui lari interval.", 1),
        // Bahasa Inggris
        LearningObjective(0, "BING", "TP 6.1", "Past Activities", "Expressing past activities using simple past tense in daily conversation.", 1),
        LearningObjective(0, "BING", "TP 6.2", "Describing Directions", "Giving and asking for directions using standard English landmarks.", 1),
        // Mulok
        LearningObjective(0, "MULOK", "TP 6.1", "Aksara & Kesenian Daerah", "Membaca dan menulis kalimat beraksara daerah serta memahami cerita rakyat.", 1)
    )
    database.subjectDao().insertObjectives(tps)

    // 4. Official 29 Students (Kelas 6 SD - Sesuai Data Foto Dokumen Wali Kelas)
    val official29Students = getOfficial29StudentsList()
    database.studentDao().insertAll(official29Students)

    // 5. Initial Assessment Scores (Formatif & Sumatif for each of the 29 students)
    val formativeList = mutableListOf<FormativeScore>()
    val summativeList = mutableListOf<SummativeScore>()

    val subjectCodes = listOf("PAIBP", "PANCASILA", "BINDO", "MTK", "IPAS", "SBDP", "PJOK", "BING", "MULOK")

    official29Students.forEachIndexed { studentIdx, student ->
        subjectCodes.forEach { subId ->
            // Formative TP 6.1, TP 6.2, TP 6.3
            val baseScore = 80 + ((studentIdx * 3 + subId.hashCode().let { if (it < 0) -it else it }) % 16)
            formativeList.add(FormativeScore(0, student.id, subId, "TP 6.1", (baseScore + 2).coerceIn(70, 98), "Tuntas"))
            formativeList.add(FormativeScore(0, student.id, subId, "TP 6.2", (baseScore - 1).coerceIn(68, 96), "Tuntas"))
            formativeList.add(FormativeScore(0, student.id, subId, "TP 6.3", (baseScore + 1).coerceIn(72, 99), "Tuntas"))

            // Summative Scores (Sumatif 1, Sumatif 2, STS, SAS)
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
    database.assessmentDao().insertAllFormative(formativeList)
    database.assessmentDao().insertAllSummative(summativeList)

    // 6. Initial Attendance Records (for today and sample past dates)
    val today = "2025-10-15"
    val yesterday = "2025-10-14"
    val attendanceList = mutableListOf<AttendanceRecord>()

    official29Students.forEachIndexed { idx, student ->
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
        // Yesterday record
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
    database.attendanceDao().insertAll(attendanceList)
}

fun getOfficial29StudentsList(): List<Student> = listOf(
    Student(
        id = 1,
        nis = "6001",
        nisn = "0134829101",
        fullName = "Akifa Nayla Hasibuan",
        nickname = "Akifa",
        gender = "P",
        birthPlace = "Medan",
        birthDate = "2013-04-12",
        religion = "Islam",
        parentName = "Hendra Hasibuan",
        parentPhone = "6281298765401",
        parentJob = "Karyawan Swasta",
        address = "Jl. Melati No. 12, Kebayoran",
        qrCodeId = "STD-6A-001",
        heightCm = 145,
        weightKg = 38,
        extracurricular = "Pramuka, Dokter Kecil",
        studentNotes = "Siswa aktif, komunikatif, dan memiliki daya tangkap tinggi dalam pembelajaran."
    ),
    Student(
        id = 2,
        nis = "6002",
        nisn = "0135892012",
        fullName = "Alifa Naufalyn Fikria Rabbani",
        nickname = "Alifa",
        gender = "P",
        birthPlace = "Bandung",
        birthDate = "2013-05-24",
        religion = "Islam",
        parentName = "Fikri Rabbani",
        parentPhone = "6281312345602",
        parentJob = "PNS Guru",
        address = "Jl. Radio Dalam No. 45, Gandaria",
        qrCodeId = "STD-6A-002",
        heightCm = 144,
        weightKg = 36,
        extracurricular = "Tari Tradisional, Karawitan",
        studentNotes = "Sangat teliti dalam literasi bahasa dan memiliki tutur kata yang santun."
    ),
    Student(
        id = 3,
        nis = "6003",
        nisn = "0137891234",
        fullName = "Aulia Yasmin",
        nickname = "Aulia",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2013-08-15",
        religion = "Islam",
        parentName = "Ir. Yasmin Gunawan",
        parentPhone = "628198765403",
        parentJob = "Arsitek",
        address = "Jl. Panglima Polim V No. 8, Melawai",
        qrCodeId = "STD-6A-003",
        heightCm = 146,
        weightKg = 37,
        extracurricular = "Paduan Suara, Melukis",
        studentNotes = "Memiliki kepekaan estetika seni budaya dan aktif di organisasi kelas."
    ),
    Student(
        id = 4,
        nis = "6004",
        nisn = "0138901235",
        fullName = "Bagaskara Farhan Rahmani",
        nickname = "Bagas",
        gender = "L",
        birthPlace = "Jakarta",
        birthDate = "2013-02-10",
        religion = "Islam",
        parentName = "Farhan Rahmani",
        parentPhone = "628213456704",
        parentJob = "Wiraswasta",
        address = "Jl. Wolter Monginsidi No. 78, Rawa Barat",
        qrCodeId = "STD-6A-004",
        heightCm = 152,
        weightKg = 44,
        extracurricular = "Futsal, Robotik",
        studentNotes = "Penalaran logika dan keterampilan pemecahan masalah matematikanya sangat baik."
    ),
    Student(
        id = 5,
        nis = "6005",
        nisn = "0139012346",
        fullName = "Bryan Faridan Alfaiz",
        nickname = "Bryan",
        gender = "L",
        birthPlace = "Surabaya",
        birthDate = "2013-07-07",
        religion = "Islam",
        parentName = "Faridan Alfaiz",
        parentPhone = "628567890105",
        parentJob = "Karyawan Swasta",
        address = "Jl. Fatmawati No. 23, Cipete",
        qrCodeId = "STD-6A-005",
        heightCm = 150,
        weightKg = 42,
        extracurricular = "Sains Club, Karate",
        studentNotes = "Bersemangat dalam eksperimen sains IPAS dan berjiwa kepemimpinan."
    ),
    Student(
        id = 6,
        nis = "6006",
        nisn = "0131234567",
        fullName = "Deril Raditya",
        nickname = "Deril",
        gender = "L",
        birthPlace = "Yogyakarta",
        birthDate = "2013-11-15",
        religion = "Islam",
        parentName = "Raditya Pratama",
        parentPhone = "628122334406",
        parentJob = "TNI/Polri",
        address = "Jl. Senopati No. 14, Selong",
        qrCodeId = "STD-6A-006",
        heightCm = 153,
        weightKg = 45,
        extracurricular = "Pencak Silat, Renang",
        studentNotes = "Berbakat di bidang keolahragaan dan memiliki disiplin waktu yang tinggi."
    ),
    Student(
        id = 7,
        nis = "6007",
        nisn = "0132345678",
        fullName = "Faiza Mahmudatunnisa Harahap",
        nickname = "Faiza",
        gender = "P",
        birthPlace = "Padang Sidempuan",
        birthDate = "2013-04-03",
        religion = "Islam",
        parentName = "Mahmud Harahap",
        parentPhone = "628133445507",
        parentJob = "Dosen",
        address = "Jl. Barito II No. 19, Kramat Pela",
        qrCodeId = "STD-6A-007",
        heightCm = 143,
        weightKg = 35,
        extracurricular = "Dokter Kecil, English Club",
        studentNotes = "Menunjukkan penguasaan konsep Pendidikan Pancasila dan berakhlak mulia."
    ),
    Student(
        id = 8,
        nis = "6008",
        nisn = "0133456789",
        fullName = "Filia Bernadine",
        nickname = "Filia",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2013-09-29",
        religion = "Katolik",
        parentName = "Bernardus Suryo",
        parentPhone = "628177889908",
        parentJob = "Akuntan",
        address = "Jl. Dharmawangsa X No. 5, Pulo",
        qrCodeId = "STD-6A-008",
        heightCm = 142,
        weightKg = 34,
        extracurricular = "Melukis, Paduan Suara",
        studentNotes = "Memiliki kreativitas visual tinggi dan gemar membaca buku cerita."
    ),
    Student(
        id = 9,
        nis = "6009",
        nisn = "0134567890",
        fullName = "Ghadira Arisha Afifah",
        nickname = "Ghadira",
        gender = "P",
        birthPlace = "Bogor",
        birthDate = "2013-06-18",
        religion = "Islam",
        parentName = "Afifuddin",
        parentPhone = "628188990009",
        parentJob = "Wiraswasta",
        address = "Jl. Iskandarsyah Raya No. 32, Melawai",
        qrCodeId = "STD-6A-009",
        heightCm = 146,
        weightKg = 39,
        extracurricular = "Pramuka, Tari",
        studentNotes = "Percaya diri dalam presentasi kelompok dan memiliki empati yang hangat."
    ),
    Student(
        id = 10,
        nis = "6010",
        nisn = "0135678901",
        fullName = "Ghuint Medez Hetharie",
        nickname = "Ghuint",
        gender = "L",
        birthPlace = "Ambon",
        birthDate = "2013-12-05",
        religion = "Kristen",
        parentName = "Medez Hetharie",
        parentPhone = "628199001110",
        parentJob = "Karyawan BUMN",
        address = "Jl. Hang Tuah VI No. 9, Gunung",
        qrCodeId = "STD-6A-010",
        heightCm = 151,
        weightKg = 43,
        extracurricular = "Musik Ansambel, Basket",
        studentNotes = "Memiliki kepekaan musikalitas yang baik dan aktif dalam tim olahraga."
    ),
    Student(
        id = 11,
        nis = "6011",
        nisn = "0136789012",
        fullName = "Hafizha Savira",
        nickname = "Hafizha",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2013-01-22",
        religion = "Islam",
        parentName = "Savira Wijaya",
        parentPhone = "628120011211",
        parentJob = "PNS",
        address = "Jl. Kyai Maja No. 50, Gunung",
        qrCodeId = "STD-6A-011",
        heightCm = 144,
        weightKg = 37,
        extracurricular = "Sains Club, Dokter Kecil",
        studentNotes = "Sangat tekun dalam menyelesaikan tugas harian dan rajin bertanya."
    ),
    Student(
        id = 12,
        nis = "6012",
        nisn = "0137890123",
        fullName = "Hanan Nabil Faeyza",
        nickname = "Hanan",
        gender = "L",
        birthPlace = "Semarang",
        birthDate = "2013-10-10",
        religion = "Islam",
        parentName = "Nabil Faeyza",
        parentPhone = "628131122312",
        parentJob = "Wiraswasta",
        address = "Jl. Melawai Raya No. 101, Melawai",
        qrCodeId = "STD-6A-012",
        heightCm = 148,
        weightKg = 40,
        extracurricular = "Catur, Robotik",
        studentNotes = "Kemampuan berhitung cepat dan daya ingat analitis sangat unggul."
    ),
    Student(
        id = 13,
        nis = "6013",
        nisn = "0138901234",
        fullName = "Hayana Ardilah",
        nickname = "Hayana",
        gender = "P",
        birthPlace = "Surakarta",
        birthDate = "2013-03-30",
        religion = "Islam",
        parentName = "Ardilah Santoso",
        parentPhone = "628142233413",
        parentJob = "Karyawan Swasta",
        address = "Jl. Pakubuwono VI No. 22, Kebayoran",
        qrCodeId = "STD-6A-013",
        heightCm = 143,
        weightKg = 36,
        extracurricular = "Karawitan, Pramuka",
        studentNotes = "Kerapian tulisan dan keteraturan buku catatan sangat patut diapresiasi."
    ),
    Student(
        id = 14,
        nis = "6014",
        nisn = "0139012345",
        fullName = "Kesya Nuraini Budiman",
        nickname = "Kesya",
        gender = "P",
        birthPlace = "Bandung",
        birthDate = "2013-08-25",
        religion = "Islam",
        parentName = "Budiman Syahputra",
        parentPhone = "628153344514",
        parentJob = "Pedagang",
        address = "Jl. Wijaya I No. 15, Petogogan",
        qrCodeId = "STD-6A-014",
        heightCm = 145,
        weightKg = 37,
        extracurricular = "Tari Kreasi, Dokter Kecil",
        studentNotes = "Sangat ramah, bersahabat dengan semua teman, dan gemar menolong."
    ),
    Student(
        id = 15,
        nis = "6015",
        nisn = "0130123456",
        fullName = "Marlian Gordeno",
        nickname = "Marlian",
        gender = "L",
        birthPlace = "Denpasar",
        birthDate = "2013-05-14",
        religion = "Hindu",
        parentName = "Gordeno Pratama",
        parentPhone = "628164455615",
        parentJob = "Desainer Grafis",
        address = "Jl. Brawijaya III No. 8, Pulo",
        qrCodeId = "STD-6A-015",
        heightCm = 149,
        weightKg = 41,
        extracurricular = "Prakarya, Robotik",
        studentNotes = "Memiliki minat tinggi di bidang teknologi komputer dan keterampilan tangan."
    ),
    Student(
        id = 16,
        nis = "6016",
        nisn = "0131234568",
        fullName = "Mirza Dwi Pratama",
        nickname = "Mirza",
        gender = "L",
        birthPlace = "Malang",
        birthDate = "2013-02-18",
        religion = "Islam",
        parentName = "Dwi Pratama",
        parentPhone = "628175566716",
        parentJob = "Karyawan Swasta",
        address = "Jl. Gandaria I No. 34, Kramat Pela",
        qrCodeId = "STD-6A-016",
        heightCm = 150,
        weightKg = 42,
        extracurricular = "Futsal, Pramuka",
        studentNotes = "Jiwa sportivitas dan kerjasama dalam tim kelompok sangat kuat."
    ),
    Student(
        id = 17,
        nis = "6017",
        nisn = "0132345679",
        fullName = "Muhammad Fairuz Athaya Nafiz",
        nickname = "Fairuz",
        gender = "L",
        birthPlace = "Jakarta",
        birthDate = "2013-09-09",
        religion = "Islam",
        parentName = "Athaya Nafiz",
        parentPhone = "628186677817",
        parentJob = "Advokat",
        address = "Jl. Lauser No. 18, Gunung",
        qrCodeId = "STD-6A-017",
        heightCm = 152,
        weightKg = 45,
        extracurricular = "Paskibra, Debat Cilik",
        studentNotes = "Memiliki kemampuan komunikasi persuasif dan selalu tampil percaya diri."
    ),
    Student(
        id = 18,
        nis = "6018",
        nisn = "0133456780",
        fullName = "Muhammad Rizki Fardhan",
        nickname = "Rizki",
        gender = "L",
        birthPlace = "Bekasi",
        birthDate = "2013-04-28",
        religion = "Islam",
        parentName = "Fardhan Maulana",
        parentPhone = "628197788918",
        parentJob = "Karyawan Swasta",
        address = "Jl. Sinabung No. 7, Rawa Barat",
        qrCodeId = "STD-6A-018",
        heightCm = 147,
        weightKg = 39,
        extracurricular = "Badminton, Pramuka",
        studentNotes = "Konsisten dalam ketertiban belajar dan selalu menjaga kebersihan kelas."
    ),
    Student(
        id = 19,
        nis = "6019",
        nisn = "0134567891",
        fullName = "Nadya Citra Khayla",
        nickname = "Nadya",
        gender = "P",
        birthPlace = "Tangerang",
        birthDate = "2013-11-03",
        religion = "Islam",
        parentName = "Khayla Nugraha",
        parentPhone = "628118899019",
        parentJob = "Dokter Gigi",
        address = "Jl. Cikajang No. 41, Petogogan",
        qrCodeId = "STD-6A-019",
        heightCm = 144,
        weightKg = 35,
        extracurricular = "Teater, Melukis",
        studentNotes = "Berbakat dalam seni peran drama dan fasih membaca puisi."
    ),
    Student(
        id = 20,
        nis = "6020",
        nisn = "0135678902",
        fullName = "Nur Humayroh Oktaviyani",
        nickname = "Humayroh",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2013-10-20",
        religion = "Islam",
        parentName = "Oktaviyanto",
        parentPhone = "628129900120",
        parentJob = "PNS",
        address = "Jl. Kerinci IX No. 3, Gunung",
        qrCodeId = "STD-6A-020",
        heightCm = 145,
        weightKg = 38,
        extracurricular = "Olimpiade Sains, Catur",
        studentNotes = "Memiliki dedikasi belajar tinggi dan selalu berprestasi di kelas."
    ),
    Student(
        id = 21,
        nis = "6021",
        nisn = "0136789013",
        fullName = "Nurul Annisa Ramadhani",
        nickname = "Nurul",
        gender = "P",
        birthPlace = "Depok",
        birthDate = "2013-07-16",
        religion = "Islam",
        parentName = "Ramadhan Santoso",
        parentPhone = "628130011221",
        parentJob = "Karyawan Swasta",
        address = "Jl. Bumi No. 28, Kebayoran",
        qrCodeId = "STD-6A-021",
        heightCm = 146,
        weightKg = 37,
        extracurricular = "Dokter Kecil, Pramuka",
        studentNotes = "Sangat terampil dalam menyusun laporan proyek dan teliti membaca grafik."
    ),
    Student(
        id = 22,
        nis = "6022",
        nisn = "3149211404",
        fullName = "Putra Affan Kamil",
        nickname = "Affan",
        gender = "L",
        birthPlace = "Jakarta",
        birthDate = "2014-09-21",
        religion = "Islam",
        parentName = "Kamil Hidayat",
        parentPhone = "628141122322",
        parentJob = "Dosen Teknik",
        address = "Jl. Daksa IV No. 11, Selong",
        qrCodeId = "STD-6A-022",
        heightCm = 149,
        weightKg = 41,
        extracurricular = "Robotik, Renang",
        studentNotes = "Memiliki daya kritis tajam dan rasa ingin tahu yang tinggi dalam sains."
    ),
    Student(
        id = 23,
        nis = "6023",
        nisn = "0138901236",
        fullName = "Rayyan Rifki Aidan",
        nickname = "Rayyan",
        gender = "L",
        birthPlace = "Palembang",
        birthDate = "2013-06-05",
        religion = "Islam",
        parentName = "Rifki Aidan",
        parentPhone = "628152233423",
        parentJob = "Wiraswasta",
        address = "Jl. Adityawarman No. 63, Melawai",
        qrCodeId = "STD-6A-023",
        heightCm = 154,
        weightKg = 46,
        extracurricular = "Paskibra, Basket",
        studentNotes = "Disiplin tinggi, selalu hadir lebih awal, dan aktif memimpin barisan."
    ),
    Student(
        id = 24,
        nis = "6024",
        nisn = "0139012347",
        fullName = "Rendra Zulfan Azhar Raihan",
        nickname = "Rendra",
        gender = "L",
        birthPlace = "Bandar Lampung",
        birthDate = "2013-12-14",
        religion = "Islam",
        parentName = "Zulfan Raihan",
        parentPhone = "628163344524",
        parentJob = "Karyawan BUMN",
        address = "Jl. Cisanggiri II No. 17, Petogogan",
        qrCodeId = "STD-6A-024",
        heightCm = 150,
        weightKg = 43,
        extracurricular = "Catur, Futsal",
        studentNotes = "Keterampilan mengolah data dan logika spasialnya sangat menonjol."
    ),
    Student(
        id = 25,
        nis = "6025",
        nisn = "0130123457",
        fullName = "Saskirana Ramadhani Ariyadi",
        nickname = "Saski",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2013-08-08",
        religion = "Islam",
        parentName = "Ariyadi Widodo",
        parentPhone = "628174455625",
        parentJob = "Wartawan",
        address = "Jl. Suryo No. 35, Rawa Barat",
        qrCodeId = "STD-6A-025",
        heightCm = 143,
        weightKg = 36,
        extracurricular = "English Club, Teater",
        studentNotes = "Mahir dalam bercerita lisan dan mengekspresikan ide gagasan kreatif."
    ),
    Student(
        id = 26,
        nis = "6026",
        nisn = "0131234569",
        fullName = "Sayyid Faqih Alatas",
        nickname = "Faqih",
        gender = "L",
        birthPlace = "Jakarta",
        birthDate = "2013-03-17",
        religion = "Islam",
        parentName = "Alatas Husein",
        parentPhone = "628185566726",
        parentJob = "Pedagang",
        address = "Jl. Senopati Dalam II No. 4, Senayan",
        qrCodeId = "STD-6A-026",
        heightCm = 148,
        weightKg = 40,
        extracurricular = "Tahfiz, Hadroh",
        studentNotes = "Sangat santun, rajin beribadah, dan suka membantu teman yang kesulitan."
    ),
    Student(
        id = 27,
        nis = "6027",
        nisn = "0132345680",
        fullName = "Sisy Sumirat",
        nickname = "Sisy",
        gender = "P",
        birthPlace = "Cirebon",
        birthDate = "2013-05-31",
        religion = "Islam",
        parentName = "Sumirat Wibowo",
        parentPhone = "628196677827",
        parentJob = "Karyawan Swasta",
        address = "Jl. Birah I No. 19, Rawa Barat",
        qrCodeId = "STD-6A-027",
        heightCm = 142,
        weightKg = 34,
        extracurricular = "Melukis, Tari Tradisional",
        studentNotes = "Memiliki kemampuan artistik menggambar yang istimewa dan rajin berlatih."
    ),
    Student(
        id = 28,
        nis = "6028",
        nisn = "0144175812",
        fullName = "Zahira Ramadhani",
        nickname = "Zahira",
        gender = "P",
        birthPlace = "Jakarta",
        birthDate = "2014-04-17",
        religion = "Islam",
        parentName = "Ramadhani Surya",
        parentPhone = "628117788928",
        parentJob = "PNS Guru",
        address = "Jl. Tulodong Bawah No. 5, Senayan",
        qrCodeId = "STD-6A-028",
        heightCm = 144,
        weightKg = 36,
        extracurricular = "Paduan Suara, Pramuka",
        studentNotes = "Unggul dalam menyimak dan memahami intisari bacaan teks sastra."
    ),
    Student(
        id = 29,
        nis = "6029",
        nisn = "3140258402",
        fullName = "Zahratul Latiffa",
        nickname = "Zahra",
        gender = "P",
        birthPlace = "Padang",
        birthDate = "2014-02-25",
        religion = "Islam",
        parentName = "Latif Hendrawan",
        parentPhone = "628128899029",
        parentJob = "Wiraswasta",
        address = "Jl. Kerinci Raya No. 40, Gunung",
        qrCodeId = "STD-6A-029",
        heightCm = 145,
        weightKg = 37,
        extracurricular = "Tari Kreasi, Dokter Kecil",
        studentNotes = "Selalu bersemangat dalam tugas kelompok dan menjaga kerukunan kelas."
    )
)
