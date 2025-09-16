package com.finn.controller

import com.finn.dto.UserDto
import com.finn.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<UserDto> =
        try { ResponseEntity.ok(userService.getUser(id)) } catch (ex: Exception) { ResponseEntity.notFound().build() }

    @PostMapping
    fun create(@Valid @RequestBody dto: UserDto): ResponseEntity<UserDto> =
        ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto))

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody dto: UserDto): ResponseEntity<Void> {
        userService.updateUser(id, dto.name)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }
}
