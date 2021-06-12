package it.dip.service;

import it.dip.forms.LoginForm;
import it.dip.forms.RegForm;

public interface ClientService {
    boolean login(LoginForm loginForm);

    boolean register(RegForm regForm);
}
