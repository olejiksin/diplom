package it.dip.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "servers")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Server {

    @Id
    private String _id;

    @Indexed
    private String ip;
    private String login;
    private String password;
    private String appsId;
}
