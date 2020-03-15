package me.jung.demokakaoapi.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jung.demokakaoapi.domain.users.Users;
import me.jung.demokakaoapi.domain.users.UsersRepository;
import me.jung.demokakaoapi.web.DTO.LoginRequestDTO;
import me.jung.demokakaoapi.web.DTO.SignupRequestDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SignControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void signin() throws Exception {
        //given
        Users users = Users.builder().email("jwjung5038@gmail.com").password(passwordEncoder.encode("1234"))
                .name("진우").roles(Collections.singletonList("ROLE_USER")).build();
        usersRepository.save(users);

        //given
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("jwjung5038@gmail.com");
        loginRequestDTO.setPassword("1234");
        String dtoAsString = objectMapper.writeValueAsString(loginRequestDTO);
        //when
        mockMvc.perform(post("/api/signin").contentType(MediaType.APPLICATION_JSON).content(dtoAsString))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.data").exists());
        //then

    }

    @Test
    public void 회원가입() throws Exception {
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO();
        signupRequestDTO.setEmail("jwjung5038@gmail.com");
        signupRequestDTO.setPassword("1234");
        signupRequestDTO.setName("진우");

        String dtoAsString = objectMapper.writeValueAsString(signupRequestDTO);
        mockMvc.perform(post("/api/signup").content(dtoAsString)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").exists());
    }

    @Test
    @WithMockUser(username = "mockUser", roles = {"ADMIN"})
    public void 접근권한_거부() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/exception/accessdenied"));
    }

}