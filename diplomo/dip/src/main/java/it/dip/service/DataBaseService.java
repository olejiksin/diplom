package it.dip.service;

import it.dip.dto.DataBaseDTO;
import it.dip.dto.DataBaseDTOtoFt;
import it.dip.models.DataBase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataBaseService {
    List<DataBaseDTOtoFt> getAllDbByLogin(String login);

    String addNew(DataBaseDTO dataBaseDTO, String login, MultipartFile file);

    boolean startStopDb(String id);

    List<DataBase> getDataBaseList(String login);

    boolean deleteDataBase(String id);
}
