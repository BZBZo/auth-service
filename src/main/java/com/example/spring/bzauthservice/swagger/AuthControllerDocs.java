package com.example.spring.bzauthservice.swagger;

import com.example.spring.bzauthservice.dto.TokenRefreshRequestDTO;
import com.example.spring.bzauthservice.dto.RefreshTokenClientResponseDTO;
import com.example.spring.bzauthservice.dto.StatusResponseDto;
import com.example.spring.bzauthservice.dto.TokenResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "token API", description = "token 관련 API")
public interface AuthControllerDocs {

//    @Operation(
//            summary = "Get Token",
//            description = "클라이언트가 쿠키에 credentials(Authorization)를 담아 보낸 요청에서 토큰을 반환합니다."
//    )
//    @Parameter(
//            name = "Authorization",
//            description = "클라이언트 쿠키에 담긴 인증 토큰",
//            required = true,
//            in = ParameterIn.COOKIE
//    )
//    public ResponseEntity<?> getToken(HttpServletRequest request);


    @Operation(
            summary = "Logout",
            description = "클라이언트가 Authorization 헤더에 액세스 토큰을 담아 보낸 요청을 통해 로그아웃 처리. Redis에서 관련 정보를 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그아웃 성공",
                            content = @Content(schema = @Schema(implementation = StatusResponseDto.class))
                    )
            }
    )
    @Parameter(
            name = "Authorization",
            description = "클라이언트 헤더에 담긴 액세스 토큰",
            required = true,
            in = ParameterIn.HEADER
    )
    public ResponseEntity<StatusResponseDto> logout(@RequestHeader("Authorization") final String accessToken);



    @Operation(
            summary = "Refresh Token",
            description = "클라이언트가 Authorization 헤더에 액세스 토큰을 담아 보낸 요청을 통해 새로운 액세스 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "새로운 액세스 토큰 발급 성공",
                            content = @Content(schema = @Schema(implementation = TokenResponseStatus.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - 요청이 유효하지 않음",
                            content = @Content(schema = @Schema(implementation = TokenResponseStatus.class))
                    )
            }
    )
    @Parameter(
            name = "Authorization",
            description = "클라이언트 헤더에 담긴 액세스 토큰",
            required = true,
            in = ParameterIn.HEADER
    )
    public RefreshTokenClientResponseDTO refreshToken(@RequestBody TokenRefreshRequestDTO tokenRefreshRequestDTO);
}
