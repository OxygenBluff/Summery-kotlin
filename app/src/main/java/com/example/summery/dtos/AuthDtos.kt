//authentication request
data class LoginRequestDTO(
    val email: String,
    val password: String
)


//RegisterRequest springBoot match
data class RegisterRequestDTO(
    val prenom: String,
    val nom: String,
    val email: String,
    val password: String,
    val role: String = "CUSTOMER"
    //sprong maps it to ENUM right ? RIGHT ?
)

data class AuthResponseDTO(
    val accessToken :String,
    val refreshToken: String
)

