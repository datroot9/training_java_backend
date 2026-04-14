package com.example.studentmangerment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication result for register (no token) or login (with JWT).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    /** User id when returned after login; may be omitted after register. */
    private Integer id;
    /** Authenticated username. */
    private String username;
    /** JWT bearer token; present after successful login only. */
    private String token;
    /** Role name (e.g. {@code USER}, {@code ADMIN}). */
    private String role;
}
