package edu.mirea.remsely.csavt.practice4.university.service

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateStudentRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateStudentRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.StudentResponse
import edu.mirea.remsely.csavt.practice4.university.entity.Student
import edu.mirea.remsely.csavt.practice4.university.mapper.toDto
import edu.mirea.remsely.csavt.practice4.university.mapper.toDtos
import edu.mirea.remsely.csavt.practice4.university.repository.CourseRepository
import edu.mirea.remsely.csavt.practice4.university.repository.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val courseRepository: CourseRepository
) {
    private val logger = LoggerFactory.getLogger(StudentService::class.java)

    @Transactional
    fun createStudent(request: CreateStudentRequest): StudentResponse {
        logger.info("Создание нового студента: email=${request.email}, studentNumber=${request.studentNumber}")

        if (studentRepository.findByEmail(request.email) != null) {
            logger.warn("Попытка создать студента с существующим email: ${request.email}")
            throw IllegalArgumentException("Студент с таким email уже существует")
        }

        if (studentRepository.findByStudentNumber(request.studentNumber) != null) {
            logger.warn("Попытка создать студента с существующим номером билета: ${request.studentNumber}")
            throw IllegalArgumentException("Студент с таким номером студенческого билета уже существует")
        }

        val course = request.courseId?.let { courseId ->
            courseRepository.findById(courseId).orElseThrow {
                logger.error("Курс с ID=$courseId не найден")
                IllegalArgumentException("Курс с ID=$courseId не найден")
            }
        }

        val student = Student(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            studentNumber = request.studentNumber,
            course = course
        )

        val savedStudent = studentRepository.save(student)
        logger.info("Студент успешно создан: id=${savedStudent.id}")

        return savedStudent.toDto()
    }

    @Transactional(readOnly = true)
    fun getAllStudents(): List<StudentResponse> {
        logger.info("Получение списка всех студентов")
        return studentRepository.findAll().map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun getStudentById(id: Long): StudentResponse {
        logger.info("Получение студента по ID=$id")
        val student = studentRepository.findById(id).orElseThrow {
            logger.error("Студент с ID=$id не найден")
            NoSuchElementException("Студент с ID=$id не найден")
        }
        return student.toDto()
    }

    @Transactional
    fun updateStudent(id: Long, request: UpdateStudentRequest): StudentResponse {
        logger.info("Обновление студента ID=$id")

        val student = studentRepository.findById(id).orElseThrow {
            logger.error("Студент с ID=$id не найден")
            NoSuchElementException("Студент с ID=$id не найден")
        }

        request.email?.let { newEmail ->
            if (newEmail != student.email) {
                studentRepository.findByEmail(newEmail)?.let {
                    logger.warn("Email $newEmail уже используется другим студентом")
                    throw IllegalArgumentException("Email уже используется")
                }
            }
            student.email = newEmail
        }

        request.studentNumber?.let { newNumber ->
            if (newNumber != student.studentNumber) {
                studentRepository.findByStudentNumber(newNumber)?.let {
                    logger.warn("Номер $newNumber уже используется другим студентом")
                    throw IllegalArgumentException("Номер студенческого билета уже используется")
                }
            }
            student.studentNumber = newNumber
        }

        request.firstName?.let { student.firstName = it }
        request.lastName?.let { student.lastName = it }

        request.courseId?.let { courseId ->
            val newCourse = courseRepository.findById(courseId).orElseThrow {
                logger.error("Курс с ID=$courseId не найден")
                IllegalArgumentException("Курс с ID=$courseId не найден")
            }
            student.course = newCourse
        }

        val saved = studentRepository.save(student)
        logger.info("Студент ID=$id успешно обновлён")
        return saved.toDto()
    }

    @Transactional
    fun deleteStudent(id: Long) {
        logger.info("Удаление студента ID=$id")
        if (!studentRepository.existsById(id)) {
            logger.error("Студент с ID=$id не найден")
            throw NoSuchElementException("Студент с ID=$id не найден")
        }
        studentRepository.deleteById(id)
        logger.info("Студент ID=$id успешно удалён")
    }

    @Transactional(readOnly = true)
    fun getStudentsByCourse(courseId: Long): List<StudentResponse> {
        logger.info("Получение студентов курса ID=$courseId")
        return studentRepository.findByCourseId(courseId).toDtos()
    }
}
