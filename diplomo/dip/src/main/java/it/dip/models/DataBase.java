package it.dip.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "databases")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBase {
    @Id
    private String _id;
    @Indexed
    private String serverType;
    private String dbType; //mongo etc
    private String port;
    private String ip;
    private String status;
    private String clientsId;
    private String name;
}
