package it.dip.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SubMicroApp {
    private String appLang;
    private String port;
    private String ip;
    private String status;
    private String name;
}
