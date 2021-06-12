package it.dip.service;

import it.dip.forms.LoginForm;
import it.dip.forms.RegForm;
import it.dip.models.Client;
import it.dip.repositories.ClientRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientRep clientRep;

    @Override
    public boolean login(LoginForm loginForm) {
        Optional<Client> client = clientRep.getClientByLogin(loginForm.getLogin());
        if (client.isPresent() && passwordEncoder.matches(loginForm.getPassword(), client.get().getHashPas())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean register(RegForm regForm) {
        Optional<Client> client = clientRep.getClientByLogin(regForm.getLogin());
        if (!client.isPresent()) {
            clientRep.save(Client.builder()
                    .login(regForm.getLogin())
                    .hashPas(passwordEncoder.encode(regForm.getPassword()))
                    .build());
            return true;
        }
        return false;
    }
}
