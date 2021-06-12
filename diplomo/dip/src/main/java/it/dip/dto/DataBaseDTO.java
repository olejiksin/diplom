package it.dip.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseDTO {
    private String serverType;
    private String dbType; //mongo etc
    private String port;
    private String ip;
    private String status;
    private String user;
    private String pas;
    private String name;
}
