package it.dip.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "clients")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Client {

    @Id
    private String _id;

    @Indexed
//    private String email;
    private String login;
    private String hashPas;
//    @DBRef
//    private List<Server> serverList;
//    @DBRef
//    private List<App> appsList;
}
