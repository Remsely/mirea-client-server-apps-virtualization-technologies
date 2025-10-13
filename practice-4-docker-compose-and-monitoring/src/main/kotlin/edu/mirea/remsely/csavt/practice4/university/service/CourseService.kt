package edu.mirea.remsely.csavt.practice4.university.service

import edu.mirea.remsely.csavt.practice4.university.dto.request.CreateCourseRequest
import edu.mirea.remsely.csavt.practice4.university.dto.request.UpdateCourseRequest
import edu.mirea.remsely.csavt.practice4.university.dto.response.CourseResponse
import edu.mirea.remsely.csavt.practice4.university.entity.Course
import edu.mirea.remsely.csavt.practice4.university.mapper.toDto
import edu.mirea.remsely.csavt.practice4.university.mapper.toDtos
import edu.mirea.remsely.csavt.practice4.university.repository.CourseRepository
import edu.mirea.remsely.csavt.practice4.university.repository.ProfessorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CourseService(
    private val courseRepository: CourseRepository,
    private val professorRepository: ProfessorRepository
) {
    private val logger = LoggerFactory.getLogger(CourseService::class.java)

    @Transactional
    fun createCourse(request: CreateCourseRequest): CourseResponse {
        logger.info("Создание нового курса: name=${request.name}")

        if (courseRepository.findByName(request.name) != null) {
            logger.warn("Курс с названием '${request.name}' уже существует")
            throw IllegalArgumentException("Курс с таким названием уже существует")
        }

        val professors = if (request.professorIds.isNotEmpty()) {
            professorRepository.findAllById(request.professorIds).toMutableSet()
        } else {
            mutableSetOf()
        }

        if (professors.size != request.professorIds.size) {
            logger.error("Не все указанные преподаватели найдены")
            throw IllegalArgumentException("Один или несколько преподавателей не найдены")
        }

        val course = Course(
            name = request.name,
            description = request.description,
            credits = request.credits
        )

        professors.forEach { course.addProfessor(it) }

        val saved = courseRepository.save(course)
        logger.info("Курс успешно создан: id=${saved.id}")
        return saved.toDto()
    }

    @Transactional(readOnly = true)
    fun getAllCourses(): List<CourseResponse> {
        logger.info("Получение списка всех курсов")
        return courseRepository.findAll().toDtos()
    }

    @Transactional(readOnly = true)
    fun getCourseById(id: Long): CourseResponse {
        logger.info("Получение курса по ID=$id")
        val course = courseRepository.findById(id).orElseThrow {
            logger.error("Курс с ID=$id не найден")
            NoSuchElementException("Курс с ID=$id не найден")
        }
        return course.toDto()
    }

    @Transactional
    fun updateCourse(id: Long, request: UpdateCourseRequest): CourseResponse {
        logger.info("Обновление курса ID=$id")

        val course = courseRepository.findById(id).orElseThrow {
            logger.error("Курс с ID=$id не найден")
            NoSuchElementException("Курс с ID=$id не найден")
        }

        request.name?.let { newName ->
            if (newName != course.name) {
                courseRepository.findByName(newName)?.let {
                    logger.warn("Название '$newName' уже используется другим курсом")
                    throw IllegalArgumentException("Курс с таким названием уже существует")
                }
            }
            course.name = newName
        }

        request.description?.let { course.description = it }
        request.credits?.let { course.credits = it }

        request.professorIds?.let { ids ->
            val newProfessors = professorRepository.findAllById(ids).toMutableSet()
            if (newProfessors.size != ids.size) {
                logger.error("Не все указанные преподаватели найдены")
                throw IllegalArgumentException("Один или несколько преподавателей не найдены")
            }

            // Удаляем старые связи
            course.professors.toList().forEach { professor ->
                professor.courses.remove(course)
            }
            course.professors.clear()

            // Добавляем новые связи
            newProfessors.forEach { course.addProfessor(it) }
        }

        val saved = courseRepository.save(course)
        logger.info("Курс ID=$id успешно обновлён")
        return saved.toDto()
    }

    @Transactional
    fun deleteCourse(id: Long) {
        logger.info("Удаление курса ID=$id")
        if (!courseRepository.existsById(id)) {
            logger.error("Курс с ID=$id не найден")
            throw NoSuchElementException("Курс с ID=$id не найден")
        }
        courseRepository.deleteById(id)
        logger.info("Курс ID=$id успешно удалён")
    }

    @Transactional(readOnly = true)
    fun searchCourses(name: String): List<CourseResponse> {
        logger.info("Поиск курсов по названию: '$name'")
        return courseRepository.searchByName(name).toDtos()
    }
}
