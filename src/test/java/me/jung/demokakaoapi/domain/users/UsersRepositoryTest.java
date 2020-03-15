package me.jung.demokakaoapi.domain.users;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@DataJpaTest
public class UsersRepositoryTest {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @After
    public void cleanup() {
        usersRepository.deleteAll();
    }

    @Test
    public void email로찾고해당유저반환() {
        //given
        String email = "jwjung5038@gmail.com";
        String name = "진우";

        usersRepository.save(Users.builder().email(email).name(name)
                .password(passwordEncoder.encode("!qwer1962"))
                .roles(Collections.singletonList("ROLE_USER")).build());

        //when
        Optional<Users> user = usersRepository.findByEmail(email);
        //then
        assertNotNull(user);
        assertTrue(user.isPresent());
        assertEquals(user.get().getName(), name);
        assertThat(user.get().getName()).isEqualTo(name);

    }

    @Test
    public void 회원가입() {
        //given
        String email = "jung@goand.com";
        String password = "!qwer1962";
        String name = "진우";
        usersRepository.save(Users
                .builder()
                .email(email).password(passwordEncoder.encode(password))
                .name(name)
                .build());
        //when
        List<Users> usersList = usersRepository.findAll();
        //then
        Users users = usersList.get(0);
        assertThat(users.getEmail()).isEqualTo(email);
//        assertThat(users.getPassword()).isEqualTo(passwordEncoder.encode(password));

    }
}