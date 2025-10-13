package edu.mirea.remsely.csavt.practice4.university.service

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateProfessorRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateProfessorRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.ProfessorResponse
import edu.mirea.remsely.csavt.practice4.university.entity.Professor
import edu.mirea.remsely.csavt.practice4.university.mapper.toDto
import edu.mirea.remsely.csavt.practice4.university.mapper.toDtos
import edu.mirea.remsely.csavt.practice4.university.repository.ProfessorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessorService(
    private val professorRepository: ProfessorRepository
) {
    private val logger = LoggerFactory.getLogger(ProfessorService::class.java)

    @Transactional
    fun createProfessor(request: CreateProfessorRequest): ProfessorResponse {
        logger.info("Создание нового преподавателя: email=${request.email}")

        if (professorRepository.findByEmail(request.email) != null) {
            logger.warn("Преподаватель с email ${request.email} уже существует")
            throw IllegalArgumentException("Преподаватель с таким email уже существует")
        }

        val professor = Professor(
            firstName = request.firstName,
            lastName = request.lastName,
            email = request.email,
            department = request.department,
            academicTitle = request.academicTitle
        )

        val saved = professorRepository.save(professor)
        logger.info("Преподаватель успешно создан: id=${saved.id}")
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    fun getAllProfessors(): List<ProfessorResponse> {
        logger.info("Получение списка всех преподавателей")
        return professorRepository.findAll().toDtos()
    }

    @Transactional(readOnly = true)
    fun getProfessorById(id: Long): ProfessorResponse {
        logger.info("Получение преподавателя по ID=$id")
        val professor = professorRepository.findById(id).orElseThrow {
            logger.error("Преподаватель с ID=$id не найден")
            NoSuchElementException("Преподаватель с ID=$id не найден")
        }
        return professor.toDto()
    }

    @Transactional
    fun updateProfessor(id: Long, request: UpdateProfessorRequest): ProfessorResponse {
        logger.info("Обновление преподавателя ID=$id")

        val professor = professorRepository.findById(id).orElseThrow {
            logger.error("Преподаватель с ID=$id не найден")
            NoSuchElementException("Преподаватель с ID=$id не найден")
        }

        request.email?.let { newEmail ->
            if (newEmail != professor.email) {
                professorRepository.findByEmail(newEmail)?.let {
                    logger.warn("Email $newEmail уже используется другим преподавателем")
                    throw IllegalArgumentException("Email уже используется")
                }
            }
            professor.email = newEmail
        }

        request.firstName?.let { professor.firstName = it }
        request.lastName?.let { professor.lastName = it }
        request.department?.let { professor.department = it }
        request.academicTitle?.let { professor.academicTitle = it }

        val saved = professorRepository.save(professor)
        logger.info("Преподаватель ID=$id успешно обновлён")
        return saved.toDto()
    }

    @Transactional
    fun deleteProfessor(id: Long) {
        logger.info("Удаление преподавателя ID=$id")
        if (!professorRepository.existsById(id)) {
            logger.error("Преподаватель с ID=$id не найден")
            throw NoSuchElementException("Преподаватель с ID=$id не найден")
        }
        professorRepository.deleteById(id)
        logger.info("Преподаватель ID=$id успешно удалён")
    }

    @Transactional(readOnly = true)
    fun getProfessorsByDepartment(department: String): List<ProfessorResponse> {
        logger.info("Получение преподавателей кафедры: $department")
        return professorRepository.findByDepartment(department).toDtos()
    }
}
