package com.finn.service.impl

import com.finn.dto.UserDto
import com.finn.entity.User
import com.finn.exception.ConflictException
import com.finn.exception.NotFoundException
import com.finn.repository.UserRepository
import com.finn.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.finn.mapper.toDto
import com.finn.mapper.toEntity

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    @Transactional(readOnly = true)
    override fun getUser(id: String): UserDto {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("User not found") }
        return user.toDto()
    }

    @Transactional
    override fun createUser(dto: UserDto): UserDto {
        if (userRepository.existsById(dto.id)) throw ConflictException("User already exists")
        val saved = userRepository.save(dto.toEntity())
        return saved.toDto()
    }

    @Transactional
    override fun updateUser(id: String, name: String) {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("User not found") }
        user.name = name
        userRepository.save(user)
    }

    @Transactional
    override fun deleteUser(id: String) {
        userRepository.deleteById(id)
    }

    private fun User.toDto() = UserDto(id = id, name = name, photo = photo)
}
