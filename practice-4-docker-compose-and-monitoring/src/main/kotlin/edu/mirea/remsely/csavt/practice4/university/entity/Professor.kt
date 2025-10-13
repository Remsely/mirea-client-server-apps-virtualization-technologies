package edu.mirea.remsely.csavt.practice4.university.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "professors")
class Professor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var department: String,

    @Column(name = "academic_title")
    var academicTitle: String? = null,

    @ManyToMany(mappedBy = "professors", fetch = FetchType.LAZY)
    val courses: MutableSet<Course> = mutableSetOf()
) {
    val fullName get() = "$firstName $lastName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Professor) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun toString(): String {
        return "Professor(id=$id, firstName='$firstName', lastName='$lastName', email='$email')"
    }
}
