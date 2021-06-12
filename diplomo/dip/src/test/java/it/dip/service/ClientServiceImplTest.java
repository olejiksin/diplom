package it.dip.service;

import it.dip.forms.LoginForm;
import it.dip.forms.RegForm;
import it.dip.models.Client;
import it.dip.repositories.ClientRep;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ClientServiceImplTest {
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRep clientRep;

    private void cleanUp() {
        Optional<Client> client = clientRep.getClientByLogin("oleg");
        client.ifPresent(value -> clientRep.delete(value));
    }

    @Test
    public void register() {
        RegForm regForm = RegForm.builder()
                .login("oleg")
                .password("oleg")
                .build();
        Assert.assertTrue(clientService.register(regForm));
    }
    @Test
    public void login() {
        LoginForm loginForm = LoginForm.builder()
                .login("oleg")
                .password("oleg")
                .build();
        Assert.assertTrue(clientService.login(loginForm));
        cleanUp();
    }
}