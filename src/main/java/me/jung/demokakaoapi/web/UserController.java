package me.jung.demokakaoapi.web;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import me.jung.demokakaoapi.advice.exception.CUserNotFoundException;
import me.jung.demokakaoapi.domain.CommonResult;
import me.jung.demokakaoapi.domain.ListResult;
import me.jung.demokakaoapi.domain.SingleResult;
import me.jung.demokakaoapi.domain.users.Users;
import me.jung.demokakaoapi.domain.users.UsersRepository;
import me.jung.demokakaoapi.service.ResponseService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"2. User"})
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UsersRepository usersRepository;
    private final ResponseService responseService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 조회", notes = "모든 회원을 조회한다")
    @GetMapping("/users")
    public ListResult<Users> findAllUser() {
        // 결과데이터가 여러건인경우 getListResult를 이용해서 결과를 출력한다.
        return responseService.getListResult(usersRepository.findAll());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 단건 조회 ", notes = "userId로 회원을 조회")
    @GetMapping(value = "/users/{userId}")
    public SingleResult<Users> findUserById(@ApiParam(value = "유저 PK 값", required = true) @PathVariable long userId,
                                            @ApiParam(value = "언어", defaultValue = "ko") @RequestParam String lang) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return responseService.getSingleResult(usersRepository.findByEmail(email).orElseThrow(CUserNotFoundException::new));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 입력", notes = "회원을 입력한다")
    @PostMapping(value = "/user")
    public SingleResult<Users> save(@ApiParam(value = "회원이메일", required = true) @RequestParam String email,
                                    @ApiParam(value = "회원패스워드", required = true) @RequestParam String password) {
        Users user = Users.builder()
                .email(email)
                .password(password)
                .build();
        return responseService.getSingleResult(usersRepository.save(user));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다.")
    @PutMapping("/users")
    public SingleResult<Users> modify(@ApiParam(value = "유저 pk값", required = true) @RequestParam long userId,
                                      @ApiParam(value = "이메일", required = true) @RequestParam String email,
                                      @ApiParam(value = "패스워드", required = true) @RequestParam String password) {

        Users users = Users.builder().id(userId).email(email).password(password).build();
        return responseService.getSingleResult(usersRepository.save(users));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @ApiOperation(value = "회원 삭제", notes = "userId로 회원정보를 삭제")
    @DeleteMapping("/users/{userId}")
    public CommonResult delete(@ApiParam(value = "유저 pk값", required = true) @PathVariable long userId) {
        usersRepository.deleteById(userId);
        return responseService.getSuccessResult();
    }
}
