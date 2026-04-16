package br.com.fiap.ms_pedidos.dto;

import br.com.fiap.ms_pedidos.entities.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StatusDTO {
    private Status status;
}
