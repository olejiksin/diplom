package it.dip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FromFrontAppDTO {

    private String name;
    private String link;
    private String status;
    private String ip;
    private String port;
    private String id;
    private String appLang;
    private String appType; //mono/micro
    private String serverType; // own/company

    private String login;
    private String password;
}
