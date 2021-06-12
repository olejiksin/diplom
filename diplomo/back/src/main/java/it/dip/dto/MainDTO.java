package it.dip.dto;

import it.dip.models.Microservice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainDTO {
    List<AppDTO> apps;
    List<DataBaseDTOtoFt> dataBases;
    List<Microservice> microservices;
}
