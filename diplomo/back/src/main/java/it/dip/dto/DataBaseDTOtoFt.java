package it.dip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataBaseDTOtoFt {
    private String dbType;
    private String port;
    private String ip;
    private String status;
}
