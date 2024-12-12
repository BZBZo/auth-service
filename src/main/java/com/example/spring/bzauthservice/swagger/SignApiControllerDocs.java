package com.example.spring.bzauthservice.swagger;

import com.example.spring.bzauthservice.dto.DuplicateResponseDTO;
import com.example.spring.bzauthservice.dto.JoinResponseDTO;
import com.example.spring.bzauthservice.dto.SecurityUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "join API", description = "회원가입 및 중복체크 API")
public interface SignApiControllerDocs {

    @Operation(
            summary = "회원가입",
            description = "회원가입 요청을 처리하고 성공 시 로그인 페이지 URL을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = JoinResponseDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = JoinResponseDTO.class))
                    )
            }
    )
    ResponseEntity<JoinResponseDTO> join(@RequestBody SecurityUserDto securityUserDto);

    @Operation(
            summary = "사업자 번호 중복 체크",
            description = "사업자 번호가 이미 가입되어 있는지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "요청 처리 성공",
                            content = @Content(schema = @Schema(implementation = DuplicateResponseDTO.class))
                    )
            }
    )
    ResponseEntity<?> checkBusinessNumber(@Valid @RequestParam String businessNumber);

    @Operation(
            summary = "닉네임 중복 체크",
            description = "닉네임이 이미 존재하는지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "요청 처리 성공",
                            content = @Content(schema = @Schema(implementation = DuplicateResponseDTO.class))
                    )
            }
    )
    ResponseEntity<?> checkNickname(@Valid @RequestParam String nickname);

    @Operation(
            summary = "판매자 전화번호 중복 체크",
            description = "판매자 전화번호가 이미 가입되어 있는지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "요청 처리 성공",
                            content = @Content(schema = @Schema(implementation = DuplicateResponseDTO.class))
                    )
            }
    )
    ResponseEntity<?> checkSellerPhone(@Valid @RequestParam String sellerPhone);

    @Operation(
            summary = "구매자 전화번호 중복 체크",
            description = "구매자 전화번호가 이미 가입되어 있는지 확인합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "202",
                            description = "요청 처리 성공",
                            content = @Content(schema = @Schema(implementation = DuplicateResponseDTO.class))
                    )
            }
    )
    ResponseEntity<?> checkCustomerPhone(@Valid @RequestParam String customerPhone);
}

