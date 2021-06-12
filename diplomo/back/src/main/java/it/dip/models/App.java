package it.dip.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apps")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class App {

    @Id
    private String _id;

    @Indexed
    private String appType;
    private String name;
    private String link; //git
    private String status;  //offline online
    private String ip;
    private String port;
    private String clientsId;
    private String serverType;
    private String appLang;
}
