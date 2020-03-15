package me.jung.demokakaoapi.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import me.jung.demokakaoapi.advice.exception.CEmailSigninFailedException;
import me.jung.demokakaoapi.advice.exception.CUserExistException;
import me.jung.demokakaoapi.advice.exception.CUserNotFoundException;
import me.jung.demokakaoapi.config.security.JwtTokenProvider;
import me.jung.demokakaoapi.domain.CommonResult;
import me.jung.demokakaoapi.domain.SingleResult;
import me.jung.demokakaoapi.domain.users.Users;
import me.jung.demokakaoapi.domain.users.UsersRepository;
import me.jung.demokakaoapi.service.ResponseService;
import me.jung.demokakaoapi.service.social.KakaoService;
import me.jung.demokakaoapi.web.DTO.KakaoProfile;
import me.jung.demokakaoapi.web.DTO.LoginRequestDTO;
import me.jung.demokakaoapi.web.DTO.SignupRequestDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@Api(tags = {"1. Sign"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SignController {

    private final UsersRepository usersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ResponseService responseService;
    private final PasswordEncoder passwordEncoder;
    private final KakaoService kakaoService;

    @ApiOperation(value = "로그인", notes = "이메일 회원 로그인을 한다")
    @PostMapping(value = "/signin")
    public SingleResult<String> signin(@ApiParam(value = "로그인 정보", required = true) @RequestBody LoginRequestDTO loginRequestDTO) {
        Users users = usersRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(CEmailSigninFailedException::new);
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), users.getPassword()))
            throw new CEmailSigninFailedException();
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(users.getId()), users.getRoles()));
    }

    @ApiOperation(value = "가입", notes = "회원가입을 한다.")
    @PostMapping(value = "/signup")
    public CommonResult signup(@ApiParam(value = "회원가입 계정 정보", required = true) @RequestBody SignupRequestDTO signupRequestDTO,
                               BindingResult bindingResult) {
        Optional<Users> user = usersRepository.findByEmail(signupRequestDTO.getEmail());
        if (user.isPresent()) {
            throw new CUserExistException();
        }
        usersRepository.save(Users.builder().password(passwordEncoder.encode(signupRequestDTO.getPassword()))
                .email(signupRequestDTO.getEmail())
                .name(signupRequestDTO.getName())
                .roles(Collections.singletonList("ROLE_USER"))
                .build());
        return responseService.getSuccessResult();
    }

    @ApiOperation(value = "소셜 로그인", notes = "소셜 회원 로그인을 한다.")
    @PostMapping(value = "/signin/{provider}")
    public SingleResult<String> signinByProvider(
            @ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
            @ApiParam(value = "소셜 access_token", required = true) @RequestParam String accessToken) {
        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        Users user = usersRepository.findByIdAndProvider(String.valueOf(profile.getId()), provider).orElseThrow(CUserNotFoundException::new);
        return responseService.getSingleResult(jwtTokenProvider.createToken(String.valueOf(user.getId()), user.getRoles()));
    }

    @ApiOperation(value = "소셜 계정 가입", notes = "소셜 계정 회원가입을 한다")
    @PostMapping(value = "/signup/{provider}")
    public CommonResult signupProvider(
            @ApiParam(value = "서비스 제공자 provider", required = true, defaultValue = "kakao") @PathVariable String provider,
            @ApiParam(value = "소셜 accessToken", required = true) @RequestParam String accessToken,
            @ApiParam(value = "이름", required = true) @RequestParam String name
    ) {
        KakaoProfile profile = kakaoService.getKakaoProfile(accessToken);
        Optional<Users> user = usersRepository.findByIdAndProvider(String.valueOf(profile.getId()), provider);
        if (user.isPresent())
            throw new CUserExistException();
        usersRepository.save(
                Users.builder()
                        .email(String.valueOf(profile.getId()))
                        .provider(provider)
                        .name(name)
                        .roles(Collections.singletonList("ROLE_USER"))
                        .build());
        return responseService.getSuccessResult();
    }

}
