package it.dip.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.dip.dto.DataBaseDTO;
import it.dip.dto.FromFrontAppDTO;
import it.dip.dto.MainDTO;
import it.dip.dto.MicroserviceAppDTO;
import it.dip.models.Microservice;
import it.dip.service.AppService;
import it.dip.service.DataBaseService;
import javafx.beans.binding.ObjectExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;


@RestController
public class AppController {
    @Autowired
    private AppService appService;
    @Autowired
    private DataBaseService dataBaseService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/main/{login}", method = RequestMethod.GET)
    public MainDTO loadMainPage(@PathVariable("login") String emailOrLogin) {
        return MainDTO.builder()
                .apps(appService.getAllAppsByLoginOrEmail(emailOrLogin))
                .dataBases(dataBaseService.getAllDbByLogin(emailOrLogin))
                .microservices(appService.getAllMicroservicesByLogin(emailOrLogin))
                .build();
    }

    @RequestMapping(value = "/newApp", method = RequestMethod.POST)
    public ResponseEntity<Object> addNewApp(@RequestHeader("user") String loginOrEmail, @RequestBody FromFrontAppDTO appDTO) {
        String re = "";
        re = appService.addNewAppMono(appDTO, loginOrEmail);
        if (re.substring(0, 2).equals("ok")) return ResponseEntity.ok().body(re.substring(2));
        return ResponseEntity.badRequest().body(re);
    }

    @RequestMapping(value = "/deleteApp/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteApp(@PathVariable("id") String id) {
        if (appService.deleteApp(id)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/startOrStop/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> startApp(@PathVariable("id") String id) {
        if (appService.startOrTurnOff(id)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/newDb", method = RequestMethod.POST)
    public ResponseEntity<Object> addNewDataBase(@RequestHeader("login") String login, @RequestParam("file") MultipartFile file, @RequestParam("db") String data) throws IOException {
        DataBaseDTO baseDTO = objectMapper.readValue(data, DataBaseDTO.class);
        String re = dataBaseService.addNew(baseDTO, login, file);
        if (re.equals("Fail") || re.equals("Error while install")) return ResponseEntity.badRequest().body(re);
        return ResponseEntity.ok().body(re);
    }

    @RequestMapping(value = "/deleteDB/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteDb(@PathVariable("id") String id) {
        if (dataBaseService.deleteDataBase(id)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/startOrStopDB/{id}", method = RequestMethod.POST)
    public ResponseEntity<Object> startStopDb(@PathVariable("id") String id) {
        if (dataBaseService.startStopDb(id)) return ResponseEntity.ok().build();
        return ResponseEntity.badRequest().build();
    }

    @RequestMapping(value = "/newMicro", method = RequestMethod.POST)
    public ResponseEntity<Object> addNewMicroApp(@RequestHeader("login") String login, @RequestBody MicroserviceAppDTO appDTO) {
        Object re = appService.addNewAppMicro(appDTO, login);
        if (re.getClass().equals(Microservice.class)) return ResponseEntity.ok().body(re);
        return ResponseEntity.badRequest().body(re);
    }
}
