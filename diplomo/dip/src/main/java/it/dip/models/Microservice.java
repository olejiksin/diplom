package it.dip.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "microservices")
@Builder
public class Microservice {

    @Id
    private String _id;

    @Indexed
    private String name;
    private String link;
    //    private String ip; // masterIp
    private String status;
    private String appType;
    private List<SubMicroApp> subNodes;
    private String clientId;

}
