package it.dip.service;

import it.dip.dto.DataBaseDTO;
import it.dip.dto.DataBaseDTOtoFt;
import it.dip.models.Client;
import it.dip.models.DataBase;
import it.dip.models.Server;
import it.dip.repositories.ClientRep;
import it.dip.repositories.DataBaseRep;
import it.dip.repositories.ServerRep;
import it.dip.utils.AES;
import it.dip.utils.DockerControl;
import it.dip.utils.SshMaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataBaseServiceImpl implements DataBaseService {
    @Autowired
    private DataBaseRep dataBaseRep;
    @Autowired
    private ClientRep clientRep;
    @Autowired
    private DockerControl dockerControl;
    @Autowired
    private ServerRep serverRep;

    @Override
    public List<DataBaseDTOtoFt> getAllDbByLogin(String login) {
        Optional<Client> client = clientRep.getClientByLogin(login);
        List<DataBaseDTOtoFt> list = new ArrayList<>();
        if (client.isPresent()) {
            for (DataBase db : dataBaseRep.getAllByClientsId(client.get().get_id())) {
                list.add(
                        DataBaseDTOtoFt.builder()
                                .dbType(db.getDbType())
                                .ip(db.getIp())
                                .port(db.getPort())
                                .status(db.getStatus())
                                .build()
                );
            }
        }
        return list;
    }

    @Override
    public String addNew(DataBaseDTO dataBaseDTO, String login, MultipartFile file) {
        Optional<Client> client = clientRep.getClientByLogin(login);
        if (client.isPresent()) {
            String dir = System.getProperty("user.dir") + "/projects/newDb_" + login;
            File path = new File(dir);
            path.mkdirs();
            Path fileNameAndPath = Paths.get(dir, file.getOriginalFilename());
            File serverFile = new File(String.valueOf(fileNameAndPath));
            BufferedOutputStream stream;
            try {
                stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                stream.write(file.getBytes());
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error downloading file";
            }
            DataBase dataBase = DataBase.builder()
                    .clientsId(client.get().get_id())
                    .dbType(dataBaseDTO.getDbType())
                    .ip(dataBaseDTO.getIp())
                    .port(dataBaseDTO.getPort())
                    .serverType(dataBaseDTO.getServerType())
                    .status("online")
                    .name(dataBaseDTO.getName())
                    .build();
            if (dataBaseDTO.getServerType().equals("own")) {

                SshMaker sshMaker = new SshMaker(dataBaseDTO.getUser(), dataBaseDTO.getPas(), dataBaseDTO.getIp());
                dockerControl.setSshMakerAndServerType(sshMaker, "own");
                boolean flag = dockerControl.installDB(dataBaseDTO.getDbType(), dataBaseDTO.getPort(), dataBaseDTO.getName(), login);
                if (flag) {
                    dataBaseRep.save(dataBase);
                    Server server = Server.builder()
                            .password(dataBaseDTO.getPas())
                            .login(dataBaseDTO.getUser())
                            .ip(dataBaseDTO.getIp())
                            .appsId(dataBaseRep.getByNameAndClientsId(dataBaseDTO.getName(), client.get().get_id()).get().get_id())
                            .build();
                    if (server.getLogin() != null) {
                        server.setPassword(AES.encrypt(dataBaseDTO.getPas(), dataBaseDTO.getUser() + "_" + dataBaseDTO.getName()));
                        serverRep.save(server);
                    }
                    return dataBaseRep.getByNameAndClientsId(dataBase.getName(), client.get().get_id()).get().get_id();
                } else return "Error while install";
            } else if (dataBaseDTO.getServerType().equals("company")) {
                dockerControl.setSshMakerAndServerType(null, "company");
                boolean flag = dockerControl.installDB(dataBaseDTO.getDbType(), dataBaseDTO.getPort(), dataBaseDTO.getName(), login);
                if (flag) {
                    dataBaseRep.save(dataBase);
                    return dataBaseRep.getByNameAndClientsId(dataBase.getName(), client.get().get_id()).get().get_id();
                } else return "Error while install";
            }
        }
        return "Fail";
    }

    private void setSshForControl(Optional<DataBase> app) {
        Optional<Server> server = serverRep.getServerByAppsId(app.get().get_id());
        SshMaker sshMaker = new SshMaker(server.get().getLogin(),
                AES.decrypt(server.get().getPassword(), server.get().getLogin() + "_" + app.get().getName()),
                server.get().getIp());
        dockerControl.setSshMakerAndServerType(sshMaker, "own");
    }

    @Override
    public boolean startStopDb(String id) {
        Optional<DataBase> db = dataBaseRep.getBy_id(id);
        if (db.isPresent() ) {
            if (db.get().getServerType().equals("own")) {
                setSshForControl(db);
            } else {
                dockerControl.setSshMakerAndServerType(null, "company");
            }
            if (db.get().getStatus().equals("offline")) {
                dockerControl.setAppName(db.get().getName(), db.get().get_id(),"db");
                db.get().setStatus("online");
                dataBaseRep.save(db.get());
                dockerControl.startContainer();
                return true;
            } else if (db.get().getStatus().equals("online")) {
                dockerControl.setAppName(db.get().getName(), db.get().get_id(),"db");
                db.get().setStatus("offline");
                dataBaseRep.save(db.get());
                dockerControl.stopContainer();
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean deleteDataBase(String id) {
        Optional<DataBase> db = dataBaseRep.getBy_id(id);
        if (db.isPresent()) {
            Optional<Client> client = clientRep.getClientBy_id(db.get().getClientsId());
            String containerName = db.get().getName() + "_" + client.get().getLogin();
            if (db.get().getServerType().equals("own")) {
                setSshForControl(db);
            } else {
                dockerControl.setSshMakerAndServerType(null,"company");
            }
            dockerControl.setAppName(db.get().getName(),client.get().getLogin(),"db");
            dataBaseRep.delete(db.get());
            dockerControl.deleteContainer();
            return true;
        }
        return false;
    }

    @Override
    public List<DataBase> getDataBaseList(String login) {
        List<DataBase> dataBaseList;
        Optional<Client> client = clientRep.getClientByLogin(login);
        if (client.isPresent()) {
            dataBaseList = dataBaseRep.getAllByClientsId(client.get().get_id());
            return dataBaseList;
        }
        return null;
    }
}
