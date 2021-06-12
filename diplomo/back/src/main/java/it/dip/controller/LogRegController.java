package it.dip.controller;

import it.dip.forms.LoginForm;
import it.dip.forms.RegForm;
import it.dip.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LogRegController {

    @Autowired
    private ClientService clientService;



    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<Object> login(@RequestBody LoginForm loginForm) {
        if (clientService.login(loginForm)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    public ResponseEntity<Object> regist(@RequestBody RegForm regForm) {
        if (clientService.register(regForm)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }

}
