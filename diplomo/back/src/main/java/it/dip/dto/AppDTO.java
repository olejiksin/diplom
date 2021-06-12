package it.dip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppDTO {
    private String name;
    private String link;
    private String status;
    private String ip;
    private String port;
    private String id;
    private String appType;
    private String appLang;
}
